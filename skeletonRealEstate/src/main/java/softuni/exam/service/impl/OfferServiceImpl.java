package softuni.exam.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.ImportOfferDTO;
import softuni.exam.models.dto.OfferExportDTO;
import softuni.exam.models.dto.OfferWrapperDTO;
import softuni.exam.models.entity.Agent;
import softuni.exam.models.entity.Apartment;
import softuni.exam.models.entity.ApartmentType;
import softuni.exam.models.entity.Offer;
import softuni.exam.repository.AgentRepository;
import softuni.exam.repository.ApartmentRepository;
import softuni.exam.repository.OfferRepository;
import softuni.exam.service.OfferService;
import softuni.exam.util.ValidationUtils;
import softuni.exam.util.XmlParser;


import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static softuni.exam.util.Constants.*;

@Service
public class OfferServiceImpl implements OfferService {
    private final OfferRepository offerRepository;
    private final ValidationUtils validationUtils;
    private final XmlParser xmlParser;
    private final ModelMapper modelMapper;
    private final AgentRepository agentRepository;
    private final ApartmentRepository apartmentRepository;

    public OfferServiceImpl(OfferRepository offerRepository, ValidationUtils validationUtils, XmlParser xmlParser, ModelMapper modelMapper, AgentRepository agentRepository, ApartmentRepository apartmentRepository) {
        this.offerRepository = offerRepository;
        this.validationUtils = validationUtils;
        this.xmlParser = xmlParser;
        this.modelMapper = modelMapper;
        this.agentRepository = agentRepository;
        this.apartmentRepository = apartmentRepository;
    }

    @Override
    public boolean areImported() {
        return this.offerRepository.count()>0;
    }

    @Override
    public String readOffersFileContent() throws IOException {
        return Files.readString(Path.of(OFFERS_FILE_PATH));
    }

    @Override
    public String importOffers() throws IOException, JAXBException {
        StringBuilder sb = new StringBuilder();
        List<ImportOfferDTO> offerDTOList = xmlParser.fromFile(Path.of(OFFERS_FILE_PATH).toFile(), OfferWrapperDTO.class)
                .getOffers();

        for(ImportOfferDTO importOfferDTO : offerDTOList){
            sb.append(System.lineSeparator());
            Optional<Agent> agent = this.agentRepository.findFirstByFirstName(importOfferDTO.getAgent().getName());
            Apartment apartment = this.apartmentRepository.findFirstById(importOfferDTO.getApartment().getId());
            if(agent.isEmpty() || !validationUtils.isValid(importOfferDTO)){
                sb.append(String.format(INVALID_FORMAT, OFFER));
            }
            else{
                int day = Integer.parseInt(importOfferDTO.getPublishedOn().split("/")[0]);
                int month = Integer.parseInt(importOfferDTO.getPublishedOn().split("/")[1]);
                int year = Integer.parseInt(importOfferDTO.getPublishedOn().split("/")[2]);
                Offer offerToSave = modelMapper.map(importOfferDTO, Offer.class);
                offerToSave.setAgent(agent.get());
                offerToSave.setApartment(apartment);
                offerToSave.setPublishedOn(LocalDate.of(year, month, day));

                this.offerRepository.save(offerToSave);
                sb.append(String.format(SUCCESSFUL_OFFER_IMPORT_FORMAT, offerToSave.getPrice().setScale(2)));
            }
        }
        return sb.toString();
    }

    @Override
    public String exportOffers() {
        StringBuilder sb = new StringBuilder();
        List<Offer> offersToExport = this.offerRepository.findAllByApartmentApartmentTypeOrderByApartmentAreaDescPriceAsc(ApartmentType.three_rooms);
        for (Offer offer : offersToExport){
            OfferExportDTO offerExportDTO = modelMapper.map(offer, OfferExportDTO.class);
            sb.append(offerExportDTO.toString());
        }
        return sb.toString();
    }
}

package softuni.exam.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.ApartmentImportDTO;
import softuni.exam.models.dto.ApartmentWrapperDTO;
import softuni.exam.models.entity.Apartment;
import softuni.exam.models.entity.Town;
import softuni.exam.repository.ApartmentRepository;
import softuni.exam.repository.TownRepository;
import softuni.exam.service.ApartmentService;
import softuni.exam.util.ValidationUtils;
import softuni.exam.util.XmlParser;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static softuni.exam.util.Constants.*;

@Service
public class ApartmentServiceImpl implements ApartmentService {
    private final ApartmentRepository apartmentRepository;
    private final TownRepository townRepository;
    private final ValidationUtils validationUtils;
    private final ModelMapper modelMapper;
    private final XmlParser xmlParser;

    public ApartmentServiceImpl(ApartmentRepository apartmentRepository, TownRepository townRepository, ValidationUtils validationUtils, ModelMapper modelMapper, XmlParser xmlParser) {
        this.apartmentRepository = apartmentRepository;
        this.townRepository = townRepository;
        this.validationUtils = validationUtils;
        this.modelMapper = modelMapper;
        this.xmlParser = xmlParser;
    }

    @Override
    public boolean areImported() {
        return this.apartmentRepository.count()>0;
    }

    @Override
    public String readApartmentsFromFile() throws IOException {
        return Files.readString(Path.of(APARTMENTS_FILE_PATH));
    }

    @Override
    public String importApartments() throws IOException, JAXBException {
        StringBuilder sb = new StringBuilder();
        List<ApartmentImportDTO> importDTOS = this.xmlParser.fromFile(Path.of(APARTMENTS_FILE_PATH).toFile(), ApartmentWrapperDTO.class)
                .getApartmentImportDTOS();
        for (ApartmentImportDTO apartmentImportDTO : importDTOS){
            sb.append(System.lineSeparator());

            if (this.apartmentRepository.findFirstByTownTownNameAndArea(apartmentImportDTO.getTown(), apartmentImportDTO.getArea()).isPresent() || !validationUtils.isValid(apartmentImportDTO)){
                sb.append(String.format(INVALID_FORMAT, APARTMENT));
            }
            else{
                Apartment apartmentToSave = modelMapper.map(apartmentImportDTO, Apartment.class);
                Optional<Town> town = this.townRepository.findFirstByTownName(apartmentImportDTO.getTown());
                if (town.isPresent()){
                    apartmentToSave.setTown(town.get());
                    this.apartmentRepository.save(apartmentToSave);
                    sb.append(String.format(SUCCESSFUL_APARTMENT_IMPORT_FORMAT, apartmentToSave.getApartmentType().toString(), apartmentToSave.getArea()));
                }
            }
        }
        return sb.toString();
    }
}

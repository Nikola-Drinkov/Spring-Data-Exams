package com.example.football.service.impl;

import com.example.football.models.dto.StatImportDTO;
import com.example.football.models.dto.StatsWrapperDTO;
import com.example.football.models.entity.Stat;
import com.example.football.repository.StatRepository;
import com.example.football.service.StatService;
import com.example.football.util.ValidationUtils;
import com.example.football.util.XmlParser;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static com.example.football.util.Constants.*;

//ToDo - Implement all methods
@Service
public class StatServiceImpl implements StatService {
    private final StatRepository statRepository;
    private final ValidationUtils validationUtils;
    private final ModelMapper modelMapper;
    private final XmlParser xmlParser;

    public StatServiceImpl(StatRepository statRepository, ValidationUtils validationUtils, ModelMapper modelMapper, XmlParser xmlParser) {
        this.statRepository = statRepository;
        this.validationUtils = validationUtils;
        this.modelMapper = modelMapper;
        this.xmlParser = xmlParser;
    }


    @Override
    public boolean areImported() {
        return this.statRepository.count()>0;
    }

    @Override
    public String readStatsFileContent() throws IOException {
        return Files.readString(Path.of(STATS_FILE_PATH));
    }

    @Override
    public String importStats() throws JAXBException, FileNotFoundException {
        List<StatImportDTO> statImportDTOS = xmlParser.fromFile(Path.of(STATS_FILE_PATH).toFile(), StatsWrapperDTO.class).getStatImportDTOS();
        StringBuilder sb = new StringBuilder();

        for(StatImportDTO statImportDTO : statImportDTOS){
            sb.append(System.lineSeparator());
            Optional<Stat> statOptional = this.statRepository.findFirstByPassingAndShootingAndEndurance(statImportDTO.getPassing(), statImportDTO.getShooting(), statImportDTO.getEndurance());

            if(statOptional.isPresent() || !this.validationUtils.isValid(statImportDTO)){
                sb.append(String.format( INVALID_FORMAT, STAT));
            }
            else{
                Stat statToSave = modelMapper.map(statImportDTO, Stat.class);
                this.statRepository.save(statToSave);
                sb.append(String.format(SUCCESSFUL_STAT_FORMAT, statToSave.getShooting(), statToSave.getPassing(), statToSave.getEndurance()));
            }
        }
        return sb.toString();
    }
}

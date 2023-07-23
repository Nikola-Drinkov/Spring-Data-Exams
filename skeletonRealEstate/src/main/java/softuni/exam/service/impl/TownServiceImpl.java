package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.ImportTownDTO;
import softuni.exam.models.entity.Town;
import softuni.exam.repository.TownRepository;
import softuni.exam.service.TownService;
import softuni.exam.util.ValidationUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static softuni.exam.util.Constants.*;

@Service
public class TownServiceImpl implements TownService {
    private final TownRepository townRepository;
    private final Gson gson;
    private final ValidationUtils validationUtils;
    private final ModelMapper modelMapper;

    public TownServiceImpl(TownRepository townRepository, Gson gson, ValidationUtils validationUtils, ModelMapper modelMapper) {
        this.townRepository = townRepository;
        this.gson = gson;
        this.validationUtils = validationUtils;
        this.modelMapper = modelMapper;
    }


    @Override
    public boolean areImported() {
        return this.townRepository.count()>0;
    }

    @Override
    public String readTownsFileContent() throws IOException {
        return Files.readString(Path.of(TOWNS_FILE_PATH));
    }

    @Override
    public String importTowns() throws IOException {
        StringBuilder sb = new StringBuilder();
        List<ImportTownDTO> townDTOS = Arrays.stream(gson.fromJson(readTownsFileContent(), ImportTownDTO[].class)).collect(Collectors.toList());
        for(ImportTownDTO townDTO : townDTOS){
            if(!validationUtils.isValid(townDTO)){
                sb.append(String.format(INVALID_FORMAT, TOWN));
            }
            else{
                Town townToSave = modelMapper.map(townDTO, Town.class);
                this.townRepository.save(townToSave);
                sb.append(String.format(SUCCESSFUL_TOWN_IMPORT_FORMAT,townToSave.getTownName(), townToSave.getPopulation()));
            }
        }
        return sb.toString();
    }
}

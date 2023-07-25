package com.example.football.service.impl;

import com.example.football.models.dto.TownImportDTO;
import com.example.football.models.entity.Town;
import com.example.football.repository.TownRepository;
import com.example.football.service.TownService;
import com.example.football.util.ValidationUtils;
import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.football.util.Constants.*;


//ToDo - Implement all methods
@Service
public class TownServiceImpl implements TownService {
    private final TownRepository townRepository;
    private final Gson gson;
    private final ModelMapper modelMapper;
    private final ValidationUtils validationUtils;

    public TownServiceImpl(TownRepository townRepository, Gson gson, ModelMapper modelMapper, ValidationUtils validationUtils) {
        this.townRepository = townRepository;
        this.gson = gson;
        this.modelMapper = modelMapper;
        this.validationUtils = validationUtils;
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
        List<TownImportDTO> townImportDTOS = Arrays.stream(gson.fromJson(readTownsFileContent(), TownImportDTO[].class)).collect(Collectors.toList());
        StringBuilder sb = new StringBuilder();
        for(TownImportDTO townImportDTO : townImportDTOS){
            sb.append(System.lineSeparator());
            Optional<Town> town = this.townRepository.findFirstByName(townImportDTO.getName());

            if(town.isPresent() || !validationUtils.isValid(townImportDTO)){
                sb.append(String.format(INVALID_FORMAT, TOWN));
            }
            else{
                Town townToSave = modelMapper.map(townImportDTO, Town.class);
                this.townRepository.save(townToSave);
                sb.append(String.format(SUCCESSFUL_TOWN_FORMAT, townToSave.getName(), townToSave.getPopulation()));
            }

        }
        return sb.toString();
    }
}

package com.example.football.service.impl;

import com.example.football.models.dto.TeamImportDTO;
import com.example.football.models.entity.Team;
import com.example.football.models.entity.Town;
import com.example.football.repository.TeamRepository;
import com.example.football.repository.TownRepository;
import com.example.football.service.TeamService;
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

@Service
public class TeamServiceImpl implements TeamService {
    private final TeamRepository teamRepository;
    private final TownRepository townRepository;
    private final Gson gson;
    private final ModelMapper modelMapper;
    private final ValidationUtils validationUtils;

    public TeamServiceImpl(TeamRepository teamRepository, TownRepository townRepository, Gson gson, ModelMapper modelMapper, ValidationUtils validationUtils) {
        this.teamRepository = teamRepository;
        this.townRepository = townRepository;
        this.gson = gson;
        this.modelMapper = modelMapper;
        this.validationUtils = validationUtils;
    }

    @Override
    public boolean areImported() {
        return this.teamRepository.count()>0;
    }

    @Override
    public String readTeamsFileContent() throws IOException {
        return Files.readString(Path.of(TEAMS_FILE_PATH));
    }

    @Override
    public String importTeams() throws IOException {
        List<TeamImportDTO> teamImportDTOS = Arrays.stream(gson.fromJson(readTeamsFileContent(), TeamImportDTO[].class)).collect(Collectors.toList());
        StringBuilder sb = new StringBuilder();
        for(TeamImportDTO teamImportDTO : teamImportDTOS){
            sb.append(System.lineSeparator());
            Optional<Team> team = this.teamRepository.findFirstByName(teamImportDTO.getName());

            if(team.isPresent() || !validationUtils.isValid(teamImportDTO)){
                sb.append(String.format(INVALID_FORMAT, TEAM));
            }
            else{
                Team teamToSave = modelMapper.map(teamImportDTO, Team.class);
                Optional<Town> townToMap = this.townRepository.findFirstByName(teamImportDTO.getTownName());
                townToMap.ifPresent(teamToSave::setTown);

                this.teamRepository.save(teamToSave);
                sb.append(String.format(SUCCESSFUL_TEAM_FORMAT, teamToSave.getName(), teamToSave.getFanBase()));
            }

        }
        return sb.toString();
    }
}

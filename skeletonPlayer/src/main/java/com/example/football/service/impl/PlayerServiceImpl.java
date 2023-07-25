package com.example.football.service.impl;

import com.example.football.models.dto.PlayerExportDTO;
import com.example.football.models.dto.PlayerImportDTO;
import com.example.football.models.dto.PlayersWrapperDTO;
import com.example.football.models.entity.*;
import com.example.football.repository.PlayerRepository;
import com.example.football.repository.StatRepository;
import com.example.football.repository.TeamRepository;
import com.example.football.repository.TownRepository;
import com.example.football.service.PlayerService;
import com.example.football.util.ValidationUtils;
import com.example.football.util.XmlParser;
import org.apache.tomcat.jni.Local;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.example.football.util.Constants.*;

//ToDo - Implement all methods
@Service
public class PlayerServiceImpl implements PlayerService {
    private final PlayerRepository playerRepository;
    private final TownRepository townRepository;
    private final TeamRepository teamRepository;
    private final StatRepository statRepository;
    private final ModelMapper modelMapper;
    private final ValidationUtils validationUtils;
    private final XmlParser xmlParser;

    public PlayerServiceImpl(PlayerRepository playerRepository, TownRepository townRepository, TeamRepository teamRepository, StatRepository statRepository, ModelMapper modelMapper, ValidationUtils validationUtils, XmlParser xmlParser) {
        this.playerRepository = playerRepository;
        this.townRepository = townRepository;
        this.teamRepository = teamRepository;
        this.statRepository = statRepository;
        this.modelMapper = modelMapper;
        this.validationUtils = validationUtils;
        this.xmlParser = xmlParser;
    }


    @Override
    public boolean areImported() {
        return this.playerRepository.count()>0;
    }

    @Override
    public String readPlayersFileContent() throws IOException {
        return Files.readString(Path.of(PLAYERS_FILE_PATH));
    }

    @Override
    public String importPlayers() throws JAXBException, FileNotFoundException {
        List<PlayerImportDTO> playerImportDTOS = xmlParser.fromFile(Path.of(PLAYERS_FILE_PATH).toFile(), PlayersWrapperDTO.class).getPlayerImportDTOList();
        StringBuilder sb = new StringBuilder();
        for (PlayerImportDTO playerImportDTO : playerImportDTOS){
            sb.append(System.lineSeparator());

            Optional<Player> playerOptional = this.playerRepository.findFirstByEmail(playerImportDTO.getEmail());
            if(playerOptional.isPresent() || !this.validationUtils.isValid(playerImportDTO)){
                sb.append(String.format(INVALID_FORMAT, PLAYER));
            }
            else if(!playerImportDTO.getPosition().equals("ATT")&&
                    !playerImportDTO.getPosition().equals("MID")&&
                    !playerImportDTO.getPosition().equals("DEF")){
                sb.append(String.format(INVALID_FORMAT, PLAYER));
            }
            else{
                Player playerToSave = modelMapper.map(playerImportDTO, Player.class);
                Optional<Town> town = this.townRepository.findFirstByName(playerImportDTO.getTown().getName());
                Optional<Team> team = this.teamRepository.findFirstByName(playerImportDTO.getTeam().getName());
                Optional<Stat> stat = this.statRepository.findFirstById(playerImportDTO.getStat().getId());
                if(town.isPresent() && team.isPresent() && stat.isPresent()){
                    playerToSave.setTown(town.get());
                    playerToSave.setTeam(team.get());
                    playerToSave.setStat(stat.get());
                }
                playerToSave.setPosition(PositionType.valueOf(playerImportDTO.getPosition()));

                int[] dateParts = Arrays.stream(playerImportDTO.getBirthDate().split("/")).mapToInt(Integer::parseInt).toArray();
                int day = dateParts[0];
                int month = dateParts[1];
                int year = dateParts[2];
                LocalDate date = LocalDate.of(year,month,day);
                playerToSave.setBirthDate(date);

                this.playerRepository.save(playerToSave);
                sb.append(String.format(SUCCESSFUL_PLAYER_FORMAT, playerToSave.getFirstName(), playerToSave.getLastName(), playerToSave.getPosition()));
            }
        }

        return sb.toString();
    }

    @Override
    public String exportBestPlayers() {
        StringBuilder sb = new StringBuilder();

        LocalDate lower = LocalDate.of(1995, 1, 1);
        LocalDate upper = LocalDate.of(2003, 1, 1);
        List<Player> players = this.playerRepository.findAllByBirthDateBetweenOrderByStatShootingDescStatPassingDescStatEnduranceDescLastNameAsc(lower, upper);
        List<PlayerExportDTO> playersToExport = new ArrayList<>();
        players.forEach(p->{
            PlayerExportDTO playerExportDTO = modelMapper.map(p, PlayerExportDTO.class);
            playersToExport.add(playerExportDTO);
        });
        playersToExport.forEach(p->sb.append(p.toString()));

        return sb.toString();
    }
}

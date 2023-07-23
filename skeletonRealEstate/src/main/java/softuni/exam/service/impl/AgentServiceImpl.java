package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.ImportAgentDTO;
import softuni.exam.models.entity.Agent;
import softuni.exam.models.entity.Town;
import softuni.exam.repository.AgentRepository;
import softuni.exam.repository.TownRepository;
import softuni.exam.service.AgentService;
import softuni.exam.util.ValidationUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static softuni.exam.util.Constants.*;

@Service
public class AgentServiceImpl implements AgentService {
    private final AgentRepository agentRepository;
    private final TownRepository townRepository;
    private final Gson gson;
    private final ValidationUtils validationUtils;
    private final ModelMapper modelMapper;

    public AgentServiceImpl(AgentRepository agentRepository, TownRepository townRepository, Gson gson, ValidationUtils validationUtils, ModelMapper modelMapper) {
        this.agentRepository = agentRepository;
        this.townRepository = townRepository;
        this.gson = gson;
        this.validationUtils = validationUtils;
        this.modelMapper = modelMapper;
    }

    @Override
    public boolean areImported() {
        return this.agentRepository.count()>0;
    }

    @Override
    public String readAgentsFromFile() throws IOException {
        return Files.readString(Path.of(AGENTS_FILE_PATH));
    }

    @Override
    public String importAgents() throws IOException {
        StringBuilder sb = new StringBuilder();
        List<ImportAgentDTO> agentDTOS = Arrays.stream(gson.fromJson(readAgentsFromFile(), ImportAgentDTO[].class)).collect(Collectors.toList());
        for(ImportAgentDTO agentDTO : agentDTOS){
            if(this.agentRepository.findFirstByFirstName(agentDTO.getFirstName()).isPresent() || !validationUtils.isValid(agentDTO)){
                sb.append(String.format(INVALID_FORMAT, AGENT));
            }
            else{
                Agent agentToSave = modelMapper.map(agentDTO, Agent.class);
                Optional<Town> town = this.townRepository.findFirstByTownName(agentDTO.getTown());
                town.ifPresent(agentToSave::setTown);
                this.agentRepository.save(agentToSave);
                sb.append(String.format(SUCCESSFUL_AGENT_IMPORT_FORMAT,agentToSave.getFirstName(), agentToSave.getLastName()));
            }
        }
        return sb.toString();
    }
}

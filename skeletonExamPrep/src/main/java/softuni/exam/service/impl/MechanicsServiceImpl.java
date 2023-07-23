package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.MechanicImportDTO;
import softuni.exam.models.entity.Mechanic;
import softuni.exam.repository.MechanicsRepository;
import softuni.exam.service.MechanicsService;
import softuni.exam.util.ValidationUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static softuni.exam.models.Constants.*;

// TODO: Implement all methods
@Service
public class MechanicsServiceImpl implements MechanicsService {
    private MechanicsRepository mechanicsRepository;
    private final ValidationUtils validationUtils;
    private final ModelMapper modelMapper;
    private final Gson gson;

    public MechanicsServiceImpl(MechanicsRepository mechanicsRepository, ValidationUtils validationUtils, ModelMapper modelMapper, Gson gson) {
        this.mechanicsRepository = mechanicsRepository;
        this.validationUtils = validationUtils;
        this.modelMapper = modelMapper;
        this.gson = gson;
    }
    private static String MECHANICS_FILE_PATH = "src/main/resources/files/json/mechanics.json";

    @Override
    public boolean areImported() {
        return this.mechanicsRepository.count()>0;
    }

    @Override
    public String readMechanicsFromFile() throws IOException {
        return Files.readString(Path.of(MECHANICS_FILE_PATH));
    }

    @Override
    public String importMechanics() throws IOException {
        StringBuilder sb = new StringBuilder();
        List<MechanicImportDTO> mechanics = Arrays.stream(gson.fromJson(readMechanicsFromFile(), MechanicImportDTO[].class))
                .collect(Collectors.toList());

        for (MechanicImportDTO mechanic : mechanics) {
            sb.append(System.lineSeparator());
            if(this.mechanicsRepository.findFirstByEmail(mechanic.getEmail()).isPresent() || !this.validationUtils.isValid(mechanic)){
                sb.append(String.format(INVALID_FORMAT,MECHANIC));
            }
            else{
                this.mechanicsRepository.save(modelMapper.map(mechanic, Mechanic.class));
                sb.append(String.format(SUCCESSFUL_FORMAT_MECHANIC, MECHANIC, mechanic.getFirstName(), mechanic.getLastName()));
            }
        }

        return sb.toString().trim();
    }
}

package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.PartImportDTO;
import softuni.exam.models.entity.Part;
import softuni.exam.repository.PartsRepository;
import softuni.exam.service.PartsService;
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
public class PartsServiceImpl implements PartsService {
    private PartsRepository partsRepository;
    private final ValidationUtils validationUtils;
    private final Gson gson;
    private final ModelMapper modelMapper;

    public PartsServiceImpl(PartsRepository partsRepository, ValidationUtils validationUtils, Gson gson, ModelMapper modelMapper) {
        this.partsRepository = partsRepository;
        this.validationUtils = validationUtils;
        this.gson = gson;
        this.modelMapper = modelMapper;
    }
    private static String PARTS_FILE_PATH = "src/main/resources/files/json/parts.json";

    @Override
    public boolean areImported() {
        return this.partsRepository.count()>0;
    }

    @Override
    public String readPartsFileContent() throws IOException {
        return Files.readString(Path.of(PARTS_FILE_PATH));
    }

    @Override
    public String importParts() throws IOException {
        StringBuilder sb = new StringBuilder();
        List<PartImportDTO> parts = Arrays.stream(gson.fromJson(readPartsFileContent(), PartImportDTO[].class))
                .collect(Collectors.toList());

        for (PartImportDTO part : parts) {
            sb.append(System.lineSeparator());
            if(this.partsRepository.findFirstByPartName(part.getPartName()).isPresent() || !this.validationUtils.isValid(part)){
                sb.append(String.format(INVALID_FORMAT,PART));
            }
            else{
                    this.partsRepository.save(modelMapper.map(part, Part.class));
                    sb.append(String.format(SUCCESSFUL_FORMAT_PART_CAR, PART, part.getPartName(), part.getPrice()));
            }
        }

        return sb.toString().trim();
    }
}

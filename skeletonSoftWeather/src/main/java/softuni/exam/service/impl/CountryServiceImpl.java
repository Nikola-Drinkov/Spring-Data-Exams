package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.CountryImportDTO;
import softuni.exam.models.entity.Country;
import softuni.exam.repository.CountryRepository;
import softuni.exam.service.CountryService;
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
public class CountryServiceImpl implements CountryService {
    private final CountryRepository countryRepository;
    private final Gson gson;
    private final ModelMapper modelMapper;
    private final ValidationUtils validationUtils;

    public CountryServiceImpl(CountryRepository countryRepository, Gson gson, ModelMapper modelMapper, ValidationUtils validationUtils) {
        this.countryRepository = countryRepository;
        this.gson = gson;
        this.modelMapper = modelMapper;
        this.validationUtils = validationUtils;
    }

    @Override
    public boolean areImported() {
        return this.countryRepository.count()>0;
    }

    @Override
    public String readCountriesFromFile() throws IOException {
        return Files.readString(Path.of(COUNTRIES_FILE_URL));
    }

    @Override
    public String importCountries() throws IOException {
        StringBuilder sb = new StringBuilder();
        List<CountryImportDTO> countryImportDTOS = Arrays.stream(gson.fromJson(readCountriesFromFile(), CountryImportDTO[].class)).collect(Collectors.toList());
        for (CountryImportDTO countryImportDTO : countryImportDTOS){
            sb.append(System.lineSeparator());
            Optional<Country> country = this.countryRepository.findFirstByCountryName(countryImportDTO.getCountryName());
            if(country.isPresent() || !validationUtils.isValid(countryImportDTO)){
                sb.append(String.format(INVALID_FORMAT, COUNTRY));
            }
            else{
                Country countryToSave = modelMapper.map(countryImportDTO, Country.class);
                this.countryRepository.save(countryToSave);
                sb.append(String.format(COUNTRY_SUCCESSFUL_FORMAT, countryToSave.getCountryName(), countryToSave.getCurrency()));
            }
        }
        return sb.toString();
    }
}

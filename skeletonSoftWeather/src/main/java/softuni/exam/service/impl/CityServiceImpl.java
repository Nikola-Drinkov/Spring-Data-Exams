package softuni.exam.service.impl;

import com.google.gson.Gson;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.CityImportDTO;
import softuni.exam.models.entity.City;
import softuni.exam.models.entity.Country;
import softuni.exam.repository.CityRepository;
import softuni.exam.repository.CountryRepository;
import softuni.exam.service.CityService;
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
public class CityServiceImpl implements CityService {
    private final CityRepository cityRepository;
    private final CountryRepository countryRepository;
    private final Gson gson;
    private final ModelMapper modelMapper;
    private final ValidationUtils validationUtils;

    public CityServiceImpl(CityRepository cityRepository, CountryRepository countryRepository, Gson gson, ModelMapper modelMapper, ValidationUtils validationUtils) {
        this.cityRepository = cityRepository;
        this.countryRepository = countryRepository;
        this.gson = gson;
        this.modelMapper = modelMapper;
        this.validationUtils = validationUtils;
    }

    @Override
    public boolean areImported() {
        return this.cityRepository.count()>0;
    }

    @Override
    public String readCitiesFileContent() throws IOException {
        return Files.readString(Path.of(CITIES_FILE_URL));
    }

    @Override
    public String importCities() throws IOException {
        StringBuilder sb = new StringBuilder();
        List<CityImportDTO> cityImportDTOS = Arrays.stream(gson.fromJson(readCitiesFileContent(), CityImportDTO[].class)).collect(Collectors.toList());
        for (CityImportDTO cityImportDTO : cityImportDTOS){
            sb.append(System.lineSeparator());
            Optional<City> city = this.cityRepository.findFirstByCityName(cityImportDTO.getCityName());

            if(city.isPresent() || !validationUtils.isValid(cityImportDTO)){
                sb.append(String.format(INVALID_FORMAT, CITY));
            }
            else{
                City cityToSave = modelMapper.map(cityImportDTO, City.class);
                Optional<Country> countryToMap = this.countryRepository.findFirstById(cityImportDTO.getCountry());
                countryToMap.ifPresent(cityToSave::setCountry);
                this.cityRepository.save(cityToSave);
                sb.append(String.format(CITY_SUCCESSFUL_FORMAT, cityToSave.getCityName(), cityToSave.getPopulation()));
            }
        }
        return sb.toString();
    }
}

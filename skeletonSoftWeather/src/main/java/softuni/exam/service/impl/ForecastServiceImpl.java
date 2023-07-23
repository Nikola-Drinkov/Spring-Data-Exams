package softuni.exam.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.ForecastImportDTO;
import softuni.exam.models.dto.ForecastWrapperDTO;
import softuni.exam.models.dto.SundayForecastExportDTO;
import softuni.exam.models.entity.City;
import softuni.exam.models.entity.DayOfWeek;
import softuni.exam.models.entity.Forecast;
import softuni.exam.repository.CityRepository;
import softuni.exam.repository.ForecastRepository;
import softuni.exam.service.ForecastService;
import softuni.exam.util.ValidationUtils;
import softuni.exam.util.XmlParser;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static softuni.exam.util.Constants.*;

@Service
public class ForecastServiceImpl implements ForecastService {
    private final ForecastRepository forecastRepository;
    private final CityRepository cityRepository;
    private final ModelMapper modelMapper;
    private final ValidationUtils validationUtils;
    private final XmlParser xmlParser;

    public ForecastServiceImpl(ForecastRepository forecastRepository, CityRepository cityRepository, ModelMapper modelMapper, ValidationUtils validationUtils, XmlParser xmlParser) {
        this.forecastRepository = forecastRepository;
        this.cityRepository = cityRepository;
        this.modelMapper = modelMapper;
        this.validationUtils = validationUtils;
        this.xmlParser = xmlParser;
    }

    @Override
    public boolean areImported() {
        return this.forecastRepository.count()>0;
    }

    @Override
    public String readForecastsFromFile() throws IOException {
        return Files.readString(Path.of(FORECASTS_FILE_URL));
    }

    @Override
    public String importForecasts() throws IOException, JAXBException {
        List<ForecastImportDTO> forecastImportDTOS = xmlParser.fromFile(Path.of(FORECASTS_FILE_URL).toFile(), ForecastWrapperDTO.class).getForecastImportDTOS();
        StringBuilder sb = new StringBuilder();
        for(ForecastImportDTO forecastImportDTO : forecastImportDTOS){
            sb.append(System.lineSeparator());
            boolean isValidDay = false;
            if(forecastImportDTO.getDayOfWeek().equals("FRIDAY")||forecastImportDTO.getDayOfWeek().equals("SATURDAY")||forecastImportDTO.getDayOfWeek().equals("SUNDAY")) isValidDay = true;

            if(!validationUtils.isValid(forecastImportDTO) || !isValidDay){
                sb.append(String.format(INVALID_FORMAT, FORECAST));
                continue;
            }

            Optional<City> city = this.cityRepository.findFirstById(forecastImportDTO.getCity());
            Optional<Forecast> forecast = Optional.empty();
            if(city.isPresent()) {
             forecast = this.forecastRepository.findFirstByDayOfWeekAndCityCityName(DayOfWeek.valueOf(forecastImportDTO.getDayOfWeek()), city.get().getCityName());
            }
            if(forecast.isPresent()){
                sb.append(String.format(INVALID_FORMAT, FORECAST));
            }
            else{
                Forecast forecastToSave = modelMapper.map(forecastImportDTO, Forecast.class);
                city.ifPresent(forecastToSave::setCity);
                int riseHour = Integer.parseInt(forecastImportDTO.getSunrise().split(":")[0]);
                int riseMin = Integer.parseInt(forecastImportDTO.getSunrise().split(":")[1]);
                int riseSec = Integer.parseInt(forecastImportDTO.getSunrise().split(":")[2]);

                int setHour = Integer.parseInt(forecastImportDTO.getSunset().split(":")[0]);
                int setMin = Integer.parseInt(forecastImportDTO.getSunset().split(":")[1]);
                int setSec = Integer.parseInt(forecastImportDTO.getSunset().split(":")[2]);
                LocalTime riseTime = LocalTime.of(riseHour,riseMin,riseSec);
                LocalTime setTime = LocalTime.of(setHour,setMin,setSec);

                forecastToSave.setSunrise(riseTime);
                forecastToSave.setSunset(setTime);
                forecastToSave.setDayOfWeek(DayOfWeek.valueOf(forecastImportDTO.getDayOfWeek()));

                this.forecastRepository.save(forecastToSave);
                sb.append(String.format(FORECAST_SUCCESSFUL_FORMAT, forecastImportDTO.getDayOfWeek(), forecastImportDTO.getMaxTemperature()));
            }
        }

        return sb.toString();
    }

    @Override
    public String exportForecasts() {
        StringBuilder sb = new StringBuilder();
        List<Forecast> forecastsToExport = this.forecastRepository.findAllByDayOfWeekAndCityPopulationLessThanOrderByMaxTemperatureDescIdAsc(DayOfWeek.SUNDAY, 150000);
        List<SundayForecastExportDTO> forecastExportDTOS = new ArrayList<>();
        for(Forecast forecast : forecastsToExport){
            SundayForecastExportDTO forecastExportDTO = modelMapper.map(forecast, SundayForecastExportDTO.class);
            forecastExportDTOS.add(forecastExportDTO);
        }
        for(SundayForecastExportDTO sundayForecastExportDTO : forecastExportDTOS){
            sb.append(sundayForecastExportDTO.toString());
        }
        return sb.toString();
    }
}

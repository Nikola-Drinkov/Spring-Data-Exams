package softuni.exam.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.CarImportDTO;
import softuni.exam.models.dto.CarsWrapperDTO;
import softuni.exam.models.entity.Car;
import softuni.exam.repository.CarsRepository;
import softuni.exam.service.CarsService;
import softuni.exam.util.ValidationUtils;
import softuni.exam.util.XmlParser;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static softuni.exam.models.Constants.*;

// TODO: Implement all methods
@Service
public class CarsServiceImpl implements CarsService {
    private CarsRepository carsRepository;
    private final ModelMapper modelMapper;
    private final XmlParser xmlParser;
    private final ValidationUtils validationUtils;

    public CarsServiceImpl(CarsRepository carsRepository, ModelMapper modelMapper, XmlParser xmlParser, ValidationUtils validationUtils) {
        this.carsRepository = carsRepository;
        this.modelMapper = modelMapper;
        this.xmlParser = xmlParser;
        this.validationUtils = validationUtils;
    }

    private static String CARS_FILE_PATH = "src/main/resources/files/xml/cars.xml";

    @Override
    public boolean areImported() {
        return this.carsRepository.count()>0;
    }

    @Override
    public String readCarsFromFile() throws IOException {
        return Files.readString(Path.of(CARS_FILE_PATH));
    }

    @Override
    public String importCars() throws IOException, JAXBException {
        StringBuilder sb = new StringBuilder();
        List<CarImportDTO> cars= this.xmlParser.fromFile(Path.of(CARS_FILE_PATH).toFile(), CarsWrapperDTO.class)
                .getCars();
        for(CarImportDTO car : cars){
            sb.append(System.lineSeparator());
            if(this.carsRepository.findFirstByPlateNumber(car.getPlateNumber()).isPresent() ||  !this.validationUtils.isValid(car)){
                sb.append(String.format(INVALID_FORMAT, CAR));
            }
            else{
                this.carsRepository.save(modelMapper.map(car, Car.class));
                sb.append(String.format(SUCCESSFUL_FORMAT_PART_CAR, CAR, car.getCarMake(), car.getCarModel()));
            }
        }
        return sb.toString().trim();
    }
}

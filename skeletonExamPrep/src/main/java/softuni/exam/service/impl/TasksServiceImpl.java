package softuni.exam.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import softuni.exam.models.dto.TaskCoupeExportDTO;
import softuni.exam.models.dto.TaskImportDTO;
import softuni.exam.models.dto.TaskWrapperDTO;
import softuni.exam.models.entity.*;
import softuni.exam.repository.CarsRepository;
import softuni.exam.repository.MechanicsRepository;
import softuni.exam.repository.PartsRepository;
import softuni.exam.repository.TasksRepository;
import softuni.exam.service.TasksService;
import softuni.exam.util.ValidationUtils;
import softuni.exam.util.XmlParser;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static softuni.exam.models.Constants.*;

// TODO: Implement all methods
@Service
public class TasksServiceImpl implements TasksService {
    private TasksRepository tasksRepository;
    private final CarsRepository carsRepository;
    private final MechanicsRepository mechanicsRepository;
    private final PartsRepository partsRepository;
    private final ModelMapper modelMapper;
    private final XmlParser xmlParser;
    private final ValidationUtils validationUtils;

    public TasksServiceImpl(TasksRepository tasksRepository, CarsRepository carsRepository, MechanicsRepository mechanicsRepository, PartsRepository partsRepository, ModelMapper modelMapper, XmlParser xmlParser, ValidationUtils validationUtils) {
        this.tasksRepository = tasksRepository;
        this.carsRepository = carsRepository;
        this.mechanicsRepository = mechanicsRepository;
        this.partsRepository = partsRepository;
        this.modelMapper = modelMapper;
        this.xmlParser = xmlParser;
        this.validationUtils = validationUtils;
    }

    private static String TASKS_FILE_PATH = "src/main/resources/files/xml/tasks.xml";

    @Override
    public boolean areImported() {
        return this.tasksRepository.count()>0;
    }

    @Override
    public String readTasksFileContent() throws IOException {
        return Files.readString(Path.of(TASKS_FILE_PATH));
    }

    @Override
    public String importTasks() throws IOException, JAXBException {
        StringBuilder sb = new StringBuilder();
        List<TaskImportDTO> tasks = this.xmlParser.fromFile(Path.of(TASKS_FILE_PATH).toFile(), TaskWrapperDTO.class)
                .getTasks();
        for(TaskImportDTO task : tasks) {
            sb.append(System.lineSeparator());
            if (this.validationUtils.isValid(task)) {
                Optional<Mechanic> mechanic = this.mechanicsRepository.findFirstByFirstName(task.getMechanic().getFirstName());
                Optional<Car> car = this.carsRepository.findFirstById(task.getCar().getId());
                Optional<Part> part = this.partsRepository.findById(task.getPart().getId());

                if (mechanic.isEmpty() || car.isEmpty() || part.isEmpty()) {
                    sb.append(String.format(INVALID_FORMAT, TASK));
                } else {
                    Task taskToSave = this.modelMapper.map(task, Task.class);
                    taskToSave.setCar(car.get());
                    taskToSave.setMechanic(mechanic.get());
                    taskToSave.setPart(part.get());
                    this.tasksRepository.save(taskToSave);
                    sb.append(String.format(SUCCESSFUL_FORMAT_TASK, TASK, task.getPrice().setScale(2)));
                }
            } else  sb.append(String.format(INVALID_FORMAT, TASK));
        }
        return sb.toString().trim();
    }

    @Override
    public String getCoupeCarTasksOrderByPrice() {
        StringBuilder sb = new StringBuilder();
        List<Task> tasks = this.tasksRepository.findAllByCarCarTypeOrderByPriceDesc(CarTypes.coupe);
        List<TaskCoupeExportDTO> tasksToExport = new ArrayList<>();
        for(Task task : tasks){
            TaskCoupeExportDTO taskCoupeExportDTO = modelMapper.map(task, TaskCoupeExportDTO.class);
            tasksToExport.add(taskCoupeExportDTO);
        }
        tasksToExport.forEach(t->sb.append(t.toString()));
        return sb.toString();
    }
}

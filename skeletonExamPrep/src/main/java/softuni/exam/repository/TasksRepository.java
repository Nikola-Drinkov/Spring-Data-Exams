package softuni.exam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import softuni.exam.models.entity.CarTypes;
import softuni.exam.models.entity.Task;

import java.util.List;
import java.util.Optional;

// TODO:
@Repository
public interface TasksRepository extends JpaRepository<Task, Long> {
    List<Task> findAllByCarCarTypeOrderByPriceDesc (CarTypes carType);
}

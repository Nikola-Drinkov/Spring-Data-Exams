package softuni.exam.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import softuni.exam.models.entity.Car;

import java.util.Optional;

// TODO:
@Repository
public interface CarsRepository extends JpaRepository<Car, Long> {
    Optional<Car> findFirstById(Long id);
    Optional<Car> findFirstByPlateNumber(String plateNumber);
}

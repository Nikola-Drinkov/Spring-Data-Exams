package softuni.exam.models.dto;

import softuni.exam.models.entity.Car;
import softuni.exam.models.entity.Mechanic;

import java.math.BigDecimal;

public class TaskCoupeExportDTO {
    private CarBasicDTO car;
    private MechanicBasicDTO mechanic;
    private String id;
    private BigDecimal price;

    public CarBasicDTO getCar() {
        return car;
    }

    public void setCar(CarBasicDTO car) {
        this.car = car;
    }

    public MechanicBasicDTO getMechanic() {
        return mechanic;
    }

    public void setMechanic(MechanicBasicDTO mechanic) {
        this.mechanic = mechanic;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Car " + car.getCarMake() + " " + car.getCarModel() + " with " + car.getKilometers() + "km" + System.lineSeparator() +
                "-Mechanic: " + mechanic.getFirstName() + " " + mechanic.getLastName() + " - task №" + id + ":" + System.lineSeparator() +
                " --Engine: " + car.getEngine() + System.lineSeparator() +
                "---Price: " + price.setScale(2) + "$" + System.lineSeparator();
    }
}

package softuni.exam.models.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.util.Date;

@XmlRootElement(name = "task")
@XmlAccessorType(XmlAccessType.FIELD)
public class TaskImportDTO {
    @NotNull
    @Positive
    @XmlElement
    private BigDecimal price;
    @NotNull
    @XmlElement
    private String date;
    @NotNull
    @XmlElement
    private CarId car;
    @NotNull
    @XmlElement
    private MechanicFirstName mechanic;
    @NotNull
    @XmlElement
    private PartId part;

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public CarId getCar() {
        return car;
    }

    public void setCar(CarId car) {
        this.car = car;
    }

    public MechanicFirstName getMechanic() {
        return mechanic;
    }

    public void setMechanic(MechanicFirstName mechanic) {
        this.mechanic = mechanic;
    }

    public PartId getPart() {
        return part;
    }

    public void setPart(PartId part) {
        this.part = part;
    }
}

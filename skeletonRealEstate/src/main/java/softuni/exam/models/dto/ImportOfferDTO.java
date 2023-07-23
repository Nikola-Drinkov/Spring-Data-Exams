package softuni.exam.models.dto;

import softuni.exam.models.entity.Agent;
import softuni.exam.models.entity.Apartment;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.time.LocalDate;

@XmlRootElement(name = "offer")
@XmlAccessorType(XmlAccessType.FIELD)
public class ImportOfferDTO {
    @NotNull
    @Positive
    private BigDecimal price;

    @NotNull
    private AgentNameDTO agent;
    @NotNull
    private ApartmentIdDTO apartment;
    @NotNull
    private String publishedOn;

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public AgentNameDTO getAgent() {
        return agent;
    }

    public void setAgent(AgentNameDTO agent) {
        this.agent = agent;
    }

    public ApartmentIdDTO getApartment() {
        return apartment;
    }

    public void setApartment(ApartmentIdDTO apartment) {
        this.apartment = apartment;
    }

    public String getPublishedOn() {
        return publishedOn;
    }

    public void setPublishedOn(String publishedOn) {
        this.publishedOn = publishedOn;
    }
}

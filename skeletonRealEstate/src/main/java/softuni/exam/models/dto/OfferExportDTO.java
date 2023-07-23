package softuni.exam.models.dto;

import softuni.exam.models.entity.Agent;
import softuni.exam.models.entity.Apartment;

import java.math.BigDecimal;

public class OfferExportDTO {
    private AgentBasicInfo agent;
    private Long id;
    private ApartmentBasicInfo apartment;
    private BigDecimal price;

    public AgentBasicInfo getAgent() {
        return agent;
    }

    public void setAgent(AgentBasicInfo agent) {
        this.agent = agent;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ApartmentBasicInfo getApartment() {
        return apartment;
    }

    public void setApartment(ApartmentBasicInfo apartment) {
        this.apartment = apartment;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return String.format("Agent %s %s with offer №%d:\n" +
                "   \t\t-Apartment area: %.2f\n" +
                "   \t\t--Town: %s\n" +
                "   \t\t---Price: %s$\n",
                agent.getFirstName(),
                agent.getLastName(),
                this.id,
                apartment.getArea(),
                apartment.getTown().getTownName(),
                this.getPrice().setScale(2));
    }
}

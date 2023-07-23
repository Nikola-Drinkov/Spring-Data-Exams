package softuni.exam.models.dto;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "offers")
@XmlAccessorType(XmlAccessType.FIELD)
public class OfferWrapperDTO {
    @XmlElement(name = "offer")
    @NotNull
    private List<ImportOfferDTO> offers;

    public List<ImportOfferDTO> getOffers() {
        return offers;
    }

    public void setOffers(List<ImportOfferDTO> offers) {
        this.offers = offers;
    }
}

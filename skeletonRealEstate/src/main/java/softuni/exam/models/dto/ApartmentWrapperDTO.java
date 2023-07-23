package softuni.exam.models.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "apartments")
@XmlAccessorType(XmlAccessType.FIELD)
public class ApartmentWrapperDTO {
    @XmlElement(name = "apartment")
    List<ApartmentImportDTO> apartmentImportDTOS;

    public List<ApartmentImportDTO> getApartmentImportDTOS() {
        return apartmentImportDTOS;
    }

    public void setApartmentImportDTOS(List<ApartmentImportDTO> apartmentImportDTOS) {
        this.apartmentImportDTOS = apartmentImportDTOS;
    }
}

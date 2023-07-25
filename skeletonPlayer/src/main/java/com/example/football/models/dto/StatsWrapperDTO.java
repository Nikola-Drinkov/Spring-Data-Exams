package com.example.football.models.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "stats")
@XmlAccessorType(XmlAccessType.FIELD)
public class StatsWrapperDTO {

    @XmlElement(name = "stat")
    private List<StatImportDTO> statImportDTOS;

    public List<StatImportDTO> getStatImportDTOS() {
        return statImportDTOS;
    }

    public void setStatImportDTOS(List<StatImportDTO> statImportDTOS) {
        this.statImportDTOS = statImportDTOS;
    }
}

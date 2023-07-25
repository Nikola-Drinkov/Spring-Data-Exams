package com.example.football.models.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "players")
@XmlAccessorType(XmlAccessType.FIELD)
public class PlayersWrapperDTO {

    @XmlElement(name = "player")
    private List<PlayerImportDTO> playerImportDTOList;

    public List<PlayerImportDTO> getPlayerImportDTOList() {
        return playerImportDTOList;
    }

    public void setPlayerImportDTOList(List<PlayerImportDTO> playerImportDTOList) {
        this.playerImportDTOList = playerImportDTOList;
    }
}

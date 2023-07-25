package com.example.football.models.dto;

import com.example.football.models.entity.PositionType;
import com.example.football.models.entity.Team;

public class PlayerExportDTO {
    private String firstName;
    private String lastName;
    private PositionType position;
    private TeamBasicDTO team;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public PositionType getPosition() {
        return position;
    }

    public void setPosition(PositionType position) {
        this.position = position;
    }

    public TeamBasicDTO getTeam() {
        return team;
    }

    public void setTeam(TeamBasicDTO team) {
        this.team = team;
    }

    @Override
    public String toString() {
        return String.format("Player - %s %s\n" +
                "\tPosition - %s\n" +
                "\tTeam - %s\n" +
                "\tStadium - %s\n",
                firstName,
                lastName,
                position.toString(),
                team.getName(),
                team.getStadiumName());
    }
}

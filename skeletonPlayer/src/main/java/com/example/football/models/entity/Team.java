package com.example.football.models.entity;

import javax.persistence.*;

@Entity
@Table(name = "teams")
public class Team extends BaseEntity{
    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, name = "stadium_name")
    private String stadiumName;

    @Column(name = "fan_base", nullable = false)
    private Integer fanBase;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String history;

    @ManyToOne
    @JoinColumn(name = "town_id")
    private Town town;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStadiumName() {
        return stadiumName;
    }

    public void setStadiumName(String stadiumName) {
        this.stadiumName = stadiumName;
    }

    public Integer getFanBase() {
        return fanBase;
    }

    public void setFanBase(Integer fanBase) {
        this.fanBase = fanBase;
    }

    public String getHistory() {
        return history;
    }

    public void setHistory(String history) {
        this.history = history;
    }

    public Town getTown() {
        return town;
    }

    public void setTown(Town town) {
        this.town = town;
    }
}

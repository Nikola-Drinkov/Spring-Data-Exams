package softuni.exam.models.dto;

import softuni.exam.models.entity.Town;

public class ApartmentBasicInfo {
    private Double area;
    private TownBasicInfo town;

    public Double getArea() {
        return area;
    }

    public void setArea(Double area) {
        this.area = area;
    }

    public TownBasicInfo getTown() {
        return town;
    }

    public void setTown(TownBasicInfo town) {
        this.town = town;
    }
}

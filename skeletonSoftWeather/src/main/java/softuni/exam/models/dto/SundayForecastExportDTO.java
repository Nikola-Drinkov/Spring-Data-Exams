package softuni.exam.models.dto;

import softuni.exam.models.entity.City;

import java.time.LocalTime;

public class SundayForecastExportDTO {
    private CityNameDTO city;
    private Double minTemperature;
    private Double maxTemperature;
    private LocalTime sunrise;
    private LocalTime sunset;

    public CityNameDTO getCity() {
        return city;
    }

    public void setCity(CityNameDTO city) {
        this.city = city;
    }

    public Double getMinTemperature() {
        return minTemperature;
    }

    public void setMinTemperature(Double minTemperature) {
        this.minTemperature = minTemperature;
    }

    public Double getMaxTemperature() {
        return maxTemperature;
    }

    public void setMaxTemperature(Double maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    public LocalTime getSunrise() {
        return sunrise;
    }

    public void setSunrise(LocalTime sunrise) {
        this.sunrise = sunrise;
    }

    public LocalTime getSunset() {
        return sunset;
    }

    public void setSunset(LocalTime sunset) {
        this.sunset = sunset;
    }

    @Override
    public String toString() {
        return String.format("City: %s:\n" +
                "-min temperature: %.2f\n" +
                "--max temperature: %.2f\n" +
                "---sunrise: %s\n" +
                "----sunset: %s\n",
                city.getCityName(),
                minTemperature,
                maxTemperature,
                sunrise.toString(),
                sunset.toString());
    }
}

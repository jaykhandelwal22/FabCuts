package com.fabcuts;

public class BlogCities {

    private Double latitude;
    private Double longitude;
    private String name;
    private String phone;


    public BlogCities(Double latitude, Double longitude, String name, String phone) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.phone = phone;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public BlogCities(){

    }
}

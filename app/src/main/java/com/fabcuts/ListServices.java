package com.fabcuts;

public class ListServices {
    String servicename;
    int estimatedtime;
    int price;

    public ListServices(String servicename, int estimatedtime, int price) {
        this.servicename = servicename;
        this.estimatedtime = estimatedtime;
        this.price = price;
    }

    public String getServicename() {
        return servicename;
    }

    public void setServicename(String servicename) {
        this.servicename = servicename;
    }

    public int getEstimatedtime() {
        return estimatedtime;
    }

    public void setEstimatedtime(int estimatedtime) {
        this.estimatedtime = estimatedtime;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public ListServices() {
    }
}


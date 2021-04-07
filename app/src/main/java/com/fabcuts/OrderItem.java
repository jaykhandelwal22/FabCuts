package com.fabcuts;

public class OrderItem {
    String name;
    int price;
    int time;

    public OrderItem(String name, int price, int time) {
        this.name = name;
        this.price = price;
        this.time = time;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getTime() {
        return time;
    }
}

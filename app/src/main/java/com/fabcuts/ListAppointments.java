package com.fabcuts;

public class ListAppointments {

    String orderedby;
    String timestamp;
    String startedat;
    String endedat;
    String name;
    String random_string;
    String assignedto;
    int status;
    int time;

    public ListAppointments(int status, int time,String assignedto,String startedat,String endedat) {
        this.status = status;
        this.time = time;
        this.assignedto=assignedto;
        this.startedat=startedat;
        this.endedat=endedat;
    }

    public ListAppointments(String orderedby, String timestamp, String name, String random_string) {
        this.orderedby = orderedby;
        this.timestamp = timestamp;
        this.name = name;
        this.random_string = random_string;
    }

    public String getOrderedby() {
        return orderedby;
    }

    public void setOrderedby(String orderedby) {
        this.orderedby = orderedby;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRandom_string() {
        return random_string;
    }

    public void setRandom_string(String random_string) {
        this.random_string = random_string;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getAssignedto() {
        return assignedto;
    }

    public void setAssignedto(String assignedto) {
        this.assignedto = assignedto;
    }

    public String getStartedat() {
        return startedat;
    }

    public void setStartedat(String startedat) {
        this.startedat = startedat;
    }

    public String getEndedat() {
        return endedat;
    }

    public void setEndedat(String endedat) {
        this.endedat = endedat;
    }

    public ListAppointments() {
    }
}

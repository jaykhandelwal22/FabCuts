package com.fabcuts;

public class LChangeState {
    String name;
    int status,time;

    public LChangeState(String name, int status, int time) {
        this.name = name;
        this.status = status;
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public LChangeState() {
    }
}

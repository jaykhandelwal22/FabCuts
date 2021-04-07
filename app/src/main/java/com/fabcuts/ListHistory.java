package com.fabcuts;

public class ListHistory {
    String orderedfrom;
    String timestamp;

    public ListHistory(String orderedfrom, String timestamp) {
        this.orderedfrom = orderedfrom;
        this.timestamp = timestamp;
    }

    public String getOrderedfrom() {
        return orderedfrom;
    }

    public void setOrderedfrom(String orderedfrom) {
        this.orderedfrom = orderedfrom;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public ListHistory() {

    }
}


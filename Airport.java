package com.example.tbg;

public class Airport {
    private String name;
    private int imageResId;

    public Airport(String name, int imageResId) {
        this.name = name;
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }

    public int getImageResId() {
        return imageResId;
    }
}

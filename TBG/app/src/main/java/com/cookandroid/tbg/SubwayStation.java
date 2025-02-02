package com.cookandroid.tbg;

public class SubwayStation {
    private String name;
    private int imageResId;

    public SubwayStation(String name, int imageResId) {
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
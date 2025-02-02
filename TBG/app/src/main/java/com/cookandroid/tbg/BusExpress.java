package com.cookandroid.tbg;


public class BusExpress {
    private String name;
    private int imageResId;

    public BusExpress(String name, int imageResId) {
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

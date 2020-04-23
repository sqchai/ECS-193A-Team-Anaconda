package com.example.carappv3;

public class MappingSpeed {
    public int convert(int progress, int targetMin, int targetMax) {
        return (int)(((float)progress / 100.0) * (targetMax - targetMin) + targetMin);
    }

    public MappingSpeed() {
    }
}

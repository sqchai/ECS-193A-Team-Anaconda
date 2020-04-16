package com.example.carappv3;

import android.graphics.Path;
import android.graphics.Point;

import java.util.ArrayList;

public class DrawingPath {
    public Path path;
    public Path linePath;
    public ArrayList<Point> vertices;

    public DrawingPath(Path path, Path linePath, ArrayList<Point> vertices) {
        this.path = path;
        this.linePath = linePath;
        this.vertices = vertices;
    }
}

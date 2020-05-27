package com.example.carappv3;

import android.graphics.Point;

public class PositionData {
    //data type that encapsulates car position/movement data
    //current position c
    private Point c;
    //previous position pp
    private Point pp;

    public Point getC() {
        return this.c;
    }

    public Point getPP() {
        return this.pp;
    }

    public Point getVec() {
        return new Point(this.c.x - this.pp.x, this.c.y - this.pp.y);
    }

    public PositionData(Point c, Point pp) {
        this.c = c;
        this.pp = pp;
    }
}

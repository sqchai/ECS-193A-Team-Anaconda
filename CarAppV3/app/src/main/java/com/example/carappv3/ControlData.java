package com.example.carappv3;

import android.graphics.Point;

public class ControlData {
    //data type that encapsulates car movement data
    //turningDirection 1: left, 2: right
    private int turningDirection;
    private int angle;
    private int distance;

    public int getAngle() {
        return angle;
    }

    public int getDistance() {
        return distance;
    }

    public int getTurningDirection() {
        return turningDirection;
    }

    public static ControlData getControlData(PositionData cPd, Point np) {
        int myTurningDirection;
        int myAngle;
        int myDist;

        Point pp = cPd.getPP();
        Point c = cPd.getC();
        PositionData dPd = new PositionData(np, c);

        //find which dir to turn
        int dir = (c.x-pp.x) * (np.y-pp.y) - (c.y-pp.y) * (np.x-pp.x);
        if (dir <= 0) {
            //turn left
            myTurningDirection = 1;
        } else {
            myTurningDirection = 2;
        }

        //find how much angle to turn
        Point vec1 = cPd.getVec();
        Point vec2 = dPd.getVec();
        double vecProduct = vec1.x * vec2.x + vec1.y * vec2.y;
        double lenVec1 = Math.sqrt(vec1.x * vec1.x + vec1.y * vec1.y);
        double lenVec2 = Math.sqrt(vec2.x * vec2.x + vec2.y * vec2.y);
        double cos = vecProduct / (lenVec1 * lenVec2);
        myAngle = (int)(Math.toDegrees(Math.acos(cos)));

        //find distance to travel from c to np
        int diffX = np.x - c.x;
        int diffY = np.y - c.y;
        myDist = (int)Math.sqrt(diffX * diffX + diffY * diffY);

        return new ControlData(myTurningDirection, myAngle, myDist);
    }

    public ControlData(int turningDirection, int angle, int distance) {
        this.turningDirection = turningDirection;
        this.angle = angle;
        this.distance = distance;
    }

    public ControlData() {
        this.turningDirection = 1;
        this.angle = 0;
        this.distance = 0;
    }
}

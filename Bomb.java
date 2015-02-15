package com.jmpa.chainreaction;

import java.util.ArrayList;

public class Bomb {

    public int radius;
    public int maxRadius;
    public int xCoord;
    public int yCoord;
    public int color = 0xaaff2211;
    public int age;
    public int maxAge = 100;
    public ArrayList<Ball> balls;

    Bomb(int x, int y, int r, int maxR, int c, ArrayList<Ball> alB) {
        balls = alB;
        age = 0;
        xCoord = x;
        yCoord = y;
        radius = r;
        maxRadius = maxR;

        // take the old colour, clear alpha then set alpha
        if (c != 0) { color = (c & 0x00ffffff) | 0xaa000000; }
    }

    public boolean age() {

        age++;

        if ((age >= maxAge) || (balls.size() < 1)) {

            // shrink bomb if dying
            radius -= (int) (maxRadius / 6);


            // delete bomb if its shrunk to 0
            if (radius <= 0) {
                return false;
            }

        } else {
            // grow bomb
            if (radius < maxRadius) {
                radius += (int)((maxRadius - radius)/4);
            }
        }

        return true;
    }

}
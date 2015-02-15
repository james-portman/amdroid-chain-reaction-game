package com.jmpa.chainreaction;


import java.util.ArrayList;

public class Ball {
    public int radius;
    public int xCoord;
    public int yCoord;
    public float xSpeed;
    public float ySpeed;
    public int color;
    public int mapWidth;
    public int mapHeight;
    public ArrayList<Bomb> bombs;



    Ball(int x, int y, float xS, float yS, int c, int mW, int mH, ArrayList<Bomb> alB) {

        // make ball a percentage of screen area
        // been using 15 / 700 px
        // so 2.x percent of screen width
        // or aread - 700,000 for 15px rad - 0,002
        radius = (int) (mW * mH * 0.00002);
        bombs = alB;
        mapWidth = mW;
        mapHeight = mH;
        if (x < radius) { x+= radius; }
        if (y < radius) { y+= radius; }
        xCoord = x;
        yCoord = y;
        xSpeed = xS;
        ySpeed = yS;

        switch (c) {
            case 1: color = 0xffcccccc; break;
            case 2: color = 0xffE8940C; break;
            case 3: color = 0xffff00ff; break;
            case 4: color = 0xffffff00; break;
            case 5: color = 0xff00ffff; break;
            case 6: color = 0xff00ff00; break;
            case 7: color = 0xffeeeeee; break;
            case 8: color = 0xffdddddd; break;
            default: color = 0xffeeeeee; break;
        }


    }

    public boolean move() {

        if ( (xSpeed != 0) || (ySpeed != 0) ) {

            // move the ball
            xCoord += xSpeed;
            yCoord += ySpeed;


            // check for collision with walls

            if ((xCoord + radius) >= mapWidth) {
                if (xSpeed > 0) {
                    xSpeed = (xSpeed * -1);
                }
            }

            if ((xCoord - radius)<= 0) {
                if (xSpeed < 0) {
                    xSpeed = (xSpeed * -1);
                }
            }

            if ((yCoord + radius) >= mapHeight) {
                //Log.d("jmpagame", "ball hit the bottom");
                if (ySpeed > 0) {
                    ySpeed = (ySpeed * -1);
                }
            }

            if ((yCoord - radius) <= 0) {
                //Log.d("jmpagame", "ball hit the top");
                if (ySpeed < 0) {
                    ySpeed = (ySpeed * -1);
                }
            }
        }



        // see if we hit a bomb
        for (Bomb bomb : bombs){
            if (circleCollision(bomb.xCoord, bomb.yCoord, bomb.radius, xCoord, yCoord, radius)) {
                return false;
            }
        }
        // not hit any bombs
        return true;


    }


    public boolean circleCollision(int x1, int y1, int r1, int x2, int y2, int r2) {
        double xDiff = x1 - x2;
        double yDiff = y1 - y2;
        double distanceSquared = xDiff * xDiff + yDiff * yDiff;
        return distanceSquared < (r1 + r2) * (r1 + r2);
    }

}

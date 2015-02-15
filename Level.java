package com.jmpa.chainreaction;

public class Level {

    public int levelNumber;
    public int bombRadius;
    public int bombMaxAge;
    public int numberOfBalls;
    public boolean completedTheGame = false;
    public int levels;

    Level(int ln, int mapWidth, int mapHeight) {

        levelNumber = ln;
        levels = 20;
        int area = mapWidth * mapHeight;
        int biggestBomb = area/7000;

        // absolutely could do this with maths instead but...
        switch (levelNumber) {

            case 1:
                bombRadius = (int) (biggestBomb * 1);
                bombMaxAge = 150;
                numberOfBalls = 50;
                break;

            case 2:
                bombRadius = (int) (biggestBomb * 0.95);
                bombMaxAge = 145;
                numberOfBalls = 50;
                break;

            case 3:
                bombRadius = (int) (biggestBomb * 0.90);
                bombMaxAge = 140;
                numberOfBalls = 45;
                break;

            case 4:
                bombRadius = (int) (biggestBomb * 0.85);
                bombMaxAge = 135;
                numberOfBalls = 45;
                break;

            case 5:
                bombRadius = (int) (biggestBomb * 0.8);
                bombMaxAge = 130;
                numberOfBalls = 40;
                break;

            case 6:
                bombRadius = (int) (biggestBomb * 0.75);
                bombMaxAge = 125;
                numberOfBalls = 40;
                break;

            case 7:
                bombRadius = (int) (biggestBomb * 0.7);
                bombMaxAge = 120;
                numberOfBalls = 35;
                break;

            case 8:
                bombRadius = (int) (biggestBomb * 0.65);
                bombMaxAge = 115;
                numberOfBalls = 35;
                break;

            case 9:
                bombRadius = (int) (biggestBomb * 0.6);
                bombMaxAge = 110;
                numberOfBalls = 30;
                break;

            case 10:
                bombRadius = (int) (biggestBomb * 0.55);
                bombMaxAge = 100;
                numberOfBalls = 30;
                break;
            case 11:
                bombRadius = (int) (biggestBomb * 0.5);
                bombMaxAge = 95;
                numberOfBalls = 25;
                break;

            case 12:
                bombRadius = (int) (biggestBomb * 0.45);
                bombMaxAge = 90;
                numberOfBalls = 25;
                break;

            case 13:
                bombRadius = (int) (biggestBomb * 0.4);
                bombMaxAge = 85;
                numberOfBalls = 20;
                break;

            case 14:
                bombRadius = (int) (biggestBomb * 0.35);
                bombMaxAge = 80;
                numberOfBalls = 20;
                break;

            case 15:
                bombRadius = (int) (biggestBomb * 0.3);
                bombMaxAge = 75;
                numberOfBalls = 15;
                break;

            case 16:
                bombRadius = (int) (biggestBomb * 0.25);
                bombMaxAge = 70;
                numberOfBalls = 15;
                break;

            case 17:
                bombRadius = (int) (biggestBomb * 0.2);
                bombMaxAge = 65;
                numberOfBalls = 10;
                break;

            case 18:
                bombRadius = (int) (biggestBomb * 0.15);
                bombMaxAge = 60;
                numberOfBalls = 10;
                break;

            case 19:
                bombRadius = (int) (biggestBomb * 0.1);
                bombMaxAge = 55;
                numberOfBalls = 5;
                break;

            case 20:
                bombRadius = (int) (biggestBomb * 0.05);
                bombMaxAge = 50;
                numberOfBalls = 5;
                break;
            default:
                bombRadius = 1;
                bombMaxAge = 1;
                numberOfBalls = 1;
                break;

        }
    }
}

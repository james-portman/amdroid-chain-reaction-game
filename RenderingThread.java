package com.jmpa.chainreaction;

import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.view.TextureView;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;


public class RenderingThread extends Thread {

    private Random randoms = new Random(System.currentTimeMillis() / 1000L);
    private final TextureView mSurface;
    // public volatile boolean mRunning = false;
    ArrayList<Ball> balls = new ArrayList<Ball>();
    ArrayList<Bomb> bombs = new ArrayList<Bomb>();
    ArrayList<Bomb> bombsQueue = new ArrayList<Bomb>();
    public int mapHeight;
    public int mapWidth;
    public int mapWidthCenter;

    public int bombsLeft = 1;
    public int lastRunTimeMs = 0;
    public int lastRunFps = 0;
    public boolean gameOver = false;
    public boolean levelCompleted = false;

    Level level;
    Paint paint = new Paint();

    long startTime;
    long endTime;

    int textSize;
    int linesWritten;

    public boolean mPaused = true;
    private Object mPauseLock;



    public RenderingThread(TextureView surface, int levelNumber, int numberOfBombs) {

        mPauseLock = new Object();

        bombsLeft = numberOfBombs;

        mSurface = surface;
        mapWidth = mSurface.getWidth();
        mapHeight = mSurface.getHeight();
        mapWidthCenter = mapWidth / 2;
        textSize = mapWidth / 20;
        paint.setTextSize(textSize);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setAntiAlias(true);

        level = new Level(levelNumber,mapWidth, mapHeight);

        for (int i = 0; i < level.numberOfBalls; i++) {
            addRandomBall();
        }

        // mRunning = true;
        mPaused = false;

    }


    @Override
    public void run() {

        // while (mRunning && !Thread.interrupted()) {
        while (!Thread.interrupted()) {

            // see if we should be paused
            synchronized (mPauseLock) {
                while (mPaused) {
                    try {
                        mPauseLock.wait();
                    } catch (InterruptedException e) {
                    }
                }
            }

            startTime = System.currentTimeMillis();
            final Canvas canvas = mSurface.lockCanvas(null);
            linesWritten = 0;


            // do bomb dropping queue
            if (bombsQueue.size() > 0) {
                for (Bomb bomb : bombsQueue) {
                    bombs.add( new Bomb(bomb.xCoord,bomb.yCoord, bomb.radius, bomb.maxRadius, 0, balls) );
                }
                bombsQueue.clear();
            }


            try {
                // seem to hit here very very occasionally so double check we got a canvas
                if (canvas == null) {
                    pauseRendering();
                    continue;
                }
                canvas.drawColor(0x00000000, PorterDuff.Mode.CLEAR);
                paint.setColor(0xffffffff);

                writeLine(canvas, "Level " + level.levelNumber);
                writeLine(canvas, " Balls left - " + balls.size());
                writeLine(canvas, " Bombs left - " + bombsLeft);


                if (gameOver) {
                    bombs.clear();
                    writeLine(canvas, "Game over!");
                    writeLine(canvas, "Tap to retry this level");

                    pauseRendering();


                } else if (levelCompleted) {
                    bombs.clear();
                    writeLine(canvas, "Well done!");
                    writeLine(canvas, "Tap to continue");

                    pauseRendering();

                }


                // BALL code
                for (Iterator<Ball> ballIterator = balls.iterator(); ballIterator.hasNext(); ) {
                    Ball ball = ballIterator.next();

                    // draw ball
                    paint.setColor(ball.color);
                    paint.setStyle(Paint.Style.FILL);
                    canvas.drawCircle(ball.xCoord, ball.yCoord, ball.radius, paint);

                    // move the ball if its moving - move returns false if we hit a bomb
                    if (!ball.move()) {
                        ballIterator.remove(); // delete the ball

                        // have to add to bomb array when not iterating it!
                        bombs.add( new Bomb(ball.xCoord,ball.yCoord, ball.radius, level.bombRadius, ball.color, balls) );
                    }
                }


                // BOMB code
                for (Iterator<Bomb> bombIterator = bombs.iterator(); bombIterator.hasNext(); ) {
                    Bomb bomb = bombIterator.next();

                    // draw bomb
                    paint.setColor(bomb.color);
                    paint.setStyle(Paint.Style.FILL);
                    canvas.drawCircle(bomb.xCoord, bomb.yCoord, bomb.radius, paint);

                    // age bomb - returns false if bomb to be deleted
                    if (!bomb.age()) {
                        bombIterator.remove(); // delete bomb

                        // if no bombs on screen then see if we have lost, or won
                        if (bombs.size() == 0) {
                            if (balls.size() == 0 ) {
                                levelCompleted = true;
                                break;
                            }
                            if (bombsLeft == 0) {
                                gameOver = true;
                            }
                        }


                    }
                }


            } finally {
                mSurface.unlockCanvasAndPost(canvas);
            }


            // sort out the sleep time of the thread to try and get certain FPS
            endTime = System.currentTimeMillis();
            lastRunTimeMs = (int) (endTime - startTime);
            // potential max fps = 1000 / lastRunTimeMs;
            // 30 fps attempt (1000/30) = 33.3 recurring, so use 33 to account for this bit of adding etc
            int sleepTime = 33 - lastRunTimeMs;
            if (sleepTime < 5) { sleepTime = 5; }
            // lastRunFps = 1000 / (lastRunTimeMs + sleepTime);

            try {
                Thread.sleep( sleepTime );
            } catch (InterruptedException e) {
                // Interrupted
            }
        }
    }


    void writeLine(Canvas canvas, String text) {
        canvas.drawText(text, mapWidthCenter, textSize * (1+linesWritten), paint);
        linesWritten++;
    }

    public void pauseRendering() {
        synchronized (mPauseLock) {
            mPaused = true;
        }
    }

    public void resumeRendering() {
        synchronized (mPauseLock) {
            mPaused = false;
            mPauseLock.notifyAll();
        }
    }

    void touchEvent(int x, int y) {
        // may come through multiple times for one touch!
        // do queue first so if anything, we drop 2 bombs rather than drop last bomb and get game end before its added
        if (bombsLeft > 0) {
            bombsQueue.add( new Bomb(x,y, (int) (level.bombRadius * 0.75), level.bombRadius, 0, balls) );
            bombsLeft--;
        }
    }

    public int randomInt(int from, int to) {
        return randoms.nextInt(to-from) + from;
    }

    public void addRandomBall() {
        float speedX = randomInt(3,10); if ((randomInt(1,100) % 2) == 0) { speedX = speedX * -1; } // do 1 to 100 to get decent random
        int speedY = randomInt(3,10); if ((randomInt(1,100) % 2) == 0) { speedY = speedY * -1; }
        balls.add( new Ball( randomInt(0,mapWidth),randomInt(0,mapHeight), speedX,speedY, randomInt(1,8), mapWidth, mapHeight, bombs ) );
    }

    public void showCompleted() {
        final Canvas canvas = mSurface.lockCanvas(null);
        if (canvas != null) {
            canvas.drawColor(0x00000000, PorterDuff.Mode.CLEAR);
            paint.setColor(0xffffffff);
            try {
                canvas.drawText("YOU COMPLETED THE GAME!", mapWidthCenter, textSize,paint);
            } finally {
                mSurface.unlockCanvasAndPost(canvas);
            }
        }
    }

}

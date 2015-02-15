package com.jmpa.chainreaction;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity
        implements TextureView.SurfaceTextureListener {
    private TextureView mTextureView;
    private RenderingThread mThread;
    public int currentLevel;
    public int reachedLevel;
    public int width;
    public int height;
    public Display display;
    public Point size;
    public SharedPreferences settings;
    public FrameLayout content;
    private int allowedBombsPerLevel = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // requesting to turn the title OFF
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // making it full screen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);


        // all this to get the screen/window height
        display = getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;
        content = new FrameLayout(this);

        mTextureView = new TextureView(this);
        mTextureView.setSurfaceTextureListener(this);
        mTextureView.setOpaque(true); // set false for transparent background?
        content.addView(mTextureView, new FrameLayout.LayoutParams(width, height, Gravity.CENTER));

        settings = getSharedPreferences("comjmpachainreaction", MODE_PRIVATE);
        reachedLevel = settings.getInt("reachedLevel",1);

        currentLevel = 1;

        setContentView(R.layout.welcome);

        if (reachedLevel <= 1) {
            Button lsb = (Button) findViewById(R.id.levelSelectButton);
            lsb.setVisibility(View.GONE);

        } else {
            Button playFirstLevel = (Button) findViewById(R.id.startButton);
            playFirstLevel.setVisibility(View.GONE);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mThread != null) {
            mThread.pauseRendering();
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if (mThread == null) {
            // mThread = new RenderingThread(mTextureView, currentLevel, allowedBombsPerLevel);
            // mThread.start();
            startLevel(currentLevel);
        } else {
            // set it going again
            mThread.resumeRendering();

        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        // Ignored
        if (mThread != null) {
            mThread.pauseRendering();
        }
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        if (mThread != null) {
            mThread.pauseRendering();
        }
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        // Ignored
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch(event.getAction())
        {
            case MotionEvent.ACTION_DOWN:

                if (mThread != null) {


                    if (mThread.levelCompleted) {

                        if (currentLevel >= mThread.level.levels) {
                            // completed the game!

                            // may need these lines to fix corruption bug:
                            // mTextureView.setOpaque(false);
                            // mTextureView.setVisibility(View.GONE);
                            mThread.pauseRendering();

                            setContentView(R.layout.completed);
                            break;
                        }


                        // only update if higher than present!
                        //settings = getSharedPreferences("comjmpachainreaction", MODE_PRIVATE);
                        int tempLevel = settings.getInt("reachedLevel",1);
                        if ((currentLevel + 1) > tempLevel) {
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putInt("reachedLevel", currentLevel + 1);
                            editor.commit();
                        }

                        nextLevel();
                        break;

                    } else if (mThread.gameOver) {
                        retryLevel();
                        break;
                    }

                    if (!mThread.mPaused) {
                        int x = (int)event.getX();
                        int y = (int)event.getY();
                        //Log.d("jmpagame", "got a touch event! at " + x + "," + y + " telling thread!");
                        mThread.touchEvent(x,y);
                    }

                }

                break;
            case MotionEvent.ACTION_MOVE:
                // held down and moving
                break;
            case MotionEvent.ACTION_UP:
                // let go
                break;
        }


        /*
        // can use for swipes:
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
        }*/
        return false;
    }

    public void onLevelSelectButtonClicked(View view) {
        setContentView(R.layout.levelselect);

        ViewGroup layout = (ViewGroup) findViewById(R.id.levelSelectList);

        for (int i = reachedLevel; i >= 1; i--) {

            Button btn = new Button(this);
            btn.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            btn.setText("Level " + i);
            btn.setId(i);

            final int finalI = i;
            btn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // currentLevel = v.getId();
                    currentLevel = finalI;
                    onStartButtonClicked(v);
                }
            });
            layout.addView(btn);
        }
    }

    public void onDifficultySelectButtonClicked(View view) {
        TextView btn = (TextView) findViewById(R.id.difficultySelectButton);
        CharSequence current = btn.getText();
        if (current.equals(getString(R.string.difficulty_hard))) {
            btn.setText(getString(R.string.difficulty_medium));
            allowedBombsPerLevel = 2;

        } else if (current.equals(getString(R.string.difficulty_medium))) {
            btn.setText(getString(R.string.difficulty_easy));
            allowedBombsPerLevel = 3;

        } else { // could say if easy set but just make sure it goes to hard anyway
            btn.setText(getString(R.string.difficulty_hard));
            allowedBombsPerLevel = 1;
        }

    }

    public void onHomeButtonClicked(View view) {
        setContentView(R.layout.welcome);
    }

    public void onStartButtonClicked(View view) {
        setContentView(content);
    }


    public void startLevel(int level) {
        mThread = new RenderingThread(mTextureView, level, allowedBombsPerLevel);
        mThread.start();
    }

    public void nextLevel() {
        currentLevel++;
        startLevel(currentLevel);
    }

    public void retryLevel() {
        startLevel(currentLevel);
    }

}


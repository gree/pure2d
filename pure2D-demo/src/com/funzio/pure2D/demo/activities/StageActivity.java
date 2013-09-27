package com.funzio.pure2D.demo.activities;

import java.util.Random;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.funzio.pure2D.BaseScene;
import com.funzio.pure2D.BaseStage;
import com.funzio.pure2D.Pure2D;
import com.funzio.pure2D.demo.R;
import com.funzio.pure2D.gl.GLColor;

public class StageActivity extends Activity implements OnTouchListener {
    final protected static int OBJ_INIT_NUM = 1000;
    final protected static int OBJ_STEP_NUM = 100;
    final protected static Random RANDOM = new Random();

    final protected static GLColor COLOR_BLACK = GLColor.BLACK;
    final protected static GLColor COLOR_WHITE = GLColor.WHITE;
    final protected static GLColor COLOR_GRAY = new GLColor(0.5f, 0.5f, 0.5f, 1);
    final protected static GLColor COLOR_RED = new GLColor(0.7f, 0, 0, 1);
    final protected static GLColor COLOR_GREEN = new GLColor(0, 0.7f, 0, 1);
    final protected static GLColor COLOR_BLUE = new GLColor(0, 0, 0.7f, 1);
    final protected static GLColor COLOR_YELLOW = new GLColor(1f, 1f, 0, 1);

    protected BaseStage mStage;
    protected BaseScene mScene;
    protected Point mDisplaySize = new Point();
    protected Point mDisplaySizeDiv2 = new Point();
    protected Random mRandom = new Random();

    // views
    protected TextView mFrameRate;
    protected TextView mObjects;
    protected boolean mUserPaused;

    protected Handler mHandler = new Handler();
    private Runnable mFrameRateUpdater = new Runnable() {
        @Override
        public void run() {
            startFrameRate();
        }
    };

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN, LayoutParams.FLAG_FULLSCREEN);

        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        mDisplaySize.x = metrics.widthPixels;
        mDisplaySize.y = metrics.heightPixels;
        mDisplaySizeDiv2.x = mDisplaySize.x / 2;
        mDisplaySizeDiv2.y = mDisplaySize.y / 2;

        setContentView(getLayout());

        // set up the stage and scene
        mStage = (BaseStage) findViewById(R.id.stage);
        mScene = createScene();
        // mScene.setAutoClear(false);
        mStage.setScene(mScene);
        mStage.setOnTouchListener(this);

        // fps
        mFrameRate = (TextView) findViewById(R.id.tv_frame_rate);
        // objects
        mObjects = (TextView) findViewById(R.id.tv_objects);
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.stage_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.pause:
                if (mScene.isPaused()) {
                    mUserPaused = false;
                    mScene.resume();
                    item.setTitle(getResources().getString(R.string.pause));
                } else {
                    mUserPaused = true;
                    mScene.pause();
                    item.setTitle(getResources().getString(R.string.resume));
                }
                return true;

            case R.id.debug:
                Pure2D.DEBUG_FLAGS = Pure2D.DEBUG_FLAGS == 0 ? Pure2D.DEBUG_FLAG_WIREFRAME | Pure2D.DEBUG_FLAG_GLOBAL_BOUNDS : 0;
                return true;

            case R.id.fps:
                final int visibility = mFrameRate.getVisibility() == View.VISIBLE ? View.INVISIBLE : View.VISIBLE;
                mFrameRate.setVisibility(visibility);
                mObjects.setVisibility(visibility);
                return true;

            case R.id.source:
                Toast.makeText(this, getClass().getCanonicalName(), Toast.LENGTH_LONG).show();
                return true;
        }

        return false;
    }

    protected BaseScene createScene() {
        return new BaseScene();
    }

    protected int getLayout() {
        return R.layout.stage_simple;
    }

    protected void startFrameRate() {
        mHandler.postDelayed(mFrameRateUpdater, 1000);

        mScene.queueEvent(new Runnable() {

            @Override
            public void run() {
                // get object count on GL thread
                final int numObjs = getNumObjects();

                // update views on UI thread
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mFrameRate.setText(mScene.getCurrentFps() + " fps");
                        mObjects.setText(numObjs + " objs");
                    }
                });

            }
        });

    }

    protected int getNumObjects() {
        return mScene.getNumChildren();
    }

    @Override
    protected void onStop() {
        super.onStop();

        // stop tracing
        mHandler.removeCallbacks(mFrameRateUpdater);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // pause the stage
        if (mStage != null && mScene != null) {
            mStage.onPause();
            mScene.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        // resume the stage
        if (mStage != null && mScene != null) {
            mStage.onResume();
            if (!mUserPaused) {
                mScene.resume();
            }
        }

        // start recording frame rate
        startFrameRate();
    }

    public boolean onTouch(final View v, final MotionEvent event) {
        return false;
    }
}

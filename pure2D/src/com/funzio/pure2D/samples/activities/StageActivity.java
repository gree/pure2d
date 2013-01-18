package com.funzio.pure2D.samples.activities;

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

import com.funzio.pure2D.BaseScene;
import com.funzio.pure2D.BaseStage;
import com.funzio.pure2D.Pure2D;
import com.funzio.pure2D.R;

public class StageActivity extends Activity implements OnTouchListener {
    final protected static int OBJ_INIT_NUM = 1000;
    final protected static int OBJ_STEP_NUM = 100;
    final protected static Random RANDOM = new Random();

    protected BaseStage mStage;
    protected BaseScene mScene;
    protected Point mDisplaySize = new Point();
    protected Point mDisplaySizeDiv2 = new Point();
    protected Random mRandom = new Random();

    // views
    protected TextView mFrameRate;
    protected TextView mObjects;

    private Handler mHandler = new Handler();
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

        // start recording frame rate
        startFrameRate();

        final DisplayMetrics metrics = getResources().getDisplayMetrics();
        mDisplaySize.x = metrics.widthPixels;
        mDisplaySize.y = metrics.heightPixels;
        mDisplaySizeDiv2.x = mDisplaySize.x / 2;
        mDisplaySizeDiv2.y = mDisplaySize.y / 2;
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
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
                    mScene.resume();
                    item.setTitle(getResources().getString(R.string.pause));
                } else {
                    mScene.pause();
                    item.setTitle(getResources().getString(R.string.resume));
                }
                return true;

            case R.id.debug:
                Pure2D.DEBUG_ENALBLED = !Pure2D.DEBUG_ENALBLED;
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

        mFrameRate.setText(mScene.getCurrentFps() + " fps");
        mObjects.setText(getNumObjects() + " objs");
    }

    protected int getNumObjects() {
        return mScene.getNumChildren();
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onStop()
     */
    @Override
    protected void onStop() {
        super.onStop();

        // pause the scene
        mScene.pause();

        // stop tracing
        mHandler.removeCallbacks(mFrameRateUpdater);
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {
        super.onResume();

        // resume the scene
        mScene.resume();
    }

    public boolean onTouch(final View v, final MotionEvent event) {
        return false;
    }
}

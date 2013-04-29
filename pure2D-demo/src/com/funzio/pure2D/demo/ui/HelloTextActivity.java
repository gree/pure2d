package com.funzio.pure2D.demo.ui;

import javax.microedition.khronos.opengles.GL10;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.funzio.pure2D.Scene;
import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.demo.objects.Bouncer;
import com.funzio.pure2D.gl.GLColor;
import com.funzio.pure2D.gl.gl10.textures.TextTexture;

public class HelloTextActivity extends StageActivity {
    private TextTexture mTexture;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // mScene.setColor(new GLColor(0, .8f, 0, 1f));
        mScene.setListener(new Scene.Listener() {
            @Override
            public void onSurfaceCreated(final GL10 gl) {
                createTexture();
                addObject(mDisplaySizeDiv2.x, mDisplaySizeDiv2.y);
            }
        });
    }

    private void createTexture() {
        mTexture = mScene.getTextureManager().createTextTexture("Hello World!", null);
    }

    private void addObject(final float x, final float y) {
        // create object
        Bouncer obj = new Bouncer();
        obj.setTexture(mTexture);
        obj.setColor(new GLColor(255, mRandom.nextInt(255), mRandom.nextInt(255), 255));

        // center origin
        // obj.setOriginAtCenter();

        // random positions
        obj.setPosition(x, y);

        // add to scene
        mScene.addChild(obj);
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        final int action = event.getAction();

        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
            mStage.queueEvent(new Runnable() {
                @Override
                public void run() {
                    int len = event.getPointerCount();
                    for (int i = 0; i < len; i++) {
                        addObject(event.getX(i), mDisplaySize.y - event.getY(i));
                    }
                }
            });
        }

        return true;
    }
}

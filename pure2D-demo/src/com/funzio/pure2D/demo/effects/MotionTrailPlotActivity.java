package com.funzio.pure2D.demo.effects;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.funzio.pure2D.Scene;
import com.funzio.pure2D.demo.R;
import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.demo.objects.Bouncer;
import com.funzio.pure2D.effects.trails.MotionTrailPlot;
import com.funzio.pure2D.gl.GLColor;
import com.funzio.pure2D.gl.gl10.textures.Texture;

public class MotionTrailPlotActivity extends StageActivity {

    private List<Texture> mTextures = new ArrayList<Texture>();

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mScene.setListener(new Scene.Listener() {

            @Override
            public void onSurfaceCreated(final GL10 gl) {
                loadTextures();

                for (int i = 0; i < 50; i++) {
                    addObject(RANDOM.nextInt(mDisplaySize.x), RANDOM.nextInt(mDisplaySize.y));
                }
            }
        });
    }

    private void loadTextures() {
        final int[] ids = {
                R.drawable.cc_32, // cc
                R.drawable.mw_32, // mw
                R.drawable.ka_32, // ka
        };

        for (int id : ids) {
            // add texture to list
            mTextures.add(mScene.getTextureManager().createDrawableTexture(id, null));
        }
    }

    private void addObject(final float x, final float y) {
        final GLColor color1 = new GLColor(.5f + RANDOM.nextFloat(), RANDOM.nextFloat(), RANDOM.nextFloat(), 1f);
        final GLColor color2 = new GLColor(color1);// new GLColor(.5f + RANDOM.nextFloat(), RANDOM.nextFloat(), RANDOM.nextFloat(), 0.5f);
        color2.a = 0.1f;

        // create object
        Bouncer obj = new Bouncer();
        obj.setSize(30, 30);
        obj.setOriginAtCenter();
        obj.setColor(color1);
        obj.setPosition(x, y);
        obj.setVisible(false);
        // add to scene
        mScene.addChild(obj);

        MotionTrailPlot trail = new MotionTrailPlot();
        trail.setTexture(mTextures.get(RANDOM.nextInt(mTextures.size())));
        // trail.setAlphaRange(1f, 0.25f);
        trail.setScaleRange(1f, 0.25f);
        trail.setNumPoints(10);
        // trail.setMinLength(500);
        trail.setTarget(obj);
        mScene.addChild(trail);

    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mStage.queueEvent(new Runnable() {

                @Override
                public void run() {
                    for (int i = 0; i < 10; i++) {
                        addObject(event.getX(), mDisplaySize.y - event.getY());
                    }
                }
            });
        }

        return true;
    }

}

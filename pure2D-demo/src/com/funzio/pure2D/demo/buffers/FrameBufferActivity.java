package com.funzio.pure2D.demo.buffers;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.funzio.pure2D.Pure2D;
import com.funzio.pure2D.Scene;
import com.funzio.pure2D.demo.R;
import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.gl.gl10.FrameBuffer;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.shapes.Sprite;

public class FrameBufferActivity extends StageActivity {
    private static final int FB_WIDTH = 200;
    private static final int FB_HEIGHT = 200;

    private List<Texture> mTextures = new ArrayList<Texture>();
    private Texture mBufferTexture;
    private FrameBuffer mFrameBuffer;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // need to get the GL reference first
        mScene.setListener(new Scene.Listener() {

            @Override
            public void onSurfaceCreated(final GL10 gl) {
                loadTextures();
                createBufferTexture();
                addObject(mDisplaySizeDiv2.x, mDisplaySizeDiv2.y);
            }
        });
    }

    private void loadTextures() {
        final int[] ids = {
                R.drawable.cc_32, // cc
                R.drawable.mw_32, // mw
                R.drawable.ka_32, // ka
        // R.drawable.cc_128, // cc
        // R.drawable.mw_128, // mw
        // R.drawable.ka_128, // ka
        };

        for (int id : ids) {
            // add texture to list
            mTextures.add(mScene.getTextureManager().createDrawableTexture(id, null));
        }
    }

    private void createBufferTexture() {
        if (!FrameBuffer.isSupported(mScene.getGLState().mGL)) {
            Log.e(Pure2D.TAG, "FrameBuffer is not supported!");
            return;
        }

        // create a frame buffer
        mFrameBuffer = new FrameBuffer(mScene.getGLState(), FB_WIDTH, FB_HEIGHT, true);
        mBufferTexture = mFrameBuffer.getTexture();

        mFrameBuffer.bind(Scene.AXIS_TOP_LEFT); // invert
        // start drawing to the frame buffer
        Sprite sprite = new Sprite();
        for (int i = 0; i < 100; i++) {
            sprite.setTexture(mTextures.get(i % mTextures.size()));
            // random color and position
            sprite.moveTo((i % 10) * 32, (i / 10) * 32);
            // draw onto frame buffer
            sprite.draw(mScene.getGLState());
        }
        mFrameBuffer.unbind();

        // framebuffer no longer needed
        mFrameBuffer.unload();
    }

    private void addObject(final float x, final float y) {
        // create object
        Sprite obj = new Sprite();
        obj.setTexture(mBufferTexture);
        // obj.setAutoUpdateBounds(true);
        // obj.setDebugFlags(Pure2D.DEBUG_FLAG_GLOBAL_BOUNDS);

        // center origin
        obj.setOriginAtCenter();

        // random position
        obj.setPosition(x, y);

        // add to scene
        mScene.addChild(obj);
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mStage.queueEvent(new Runnable() {

                @Override
                public void run() {
                    addObject(event.getX(), mDisplaySize.y - event.getY());
                }
            });
        }

        return true;
    }
}

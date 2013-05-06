package com.funzio.pure2D.demo.buffers;

import javax.microedition.khronos.opengles.GL10;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.funzio.pure2D.Pure2D;
import com.funzio.pure2D.Scene;
import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.gl.GLColor;
import com.funzio.pure2D.gl.gl10.FrameBuffer;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.shapes.Rectangular;
import com.funzio.pure2D.shapes.Sprite;

public class FrameBufferActivity extends StageActivity {
    private static final int FB_WIDTH = 256;
    private static final int FB_HEIGHT = 256;

    private Texture mTexture;
    private FrameBuffer mFrameBuffer;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // need to get the GL reference first
        mScene.setListener(new Scene.Listener() {

            @Override
            public void onSurfaceCreated(final GL10 gl) {
                createTexture();
                addObject(mDisplaySizeDiv2.x, mDisplaySizeDiv2.y);
            }
        });
    }

    private void createTexture() {
        if (!FrameBuffer.isSupported(mScene.getGLState().mGL)) {
            Log.e(Pure2D.TAG, "FrameBuffer is not supported!");
            return;
        }

        // generate the texture
        // new texture
        // mTexture = mScene.getTextureManager().createTexture(FB_WIDTH, FB_HEIGHT);
        // create a frame buffer
        mFrameBuffer = new FrameBuffer(mScene.getGLState(), FB_WIDTH, FB_HEIGHT, true);
        mTexture = mFrameBuffer.getTexture();

        mFrameBuffer.bind();
        // start drawing to the frame buffer
        Rectangular rect = new Rectangular();
        rect.setSize(32f, 32f);
        for (int i = 0; i < 100; i++) {
            // random color and position
            rect.setColor(new GLColor(1f, mRandom.nextFloat(), mRandom.nextFloat(), mRandom.nextFloat() + 0.5f));
            rect.moveTo(mRandom.nextInt(FB_WIDTH), mRandom.nextInt(FB_HEIGHT));
            // draw onto frame buffer
            rect.draw(mScene.getGLState());
        }
        mFrameBuffer.unbind();
        mFrameBuffer.unload();
    }

    private void addObject(final float x, final float y) {
        // create object
        Sprite obj = new Sprite();
        obj.setTexture(mTexture);

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

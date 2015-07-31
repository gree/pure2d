/*******************************************************************************
 * Copyright (C) 2012-2014 GREE, Inc.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/
package com.funzio.pure2D.demo.objects;

import java.io.InputStream;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.funzio.pure2D.Scene;
import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.lwf.LWF;
import com.funzio.pure2D.lwf.LWFData;
import com.funzio.pure2D.lwf.LWFManager;
import com.funzio.pure2D.lwf.LWFObject;

public class LWFCharacterActivity extends StageActivity {

    private LWFManager mLWFManager;
    private LWFData mLWFData;
    private LWFObject mLWFObject;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!LWF.loadLibrary()) {
            Log.e("LWFCharacterActivity", "ERROR: loadLibrary");
        }

        mLWFManager = new LWFManager(mDisplaySize.x, mDisplaySize.y);

        mLWFObject = new LWFObject();
        mScene.addChild(mLWFObject);

        mScene.setListener(new Scene.Listener() {

            @Override
            public void onSurfaceCreated(final GLState gl, final boolean firstTime) {
                if (firstTime) {
                    try {
                        InputStream stream = getAssets().open("lwf/YetiBlue/YetiBlue.lwf");
                        mLWFData = mLWFManager.createLWFData(stream);
                    } catch (Exception e) {
                        Log.e("LWFCharacterActivity", "ERROR: " + e);
                    }

                    int textureNum = mLWFData.getTextureNum();
                    Texture[] textures = new Texture[textureNum];
                    for (int i = 0; i < textureNum; ++i) {
                        String name = mLWFData.getTextureName(i);
                        textures[i] = mScene.getTextureManager().createAssetTexture("lwf/YetiBlue/" + name, null);
                    }
                    mLWFData.setTextures(textures);

                    attachLWF(mDisplaySizeDiv2.x, mDisplaySizeDiv2.y);
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLWFManager.dispose();
    }

    private void attachLWF(final float x, final float y) {
        if (mLWFData == null) {
            return;
        }

        // attach lwf
        LWF lwf = mLWFObject.attachLWF(mLWFData);

        // position in Flash coordinate
        lwf.moveTo("_root", x - 450, -y - 80);
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mStage.queueEvent(new Runnable() {

                @Override
                public void run() {
                    attachLWF(event.getX(), mDisplaySize.y - event.getY());
                }
            });
        }

        return true;
    }
}

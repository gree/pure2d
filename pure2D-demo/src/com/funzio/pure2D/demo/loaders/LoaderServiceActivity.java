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
package com.funzio.pure2D.demo.loaders;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.gl.gl10.textures.TextureOptions;
import com.funzio.pure2D.loaders.tasks.DownloadTask;
import com.funzio.pure2D.shapes.Sprite;
import com.longo.pure2D.demo.R;

public class LoaderServiceActivity extends StageActivity {
    private boolean mLoading = false;
    private long mStartTime;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // listen for the finish event
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, final Intent intent) {
                Log.v("long", "ALL TASKS ARE DONE! " + intent.getAction());
                Log.v("long", "Load time: " + (System.currentTimeMillis() - mStartTime));

                mLoading = false;
                ((Button) findViewById(R.id.btn_load)).setText(R.string.start_loading);
            }
        }, new IntentFilter(HelloLoaderService.getOnFinishAction()));

        // listen to every single task
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, final Intent intent) {
                Log.v("long", "Some task is done! " + intent.getAction());
                final String filePath = intent.getStringExtra(DownloadTask.EXTRA_FILE_PATH);

                // add the the scene
                mScene.queueEvent(new Runnable() {

                    @Override
                    public void run() {
                        addObject(filePath, RANDOM.nextInt(mDisplaySize.x), RANDOM.nextInt(mDisplaySize.y));
                    }
                });
            }
        }, new IntentFilter(DownloadTask.INTENT_COMPLETE));
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.samples.activities.StageActivity#getLayout()
     */
    @Override
    protected int getLayout() {
        return R.layout.stage_loader;
    }

    private void addObject(final String textureFile, final float x, final float y) {
        final Texture texture = mScene.getTextureManager().createFileTexture(textureFile, (TextureOptions) null);

        // create object
        Sprite obj = new Sprite();
        obj.setTexture(texture);

        // center origin
        obj.setOriginAtCenter();

        // random positions
        obj.setPosition(x, y);

        // add to scene
        mScene.addChild(obj);
    }

    public void onClickLoad(final View view) {
        if (view.getId() == R.id.btn_load) {
            if (!mLoading) {
                mScene.queueEvent(new Runnable() {

                    @Override
                    public void run() {
                        mScene.removeAllChildren();
                    }
                });

                mStartTime = System.currentTimeMillis();
                startService(HelloLoaderService.getStartIntent());
                mLoading = true;

                ((Button) view).setText(R.string.loading);
            }
        }
    }
}

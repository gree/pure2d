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

import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.gl.gl10.textures.TextureOptions;
import com.funzio.pure2D.loaders.AsyncTaskExecuter;
import com.funzio.pure2D.loaders.tasks.DownloadTask;
import com.funzio.pure2D.loaders.tasks.Task;
import com.funzio.pure2D.shapes.Sprite;
import com.longo.pure2D.demo.R;

public class LoaderAsyncActivity extends StageActivity implements Task.TaskListener {
    public static final String DST_DIR = Environment.getExternalStorageDirectory() + "/Android/data/com.funzio.pure2D/";
    private static final boolean OVERRIDING = false;
    private final Task[] mTasks = {
            new DownloadTask("http://a4.mzstatic.com/us/r1000/106/Purple/v4/04/df/1d/04df1d83-ca73-1bc9-86ed-a8bfe54a54f2/mzl.bfeuaicq.320x480-75.jpg", DST_DIR + "ka_1.jpg", OVERRIDING), //
            new DownloadTask("http://a1.mzstatic.com/us/r1000/102/Purple/v4/4a/83/ae/4a83aebf-d211-06df-bdac-25ee0445fc37/mzl.tvdbyxif.320x480-75.jpg", DST_DIR + "ka_2.jpg", OVERRIDING), //
            new DownloadTask("http://a5.mzstatic.com/us/r1000/070/Purple/v4/33/ec/68/33ec68d7-eadc-67e1-1707-0b817d18c198/mzl.khtwjfxs.320x480-75.jpg", DST_DIR + "ka_3.jpg", OVERRIDING), //
            new DownloadTask("http://a5.mzstatic.com/us/r1000/119/Purple/v4/17/97/5f/17975f9b-b111-2c50-6958-aa3c2cf179ef/mzl.bcrjwems.320x480-75.jpg", DST_DIR + "ka_4.jpg", OVERRIDING)
    };

    private boolean mLoading = false;
    private int mLoadedFiles = 0;
    private long mStartTime;

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

    private void startLoad() {
        mStartTime = System.currentTimeMillis();
        mLoading = true;
        mLoadedFiles = 0;

        // change button label
        ((Button) findViewById(R.id.btn_load)).setText(R.string.loading);

        AsyncTaskExecuter<Task> executer = new AsyncTaskExecuter<Task>();
        executer.setTaskListener(this);
        executer.executeOnPool(mTasks);
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.loaders.tasks.Task.TaskListener#onTaskComplete(com.funzio.pure2D.loaders.tasks.Task)
     */
    @Override
    public void onTaskComplete(final Task task) {
        if (task != null && task.isSucceeded()) {
            // add the the scene
            mScene.queueEvent(new Runnable() {

                @Override
                public void run() {
                    addObject(((DownloadTask) task).getFilePath(), RANDOM.nextInt(mDisplaySize.x), RANDOM.nextInt(mDisplaySize.y));
                }
            });

            if (++mLoadedFiles == mTasks.length) {
                Log.v("long", "ALL TASKS ARE DONE!");
                Log.v("long", "Load time: " + (System.currentTimeMillis() - mStartTime));
                mLoading = false;

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // change button label
                        ((Button) findViewById(R.id.btn_load)).setText(R.string.start_loading);
                    }
                });
            }
        }
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

                startLoad();
            }
        }
    }
}

package com.funzio.pure2D.samples.activities.loaders;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.funzio.pure2D.R;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.gl.gl10.textures.TextureOptions;
import com.funzio.pure2D.loaders.tasks.DownloadTask;
import com.funzio.pure2D.samples.activities.StageActivity;
import com.funzio.pure2D.shapes.Sprite;

public class LoaderServiceActivity extends StageActivity {
    private boolean mLoading = false;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(final Context context, final Intent intent) {
                Log.v("long", "ALL TASKS ARE DONE! " + intent.getAction());
                mLoading = false;
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

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        // if (event.getAction() == MotionEvent.ACTION_DOWN) {
        // mStage.queueEvent(new Runnable() {
        //
        // @Override
        // public void run() {
        // addObject(event.getX(), mDisplaySize.y - event.getY());
        // }
        // });
        // }

        return true;
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

                startService(HelloLoaderService.getStartIntent());
                mLoading = true;
            }
        }
    }
}

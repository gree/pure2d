/**
 * 
 */
package com.funzio.pure2D.app;

import android.app.Application;
import android.os.Handler;
import android.widget.Toast;

import com.funzio.pure2D.sounds.SoundManager;
import com.funzio.pure2D.ui.UIManager;

/**
 * @author long.ngo
 */
public class Pure2DAppplication extends Application {

    protected UIManager mUIManager;
    protected SoundManager mSoundManager;

    protected Handler mHandler;
    protected Toast mCurrentToast;

    public Pure2DAppplication() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mHandler = new Handler();

        // init UI manager
        mUIManager = UIManager.getInstance();
        mUIManager.setContext(this);
        // load ui config
        mUIManager.loadConfig("ui_config.json");// .applyScale(0.5f);

        // create sound manager
        mSoundManager = createSoundManager();
    }

    protected SoundManager createSoundManager() {
        return new SoundManager(this, 10);
    }

    public UIManager getUIManager() {
        return mUIManager;
    }

    public SoundManager getSoundManager() {
        return mSoundManager;
    }

    public Handler getHandler() {
        return mHandler;
    }

    public void showToast(final int stringResId, final int duration) {
        showToast(getString(stringResId), duration);
    }

    public void showToast(final String text, final int duration) {

        mHandler.post(new Runnable() {

            @Override
            public void run() {
                if (mCurrentToast != null) {
                    mCurrentToast.cancel();
                    mCurrentToast = null;
                }

                mCurrentToast = Toast.makeText(Pure2DAppplication.this, text, duration);
                mCurrentToast.show();
            }
        });
    }

}

/**
 * 
 */
package com.funzio.pure2D.app;

import android.app.Application;

import com.funzio.pure2D.sounds.SoundManager;
import com.funzio.pure2D.ui.UIManager;

/**
 * @author long.ngo
 */
public class Pure2DAppplication extends Application {

    protected UIManager mUIManager;
    protected SoundManager mSoundManager;

    public Pure2DAppplication() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate() {
        super.onCreate();

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

}

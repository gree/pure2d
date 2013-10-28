/**
 * 
 */
package com.funzio.pure2D.demo;

import android.app.Application;

import com.funzio.pure2D.ui.UIManager;

/**
 * @author long.ngo
 */
public class Pure2DDemoApplication extends Application {

    private static Pure2DDemoApplication sInstance;

    private UIManager mUIManager = UIManager.getInstance();

    public Pure2DDemoApplication() {
        // singleton
        sInstance = this;
    }

    public static Pure2DDemoApplication getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mUIManager.setContext(this);
        // load ui config
        // mUIManager.loadConfig(getResources().getXml(R.xml.ui_config));
        mUIManager.loadConfig("ui_config.json");
    }

    public UIManager getUIManager() {
        return mUIManager;
    }

}

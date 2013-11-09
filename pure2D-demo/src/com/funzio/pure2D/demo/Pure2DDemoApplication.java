/**
 * 
 */
package com.funzio.pure2D.demo;

import com.funzio.pure2D.app.Pure2DAppplication;

/**
 * @author long.ngo
 */
public class Pure2DDemoApplication extends Pure2DAppplication {

    private static Pure2DDemoApplication sInstance;

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

        // TODO
    }

}

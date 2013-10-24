/**
 * 
 */
package com.funzio.pure2D.ui;


/**
 * @author long.ngo
 */
public class UIManager {

    // singleton
    private static UIManager sInstance = new UIManager();

    private UIManager() {

    }

    public static UIManager getInstance() {
        return sInstance;
    }

    public void reset() {
        // TODO
    }

}

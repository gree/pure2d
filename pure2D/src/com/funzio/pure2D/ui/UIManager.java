/**
 * 
 */
package com.funzio.pure2D.ui;

import java.util.HashMap;

/**
 * @author long.ngo
 */
public class UIManager {

    // singleton
    private static UIManager sInstance = new UIManager();

    private HashMap<String, UIObject> mNameMap = new HashMap<String, UIObject>();

    private UIManager() {

    }

    public static UIManager getInstance() {
        return sInstance;
    }

    public void reset() {
        // TODO
    }

    public UIObject getObjectByName(final String name) {
        return mNameMap.get(name);
    }

}

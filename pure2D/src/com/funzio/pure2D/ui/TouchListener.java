/**
 * 
 */
package com.funzio.pure2D.ui;

import com.funzio.pure2D.DisplayObject;

/**
 * @author long.ngo
 */
public interface TouchListener {
    public void onTouchDown(DisplayObject obj);

    public void onTouchUp(DisplayObject obj, boolean hit);
}

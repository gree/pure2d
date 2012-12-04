/**
 * 
 */
package com.funzio.pure2D;

import android.view.MotionEvent;

/**
 * @author long
 */
public interface Touchable {
    public boolean onTouchEvent(final MotionEvent event);

    public void setTouchable(boolean touchable);

    public boolean isTouchable();
}

/**
 * 
 */
package com.funzio.pure2D;

import android.view.MotionEvent;

/**
 * @author long
 */
public interface Touchable {
    /**
     * Note: This is called from UI-Thread
     */
    public boolean onTouchEvent(final MotionEvent event);

    public void setTouchable(boolean touchable);

    public boolean isTouchable();

    public void setModal(boolean modal);

    public boolean isModal();
}

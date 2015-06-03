package com.funzio.pure2D.ui;

import com.funzio.pure2D.DisplayObject;

/**
 * Created by longngo on 6/2/15.
 */
abstract public class AbstractTouchListener implements TouchListener {
    @Override
    public void onTouchDown(final DisplayObject obj) {
        // TODO override this
    }

    @Override
    public void onTouchUp(final DisplayObject obj, final boolean hit) {
        // TODO override this
    }
}
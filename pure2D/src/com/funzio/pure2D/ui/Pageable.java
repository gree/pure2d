/**
 * 
 */
package com.funzio.pure2D.ui;

import com.funzio.pure2D.DisplayObject;

/**
 * @author long.ngo
 */
public interface Pageable extends DisplayObject {

    public boolean isPageFloating();

    public void setPageFloating(final boolean pageFloating);

    public void transitionIn(final boolean pushing);

    public void transitionOut(final boolean pushing);

    public void setTransitionListener(final TransitionListener listener);

    public TransitionListener getTransitionListener();

    public boolean isPageActive();

    public static interface TransitionListener {
        public void onTransitionInComplete(final Pageable page);

        public void onTransitionOutComplete(final Pageable page);
    }

}

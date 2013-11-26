/**
 * 
 */
package com.funzio.pure2D.containers;

import com.funzio.pure2D.Contentable;
import com.funzio.pure2D.DisplayObject;

/**
 * @author long
 */
public interface Container extends Contentable {
    public boolean addChild(final DisplayObject child);

    public boolean addChild(final DisplayObject child, final int index);

    public boolean removeChild(final DisplayObject child);

    public DisplayObject getChildAt(final int index);

    public int getChildIndex(final DisplayObject child);

    public DisplayObject getChildById(final String id);

    public boolean swapChildren(final DisplayObject child1, final DisplayObject child2);

}

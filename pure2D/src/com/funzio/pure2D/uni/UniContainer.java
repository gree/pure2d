/**
 * 
 */
package com.funzio.pure2D.uni;

import com.funzio.pure2D.Contentable;
import com.funzio.pure2D.StackableObject;
import com.funzio.pure2D.gl.gl10.textures.Texture;

/**
 * @author long
 */
public interface UniContainer extends Contentable {
    public Texture getTexture();

    public void setTexture(final Texture texture);

    public boolean addChild(final StackableObject child);

    public boolean addChild(final StackableObject child, final int index);

    public boolean removeChild(final StackableObject child);

    public StackableObject getChildAt(final int index);

    public int getChildIndex(final StackableObject child);

    public StackableObject getChildById(final String id);

    public boolean swapChildren(final StackableObject child1, final StackableObject child2);

    public int getNumStackedChildren();

}

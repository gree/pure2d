/**
 * 
 */
package com.funzio.pure2D.uni;

import com.funzio.pure2D.Contentable;
import com.funzio.pure2D.gl.gl10.textures.Texture;

/**
 * @author long
 */
public interface UniContainer extends Contentable {
    public Texture getTexture();

    public void setTexture(final Texture texture);

    public boolean addChild(final Uniable child);

    public boolean addChild(final Uniable child, final int index);

    public boolean removeChild(final Uniable child);

    public Uniable getChildAt(final int index);

    public int getChildIndex(final Uniable child);

    public Uniable getChildById(final String id);

    public boolean swapChildren(final Uniable child1, final Uniable child2);

    public int getNumDrawingChildren();

}

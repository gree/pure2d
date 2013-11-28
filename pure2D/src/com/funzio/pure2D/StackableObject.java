/**
 * 
 */
package com.funzio.pure2D;

import com.funzio.pure2D.gl.gl10.ColorBuffer;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.gl.gl10.VertexBuffer;
import com.funzio.pure2D.gl.gl10.textures.TextureCoordBuffer;
import com.funzio.pure2D.uni.UniContainer;

/**
 * @author long.ngo
 */
public interface StackableObject extends Displayable {

    public int stack(final GLState glState, final int index, final VertexBuffer vertexBuffer, final ColorBuffer colorBuffer, final TextureCoordBuffer coordBuffer);

    public int getNumStackedChildren();

    /**
     * @hide for internal use
     */
    public void setStackable(final boolean value);

    /**
     * @hide for internal uses
     */
    public boolean isStackable();

    /**
     * @hide For internal use
     */
    public void onAdded(UniContainer container); // TODO change to Parentable and consolidate with Displayable

}

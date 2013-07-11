/**
 * 
 */
package com.funzio.pure2D.shapes;

import android.graphics.RectF;

import com.funzio.pure2D.gl.gl10.QuadBuffer;
import com.funzio.pure2D.gl.gl10.textures.TextureCoordBuffer;

/**
 * @author long
 */
public class Rectangular extends Shape {

    public Rectangular() {
        super();

        // use default texture coordinates
        setTextureCoordBuffer(TextureCoordBuffer.getDefault());
    }

    @Override
    public void setSize(final float w, final float h) {
        if (mVertexBuffer == null) {
            mVertexBuffer = new QuadBuffer(0, 0, w, h);
        } else {
            ((QuadBuffer) mVertexBuffer).setSize(w, h);
        }

        super.setSize(w, h);
    }

    public void setRect(final RectF rect) {
        mPosition.x = rect.left;
        mPosition.y = rect.top;
        setSize(rect.width(), rect.height());
    }
}

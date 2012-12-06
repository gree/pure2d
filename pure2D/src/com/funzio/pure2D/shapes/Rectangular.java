/**
 * 
 */
package com.funzio.pure2D.shapes;

import java.util.Arrays;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.RectF;

import com.funzio.pure2D.gl.gl10.VertexBuffer;
import com.funzio.pure2D.gl.gl10.textures.TextureCoordBuffer;

/**
 * @author long
 */
public class Rectangular extends Shape {
    private static final int VERTICES_NUM = 4;
    private float[] mVertices = new float[8];

    public Rectangular() {
        super();

        // init vertices
        Arrays.fill(mVertices, 0);

        // use default texture coordinates
        setTextureCoordBuffer(TextureCoordBuffer.getDefault());
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.DisplayObject#setSize(android.graphics.PointF)
     */
    @Override
    public void setSize(final float w, final float h) {
        super.setSize(w, h);

        // final float vertices[] = {
        // 0.0f, h, // TL
        // 0.0f, 0.0f, // BL
        // w, h, // TR
        // w, 0.0f, // BR
        // };
        mVertices[1] = h;
        mVertices[4] = w;
        mVertices[5] = h;
        mVertices[6] = w;

        if (mVertexBuffer == null) {
            mVertexBuffer = new VertexBuffer(GL10.GL_TRIANGLE_STRIP, VERTICES_NUM, mVertices);
        } else {
            mVertexBuffer.setVertices(GL10.GL_TRIANGLE_STRIP, VERTICES_NUM, mVertices);
        }
    }

    public void setRect(final RectF rect) {
        mPosition.x = rect.left;
        mPosition.y = rect.top;
        setSize(rect.width(), rect.height());
    }
}

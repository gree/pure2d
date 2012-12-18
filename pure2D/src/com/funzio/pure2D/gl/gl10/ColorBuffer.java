/**
 * 
 */
package com.funzio.pure2D.gl.gl10;

import javax.microedition.khronos.opengles.GL10;

import com.funzio.pure2D.gl.GLColor;
import com.funzio.pure2D.gl.GLFloatBuffer;

/**
 * @author long
 */
public class ColorBuffer extends GLFloatBuffer {

    public ColorBuffer(final float[] colors) {
        super(colors);
    }

    public ColorBuffer(final GLColor... colors) {
        setValues(colors);
    }

    public void setValues(final GLColor... colors) {
        final float[] values = new float[colors.length * 4];
        int index = 0;
        for (int i = 0; i < colors.length; i++) {
            values[index++] = colors[i].r;
            values[index++] = colors[i].g;
            values[index++] = colors[i].b;
            values[index++] = colors[i].a;
        }

        setValues(values);
    }

    public void apply(final GLState glState) {
        if (mBuffer != null) {
            // gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
            glState.setColorArrayEnabled(true);

            glState.mGL.glColorPointer(4, GL10.GL_FLOAT, 0, mBuffer);
        }
    }

    public void unapply(final GLState glState) {
        if (mBuffer != null) {
            // gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
            glState.setColorArrayEnabled(false);
        }
    }
}

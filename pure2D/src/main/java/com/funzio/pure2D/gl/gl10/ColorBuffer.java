/*******************************************************************************
 * Copyright (C) 2012-2014 GREE, Inc.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/
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

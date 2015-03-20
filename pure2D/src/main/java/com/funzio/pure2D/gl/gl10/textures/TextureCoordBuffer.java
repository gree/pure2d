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
package com.funzio.pure2D.gl.gl10.textures;

import android.graphics.PointF;

import com.funzio.pure2D.gl.GLFloatBuffer;
import com.funzio.pure2D.gl.gl10.GLState;

/**
 * @author long
 */
public class TextureCoordBuffer extends GLFloatBuffer {
    final private static float[] DEFAULT_COORDS = {
            0.0f, 0.0f, // TL
            0.0f, 1.0f, // BL
            1.0f, 0.0f, // TR
            1.0f, 1.0f, // BR
    };

    protected float[] mValues;

    public TextureCoordBuffer(final float... textCoords) {
        super(textCoords);
    }
    
    public static void getDefault(float[] values) {
        values[0] = DEFAULT_COORDS[0];
        values[1] = DEFAULT_COORDS[1];
        values[2] = DEFAULT_COORDS[2];
        values[3] = DEFAULT_COORDS[3];
        values[4] = DEFAULT_COORDS[4];
        values[5] = DEFAULT_COORDS[5];
        values[6] = DEFAULT_COORDS[6];
        values[7] = DEFAULT_COORDS[7];
    }
    
    @Override
    public void setValues(final float... values) {
        super.setValues(values);

        if (values != null) {
            if (mValues == null) {
                mValues = new float[values.length];
            }
            // store the values
            for (int i = 0; i < mValues.length; i++) {
                mValues[i] = values[i];
            }
        }
    }

    public float[] getValues() {
        return mValues;
    }

    public void setRectFlipVertical(final float x, final float y, final float width, final float height) {
        if (mValues == null || mValues.length < 8) {
            mValues = new float[8];
        }

        mValues[0] = x;
        mValues[1] = y + height;
        mValues[2] = x;
        mValues[3] = y;
        mValues[4] = x + width;
        mValues[5] = y + height;
        mValues[6] = x + width;
        mValues[7] = y;

        super.setValues(mValues);
    }

    public void scale(final float sx, final float sy) {
        // scale the values
        if (mValues != null) {
            for (int i = 0; i < mValues.length; i++) {
                if (i % 2 == 0) {
                    if (sx != 1) {
                        mValues[i] *= sx;
                    }
                } else if (sy != 1) {
                    mValues[i] *= sy;
                }
            }
            // apply
            setValues(mValues);
        }
    }

    public void scale(final PointF value) {
        scale(value.x, value.y);
    }

    public void apply(final GLState glState) {
        if (mBuffer != null) {
            // gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
            glState.setTextureCoordArrayEnabled(true);

            // gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mBuffer);
            glState.setTextureCoordBuffer(this);
        }
    }

    public void unapply(final GLState glState) {
        if (mBuffer != null) {
            // gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
            glState.setTextureCoordArrayEnabled(false);
        }
    }

    public static TextureCoordBuffer getDefault() {
        return new TextureCoordBuffer(DEFAULT_COORDS);
    }

    public void flipHorizontal() {
        float x = mValues[0];
        float y = mValues[1];
        // TL <-> TR
        mValues[0] = mValues[4];
        mValues[1] = mValues[5];
        mValues[4] = x;
        mValues[5] = y;

        x = mValues[2];
        y = mValues[3];
        // BL <-> BR
        mValues[2] = mValues[6];
        mValues[3] = mValues[7];
        mValues[6] = x;
        mValues[7] = y;

        setValues(mValues);
    }

    public void flipVertical() {
        float x = mValues[0];
        float y = mValues[1];
        // TL <-> BL
        mValues[0] = mValues[2];
        mValues[1] = mValues[3];
        mValues[2] = x;
        mValues[3] = y;

        x = mValues[4];
        y = mValues[5];
        // TR <-> BR
        mValues[4] = mValues[6];
        mValues[5] = mValues[7];
        mValues[6] = x;
        mValues[7] = y;

        setValues(mValues);
    }

    public void rotateCCW() {
        float x0 = mValues[0];
        float y0 = mValues[1];
        float x1 = mValues[2];
        float y1 = mValues[3];

        // TR
        mValues[0] = mValues[4];
        mValues[1] = mValues[5];
        // TL
        mValues[2] = x0;
        mValues[3] = y0;
        // BR
        mValues[4] = mValues[6];
        mValues[5] = mValues[7];
        // BL
        mValues[6] = x1;
        mValues[7] = y1;

        setValues(mValues);
    }

    // public boolean equals(final TextureCoordBuffer buffer) {
    // return this == buffer || compare(this, buffer);
    // }

    public static void scale(final float[] values, final float sx, final float sy) {
        // scale the values
        for (int i = 0; i < values.length; i++) {
            if (i % 2 == 0) {
                if (sx != 1) {
                    values[i] *= sx;
                }
            } else if (sy != 1) {
                values[i] *= sy;
            }
        }
    }

    // public static boolean compare(final TextureCoordBuffer a, final TextureCoordBuffer b) {
    // return Arrays.equals(a == null ? null : a.mValues, b == null ? null : b.mValues);
    // }

    public static boolean compare(final TextureCoordBuffer a, final TextureCoordBuffer b) {
        if (a == b) {
            return true;
        } else if (a == null || b == null) { // || a.mValues.length != b.mValues.length
            return false;
        } else {

            final float[] aValues = a.mValues;
            final float[] bValues = b.mValues;
            return aValues[0] == bValues[0] //
                    && aValues[1] == bValues[1] //
                    && aValues[2] == bValues[2] //
                    && aValues[3] == bValues[3] //
                    && aValues[4] == bValues[4] //
                    && aValues[5] == bValues[5] //
                    && aValues[6] == bValues[6] //
                    && aValues[7] == bValues[7];
        }

        // for (int i = 0; i < a.mValues.length; i++) {
        // if (a.mValues[i] != b.mValues[i]) {
        // return false;
        // }
        // }
    }

    @Override
    public String toString() {
        return "(" + mValues[0] + ", " + mValues[1] + ") " + "(" + mValues[2] + ", " + mValues[3] + ") " + "(" + mValues[4] + ", " + mValues[5] + ") " + "(" + mValues[6] + ", " + mValues[7] + ")";
    }
}

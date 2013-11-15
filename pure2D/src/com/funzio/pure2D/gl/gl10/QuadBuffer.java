/**
 * 
 */
package com.funzio.pure2D.gl.gl10;

import javax.microedition.khronos.opengles.GL10;

/**
 * @author long
 */
public class QuadBuffer extends VertexBuffer {
    private static final int NUM_VERTICES = 4;

    protected float[] mValues = new float[NUM_VERTICES * 2];
    protected float mX = 0;
    protected float mY = 0;
    protected float mWidth = 0;
    protected float mHeight = 0;

    public QuadBuffer() {
        super(GL10.GL_TRIANGLE_STRIP, NUM_VERTICES);
    }

    public QuadBuffer(final float x, final float y, final float width, final float height) {
        super(GL10.GL_TRIANGLE_STRIP, NUM_VERTICES);

        setRect(x, y, width, height);
    }

    public void setRect(final float x, final float y, final float width, final float height) {
        mX = x;
        mY = y;
        mWidth = width;
        mHeight = height;

        mValues[0] = x;
        mValues[1] = y + height;
        mValues[2] = x;
        mValues[3] = y;
        mValues[4] = x + width;
        mValues[5] = y + height;
        mValues[6] = x + width;
        mValues[7] = y;

        setValues(mValues);
    }

    public void setRectFlipVertical(final float x, final float y, final float width, final float height) {
        mX = x;
        mY = y;
        mWidth = width;
        mHeight = height;

        mValues[2] = x;
        mValues[3] = y + height;
        mValues[0] = x;
        mValues[1] = y;
        mValues[6] = x + width;
        mValues[7] = y + height;
        mValues[4] = x + width;
        mValues[5] = y;

        setValues(mValues);
    }

    public void setSize(final float width, final float height) {
        mWidth = width;
        mHeight = height;

        mValues[1] = mY + height;
        mValues[4] = mX + width;
        mValues[5] = mY + height;
        mValues[6] = mX + width;

        setValues(mValues);
    }

    public boolean hasSize() {
        return mWidth != 0 && mHeight != 0;
    }

    public float getWidth() {
        return mWidth;
    }

    public float getHeight() {
        return mHeight;
    }

    public static boolean compare(final QuadBuffer a, final QuadBuffer b) {
        if (a == b) {
            return true;
        } else if (a == null || b == null) {
            return false;
        } else {

            return a.mWidth == b.mWidth //
                    && a.mHeight == b.mHeight //
                    && a.mX == b.mX //
                    && a.mY == b.mY;
        }

    }
}

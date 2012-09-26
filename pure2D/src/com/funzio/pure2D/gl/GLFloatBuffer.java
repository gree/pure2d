/**
 * 
 */
package com.funzio.pure2D.gl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * @author long
 */
public class GLFloatBuffer {
    public FloatBuffer mBuffer;
    private int mCapacity = 0;

    public GLFloatBuffer() {
        // nothing now
    }

    public GLFloatBuffer(final float[] values) {
        setValues(values);
    }

    public void setValues(final float[] values) {
        // null check and create new buffer
        if (values != null) {
            if (mBuffer == null || mCapacity != values.length) {
                // a float is 4 bytes, therefore we multiply the number if vertices with 4.
                ByteBuffer bb = ByteBuffer.allocateDirect(values.length * 4);
                bb.order(ByteOrder.nativeOrder());
                mBuffer = bb.asFloatBuffer();
            } else {
                // recycle for perf
                mBuffer.clear();
            }

            mBuffer.put(values);
            mBuffer.flip();
            mCapacity = values.length;
        } else if (mBuffer != null) {
            mBuffer.clear();
            mBuffer = null;
            mCapacity = 0;
        }
    }

    public void dispose() {
        // texture coordinates buffer
        if (mBuffer != null) {
            mBuffer.clear();
            mBuffer = null;
            mCapacity = 0;
        }
    }
}

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

    public GLFloatBuffer(final float... values) {
        setValues(values);
    }

    public void setValues(final float... values) {
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

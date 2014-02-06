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
package com.funzio.pure2D.effects.sparks;

import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import com.funzio.pure2D.gl.GLColor;
import com.funzio.pure2D.gl.gl10.ColorBuffer;
import com.funzio.pure2D.gl.gl10.VertexBuffer;
import com.funzio.pure2D.gl.gl10.textures.TextureCoordBuffer;
import com.funzio.pure2D.shapes.Shape;

/**
 * @author long
 */
public class TriangleSpark extends Shape implements Sparkable {
    protected static final Random RANDOM = new Random();
    protected static final int VERTICES_NUM = 3;
    protected static final TextureCoordBuffer DEFAULT_TEXTURE_COORDS = TextureCoordBuffer.getDefault();
    protected static final GLColor[] DEFAULT_COLORS = new GLColor[] {
            new GLColor(1f, 1f, 1f, 1f), new GLColor(1f, 1f, 1f, 0f), new GLColor(1f, 1f, 1f, 0f)
    };

    public TriangleSpark() {
        super();

        // use default texture coordinates
        setTextureCoordBuffer(DEFAULT_TEXTURE_COORDS);

        // default colors
        setColorBuffer(new ColorBuffer(DEFAULT_COLORS));
    }

    @Override
    public void setSize(final float w, final float h) {
        super.setSize(w, h);

        final float vertices[] = {
                0, 0, // tip
                w, -h / 2f, // BR
                w, h / 2f, // TR
        };

        if (mVertexBuffer == null) {
            mVertexBuffer = new VertexBuffer(GL10.GL_TRIANGLE_STRIP, VERTICES_NUM, vertices);
        } else {
            mVertexBuffer.setVertices(GL10.GL_TRIANGLE_STRIP, VERTICES_NUM, vertices);
        }
    }

    public void setColors(final GLColor... colors) {
        mColorBuffer.setValues(colors);
    }

}

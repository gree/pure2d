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
package com.funzio.pure2D.shapes;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.PointF;
import android.view.animation.Interpolator;

import com.funzio.pure2D.Parentable;
import com.funzio.pure2D.StackableObject;
import com.funzio.pure2D.atlas.AtlasFrame;
import com.funzio.pure2D.geom.Line;
import com.funzio.pure2D.gl.GLColor;
import com.funzio.pure2D.gl.gl10.ColorBuffer;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.gl.gl10.QuadMeshBuffer;
import com.funzio.pure2D.gl.gl10.QuadMeshColorBuffer;
import com.funzio.pure2D.gl.gl10.VertexBuffer;
import com.funzio.pure2D.gl.gl10.textures.QuadMeshTextureCoordBuffer;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.gl.gl10.textures.TextureCoordBuffer;
import com.funzio.pure2D.uni.UniContainer;
import com.funzio.pure2D.utils.Pure2DUtils;

/**
 * @author long
 */
public class Polyline extends Shape implements StackableObject {

    protected static final int VERTEX_POINTER_SIZE = 2; // xy

    protected PointF[] mPoints;
    protected int mNumPointsUsed = 0; // <= mPoints.length
    protected float[] mTextureCoords;
    protected float mNarrowAngle = (float) (Math.PI / 4);
    protected float mStroke1 = 1;
    protected float mStroke2 = 1;

    protected GLColor[] mStrokeColors;
    protected float[] mColorValues;

    protected float[] mVertices;
    protected int mVerticesNum = 0;
    protected float mTotalLength;
    protected Interpolator mStrokeInterpolator = null;

    // texture caps
    protected float mTextureCap1 = 0;
    protected float mTextureCap2 = 0;
    protected boolean mTextureRepeating = false;

    // for texture animation
    protected AtlasFrame mAtlasFrame;

    // Uniable implementation
    protected boolean mStackable;
    protected UniContainer mUniParent;

    public PointF[] getPoints() {
        return mPoints;
    }

    final public void setPoints(final PointF... points) {
        setPoints(points.length, points);
    }

    public void setPoints(final int numPoints, final PointF... points) {
        // you don't have to use all the points
        mNumPointsUsed = Math.min(numPoints, points.length); // <= mPoints.length
        mPoints = points;

        invalidate(VERTICES);
    }

    protected void validateVertices() {
        final PointF[] points = mPoints;

        allocateVertices(mNumPointsUsed * 2, VERTEX_POINTER_SIZE);// each point has upper and lower points

        final float strokeDelta = mStroke2 - mStroke1;
        float dx, dy, segment = 0;
        float firstAngle = 0;
        float angle0 = 0;
        float angle1 = 0;
        float angleDelta = 0;
        float angleCut = 0;
        float rx = 0, ry = 0, r;
        float stroke = mStroke1;
        int i, vertexIndex = 0;
        float lastUX = 0;
        float lastUY = 0;
        PointF lastPoint = null, currentPoint;

        // find total segment
        mTotalLength = 0;
        for (i = 0; i < mNumPointsUsed - 1; i++) {
            dx = points[i + 1].x - points[i].x;
            dy = points[i + 1].y - points[i].y;

            mTotalLength += Math.sqrt(dx * dx + dy * dy);
        }

        // texture fitting
        int textureCoordIndex = 0;
        float textureCoordOffset = 0;
        float textureScale = 1;
        float textureWidth = 0;
        if (mTexture != null) {
            if (mAtlasFrame == null) {
                textureWidth = mTexture.getSize().x;
            } else {
                textureWidth = mAtlasFrame.getSize().x;
            }
            final int numCoords = (mNumPointsUsed + (mTextureCap1 > 0 ? 1 : 0) + (mTextureCap2 > 0 ? 1 : 0)) * 4;
            if (mTextureCoords == null || mTextureCoords.length < numCoords) {
                mTextureCoords = new float[numCoords];
            }

            // first point of the texture
            mTextureCoords[textureCoordIndex] = mTextureCoords[textureCoordIndex + 2] = textureCoordOffset;
            mTextureCoords[textureCoordIndex + 1] = 0;
            mTextureCoords[textureCoordIndex + 3] = 1;
            textureCoordIndex += 4;

            // first cap
            if (mTextureCap1 > 0) {
                textureCoordOffset += mTextureCoords[textureCoordIndex] = mTextureCoords[textureCoordIndex + 2] = mTextureCap1 / textureWidth;
                mTextureCoords[textureCoordIndex + 1] = 0;
                mTextureCoords[textureCoordIndex + 3] = 1;
                textureCoordIndex += 4;

                // also offset the vertex index
                vertexIndex += VERTEX_POINTER_SIZE * 2;
            }

            textureScale = 1 - (mTextureCap1 + mTextureCap2) / textureWidth;
        }

        for (i = 0; i < mNumPointsUsed; i++) {
            currentPoint = points[i];

            if (i < mNumPointsUsed - 1) {
                dx = points[i + 1].x - currentPoint.x;
                dy = points[i + 1].y - currentPoint.y;

                segment += Math.sqrt(dx * dx + dy * dy);

                if (mStrokeInterpolator != null) {
                    // interpolating
                    stroke = mStroke1 + mStrokeInterpolator.getInterpolation(segment / mTotalLength) * strokeDelta;
                } else {
                    // linear
                    stroke = mStroke1 + (segment / mTotalLength) * strokeDelta;
                }

                angle1 = (float) Math.atan2(dy, dx);
                if (i == 0) {
                    firstAngle = angle1;
                }

                // texture fitting
                if (mTexture != null) {
                    mTextureCoords[textureCoordIndex] = mTextureCoords[textureCoordIndex + 2] = textureCoordOffset + textureScale * segment / mTotalLength;
                    mTextureCoords[textureCoordIndex + 1] = 0;
                    mTextureCoords[textureCoordIndex + 3] = 1;
                    textureCoordIndex += 4;
                }
            }

            r = stroke * 0.5f;
            if (i == 0 || i == mNumPointsUsed - 1) {
                // beginning and closing cut
                angleCut = angle1 + Pure2DUtils.PI_D2;
            } else {
                angleDelta = (angle1 - angle0);
                // check narrow/opposite angle
                if (Math.abs(Math.abs(angleDelta) - Math.PI) > mNarrowAngle) {
                    angleCut = angle1 + Pure2DUtils.PI_D2 - angleDelta * 0.5f;
                    r /= (float) Math.cos(angleDelta * 0.5f);
                } else {
                    angleCut = angle1 + Pure2DUtils.PI_D2;
                }
            }

            rx = r * (float) Math.cos(angleCut);
            ry = r * (float) Math.sin(angleCut);
            if (lastPoint != null) {
                // check crossing lines
                if (Line.linesIntersect(lastPoint.x, lastPoint.y, currentPoint.x, currentPoint.y, lastUX, lastUY, currentPoint.x + rx, currentPoint.y + ry)) {
                    // invert
                    rx = -rx;
                    ry = -ry;
                }
            }

            // upper point
            mVertices[vertexIndex] = lastUX = currentPoint.x + rx;
            mVertices[vertexIndex + 1] = lastUY = currentPoint.y + ry;
            // lower point
            mVertices[vertexIndex + 2] = currentPoint.x - rx;
            mVertices[vertexIndex + 3] = currentPoint.y - ry;

            vertexIndex += VERTEX_POINTER_SIZE * 2;

            angle0 = angle1;
            lastPoint = currentPoint;
        }

        // texture fitting
        if (mTexture != null) {
            if (mTextureCap1 > 0) {
                // prepend the first vertices
                dx = mTextureCap1 * (float) Math.cos(firstAngle + Math.PI);
                dy = mTextureCap1 * (float) Math.sin(firstAngle + Math.PI);
                mVertices[0] = mVertices[4] + dx;
                mVertices[1] = mVertices[5] + dy;
                // lower point
                mVertices[2] = mVertices[6] + dx;
                mVertices[3] = mVertices[7] + dy;
            }

            // texture's end cap
            if (mTextureCap2 > 0) {
                // append the last texture coords
                mTextureCoords[textureCoordIndex] = mTextureCoords[textureCoordIndex + 2] = 1;
                mTextureCoords[textureCoordIndex + 1] = 0;
                mTextureCoords[textureCoordIndex + 3] = 1;
                textureCoordIndex += 4;

                // append the last vertices
                dx = mTextureCap2 * (float) Math.cos(angle1);
                dy = mTextureCap2 * (float) Math.sin(angle1);
                mVertices[vertexIndex] = mVertices[vertexIndex - 4] + dx;
                mVertices[vertexIndex + 1] = mVertices[vertexIndex - 3] + dy;
                // lower point
                mVertices[vertexIndex + 2] = mVertices[vertexIndex - 2] + dx;
                mVertices[vertexIndex + 3] = mVertices[vertexIndex - 1] + dy;
            }

            // texture repeating?
            if (mTextureRepeating) {
                final float sx = (mTotalLength + mTextureCap1 + mTextureCap2) / textureWidth;
                final int pairs = mTextureCoords.length / 2;
                for (int n = 0; n < pairs; n++) {
                    // apply to x only
                    mTextureCoords[n * 2] *= sx;
                }
            }

            // check atlas frame
            if (mAtlasFrame != null) {
                final float[] coords = mAtlasFrame.getTextureCoords();
                final float ox = coords[0];
                final float oy = coords[1];
                final float w = coords[4] - ox;
                final float h = coords[3] - oy;
                final int pairs = mTextureCoords.length / 2;
                int index = 0;
                for (int t = 0; t < pairs; t++) {
                    mTextureCoords[index] = ox + w * mTextureCoords[index];
                    mTextureCoords[index + 1] = oy + h * mTextureCoords[index + 1];
                    index += 2;
                }

            }

            // optional for Uni Parent
            if (mParent != null) {
                setTextureCoordBuffer(mTextureCoords);
            }
        }

        // optional for Uni Parent
        if (mParent != null) {
            if (mVertexBuffer == null) {
                mVertexBuffer = new VertexBuffer(GL10.GL_TRIANGLE_STRIP, mVerticesNum, mVertices);
            } else {
                mVertexBuffer.setVertices(GL10.GL_TRIANGLE_STRIP, mVerticesNum, mVertices);
            }
        }

        validate(VERTICES);
    }

    @Override
    public boolean update(final int deltaTime) {
        if ((mInvalidateFlags & (VERTICES | PARENT)) > 0) {
            if (mPoints != null && mNumPointsUsed > 0) {
                // allocate more/less points if necessary
                validateVertices();
            }
        }

        return super.update(deltaTime);
    }

    @Override
    public void setTexture(final Texture texture) {
        super.setTexture(texture);

        invalidate(VERTICES);
    }

    /**
     * Set the beginning and end caps' widths. This works similarly as the 9-patch technique.
     * 
     * @param cap1
     * @param cap2
     */
    public void setTextureCaps(final float cap1, final float cap2) {
        mTextureCap1 = cap1;
        mTextureCap2 = cap2;

        invalidate(VERTICES);
    }

    /**
     * Repeat the texture to fill the body of this polyline
     * 
     * @param repeating
     */
    public void setTextureRepeating(final boolean repeating) {
        mTextureRepeating = repeating;

        invalidate(VERTICES);
    }

    @Override
    public boolean draw(final GLState glState) {
        if (mTotalLength >= 1) {
            return super.draw(glState);
        } else {
            return false;
        }
    }

    protected void allocateVertices(final int numVertices, final int vertexSize) {
        mVerticesNum = numVertices; // each point has upper and lower points
        if (mTextureCap1 > 0) {
            mVerticesNum += 2;
        }
        if (mTextureCap2 > 0) {
            mVerticesNum += 2;
        }

        // NOTE: only re-allocate when the required size is bigger
        if (mVertices == null || mVerticesNum * vertexSize > mVertices.length) {
            mVertices = new float[mVerticesNum * vertexSize];

            // only set colors ONCE!
            setStrokeColors(mStrokeColors);
        }
    }

    public void setStrokeRange(final float stroke1, final float stroke2) {
        mStroke1 = stroke1;
        mStroke2 = stroke2;

        invalidate(VERTICES);
    }

    /**
     * Set colors for up to 4 corners, in N-shape order
     */
    public void setStrokeColors(final GLColor... colors) {
        mStrokeColors = colors;

        if (mPoints == null || mPoints.length == 0 || colors == null || colors.length == 0) {
            mColorValues = null;
            mColorBuffer = null;
            return;
        }

        GLColor color1, color2, color3, color4;

        color1 = colors[0];
        if (colors.length >= 2 && colors[1] != null) {
            color2 = colors[1];

            if (colors.length >= 3 && colors[2] != null) {
                color3 = colors[2];
                if (colors.length >= 4 && colors[3] != null) {
                    color4 = colors[3];
                } else {
                    color4 = color2;
                }
            } else {
                color3 = color1;
                color4 = color2;
            }
        } else {
            color4 = color3 = color2 = color1;
        }

        if (mColorValues == null || (mVerticesNum * 4) > mColorValues.length) {
            mColorValues = new float[mVerticesNum * 4]; // each vertex has 4 floats
        }

        final int range = mPoints.length - 1;
        float udr = (color2.r - color1.r) / range;
        float udg = (color2.g - color1.g) / range;
        float udb = (color2.b - color1.b) / range;
        float uda = (color2.a - color1.a) / range;
        float ur = color1.r;
        float ug = color1.g;
        float ub = color1.b;
        float ua = color1.a;
        float ldr = (color4.r - color3.r) / range;
        float ldg = (color4.g - color3.g) / range;
        float ldb = (color4.b - color3.b) / range;
        float lda = (color4.a - color3.a) / range;
        float lr = color3.r;
        float lg = color3.g;
        float lb = color3.b;
        float la = color3.a;
        int index = 0;
        for (int i = 0; i <= range; i++) {
            // upper point
            mColorValues[index++] = ur;
            mColorValues[index++] = ug;
            mColorValues[index++] = ub;
            mColorValues[index++] = ua;

            // lower point
            mColorValues[index++] = lr;
            mColorValues[index++] = lg;
            mColorValues[index++] = lb;
            mColorValues[index++] = la;

            ur += udr;
            ug += udg;
            ub += udb;
            ua += uda;

            lr += ldr;
            lg += ldg;
            lb += ldb;
            la += lda;
        }

        if (mColorBuffer == null) {
            mColorBuffer = new ColorBuffer(mColorValues);
        } else {
            mColorBuffer.setValues(mColorValues);
        }

    }

    public Interpolator getStrokeInterpolator() {
        return mStrokeInterpolator;
    }

    public void setStrokeInterpolator(final Interpolator strokeInterpolator) {
        mStrokeInterpolator = strokeInterpolator;
    }

    public float getTotalLength() {
        return mTotalLength;
    }

    public void setAtlasFrame(final AtlasFrame frame) {
        if (frame != null) {

            // if there is a specific texture
            final Texture frameTexture = frame.getTexture();
            if (frameTexture != null) {
                setTexture(frameTexture);
            }
        }

        mAtlasFrame = frame;
        invalidate(FRAME | VERTICES);
    }

    public AtlasFrame getAtlasFrame() {
        return mAtlasFrame;
    }

    // Uni implementation ///////////////////////////////////////

    @Override
    public int stack(final GLState glState, final int index, final VertexBuffer vertexBuffer, final ColorBuffer colorBuffer, final TextureCoordBuffer coordBuffer) {
        final int numCells = mNumPointsUsed - 1;
        int verIndex = 0;
        int colorIndex = 0;
        int coordIndex = 0;
        for (int i = 0; i < numCells; i++) {
            // update vertices
            ((QuadMeshBuffer) vertexBuffer).setValuesAt(index + i, 1, verIndex, mVertices);

            // update colors
            if (mColorValues != null) {
                ((QuadMeshColorBuffer) colorBuffer).setValuesAt(index + i, 1, colorIndex, mColorValues);
            }

            // optional
            if (coordBuffer != null && mTextureCoords != null) {
                ((QuadMeshTextureCoordBuffer) coordBuffer).setValuesAt(index + i, 1, coordIndex, mTextureCoords);
            }

            verIndex += 4;
            colorIndex += 8;
            coordIndex += 4;
        }

        return numCells;
    }

    @Override
    public int getNumStackedChildren() {
        return mNumPointsUsed - 1;
    }

    @Override
    public void setStackable(final boolean value) {
        mStackable = value;

    }

    @Override
    public boolean isStackable() {
        return mStackable;
    }

    @Override
    final public Parentable getParent() {
        return mParent != null ? mParent : mUniParent;
    }

    @Override
    public void onAdded(final UniContainer container) {
        mUniParent = container;

        // apply texture
        setTexture(container.getTexture());
    }

    @Override
    public void onRemoved() {
        super.onRemoved();

        mUniParent = null;
    }

}

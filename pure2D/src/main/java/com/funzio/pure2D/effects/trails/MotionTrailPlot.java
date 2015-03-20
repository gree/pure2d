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
package com.funzio.pure2D.effects.trails;

import android.graphics.PointF;

import com.funzio.pure2D.BaseDisplayObject;
import com.funzio.pure2D.Manipulatable;
import com.funzio.pure2D.Parentable;
import com.funzio.pure2D.Scene;
import com.funzio.pure2D.StackableObject;
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

/**
 * @author long
 */
public class MotionTrailPlot extends BaseDisplayObject implements MotionTrail, StackableObject {
    public static final int DEFAULT_PLOT_SIZE = 10;
    public static final int DEFAULT_NUM_POINTS = 10;
    public static final float DEFAULT_MOTION_EASING = 0.5f;

    protected int mNumPoints = DEFAULT_NUM_POINTS;
    protected float mMotionEasingX = DEFAULT_MOTION_EASING;
    protected float mMotionEasingY = DEFAULT_MOTION_EASING;
    protected int mMinLength = 0;
    protected int mSegmentLength = 0;

    protected PointF[] mPoints;

    protected Manipulatable mTarget;
    protected PointF mTargetOffset = new PointF(0, 0);
    protected Object mData;
    private boolean mFollowingHead = false;

    protected Texture mTexture;
    protected float mTextureWidth = DEFAULT_PLOT_SIZE;
    protected float mTextureHeight = DEFAULT_PLOT_SIZE;
    protected float mTextureScaleX = 1, mTextureScaleY = 1;

    protected QuadMeshBuffer mMeshBuffer;
    protected QuadMeshTextureCoordBuffer mTextureCoordBuffer;
    protected QuadMeshColorBuffer mColorBuffer;

    protected GLColor mColor1 = null;
    protected GLColor mColor2 = null;
    protected float mScale1 = 1;
    protected float mScale2 = 1;

    // Uniable implementation
    protected boolean mStackable;
    protected UniContainer mUniParent;

    public MotionTrailPlot() {
        this(null);
    }

    public MotionTrailPlot(final Manipulatable target) {
        super();

        mMeshBuffer = new QuadMeshBuffer(mNumPoints);

        // set default num points
        setNumPoints(mNumPoints);

        if (target != null) {
            setTarget(target);
        }

    }

    @Override
    public void reset(final Object... params) {
        mMotionEasingX = mMotionEasingY = DEFAULT_MOTION_EASING;
    }

    @Override
    public Object getData() {
        return mData;
    }

    @Override
    public void setData(final Object data) {
        mData = data;
    }

    public void setColorRange(final GLColor color1, final GLColor color2) {
        mColor1 = color1;
        mColor2 = color2;

        invalidate(COLOR);
    }

    public void setScaleRange(final float scale1, final float scale2) {
        mScale1 = scale1;
        mScale2 = scale2;

        invalidate(SCALE);
    }

    @Override
    public void setPosition(final float x, final float y) {
        if (mNumPoints > 0) {
            if (!mPoints[0].equals(x, y)) {

                mPoints[0].set(x, y);

                // flag
                mFollowingHead = true;
            }
        }

        invalidate(CHILDREN);
    }

    @Override
    public void move(final float dx, final float dy) {
        if (mNumPoints > 0) {
            mPoints[0].offset(dx, dy);

            // flag
            mFollowingHead = true;
        }

        invalidate(CHILDREN);
    }

    @Override
    public boolean update(final int deltaTime) {
        if (mNumPoints > 0) {

            if (!mFollowingHead && mTarget != null) {
                final PointF p1 = mTarget.getPosition();
                final float dx = p1.x - (mPoints[0].x - mTargetOffset.x);
                final float dy = p1.y - (mPoints[0].y - mTargetOffset.y);
                if (Math.abs(dx) >= 1 || Math.abs(dy) >= 1) {
                    // flag
                    mFollowingHead = true;
                }
            }

            final boolean changedTextureCoords = mTexture != null && (mInvalidateFlags & (TEXTURE_COORDS | CHILDREN)) != 0;
            final boolean changedColors = (mColor1 != null && mColor2 != null) && (mInvalidateFlags & (COLOR | CHILDREN)) != 0;
            if (mFollowingHead || changedTextureCoords || changedColors) {

                // calculate time loop for consistency with different framerate
                final int loop = deltaTime / Scene.DEFAULT_MSPF;
                PointF p1, p2;
                float dx, dy;
                for (int n = 0; n < loop; n++) {
                    for (int i = mNumPoints - 1; i > 0; i--) {
                        p1 = mPoints[i];
                        p2 = mPoints[i - 1];
                        dx = p2.x - p1.x;
                        dy = p2.y - p1.y;
                        if (mMinLength == 0 || (dx * dx + dy * dy) > (mSegmentLength * mSegmentLength)) {
                            // move toward the leading point
                            p1.x += dx * mMotionEasingX;
                            p1.y += dy * mMotionEasingY;
                        }
                    }
                }

                // follow the target
                if (mTarget != null) {
                    // set the head
                    final PointF pos = mTarget.getPosition();
                    mPoints[0].set(pos.x + mTargetOffset.x, pos.y + mTargetOffset.y);
                }

                // validate texture coords
                if (changedTextureCoords) {
                    if (mTextureCoordBuffer == null) {
                        mTextureCoordBuffer = new QuadMeshTextureCoordBuffer(mNumPoints);
                    } else if (mTextureCoordBuffer.getNumCells() < mNumPoints) {
                        mTextureCoordBuffer.setNumCells(mNumPoints);
                    }
                }

                // validate color
                if (changedColors) {
                    if (mColorBuffer == null) {
                        mColorBuffer = new QuadMeshColorBuffer(mNumPoints);
                    } else if (mColorBuffer.getNumCells() < mNumPoints) {
                        mColorBuffer.setNumCells(mNumPoints);
                    }
                }

                // apply to the mesh
                if (mMeshBuffer.getNumCells() < mNumPoints) {
                    mMeshBuffer.setNumCells(mNumPoints);
                }
                PointF point;
                final float scaleStep = (mScale2 - mScale1) / mNumPoints;
                float width = mTextureWidth, height = mTextureHeight, scale = mScale1;
                final boolean flippedAxis = mScene != null && mScene.getAxisSystem() == Scene.AXIS_TOP_LEFT;
                final float dr = changedColors ? (mColor2.r - mColor1.r) / mNumPoints : 0;
                final float dg = changedColors ? (mColor2.g - mColor1.g) / mNumPoints : 0;
                final float db = changedColors ? (mColor2.b - mColor1.b) / mNumPoints : 0;
                final float da = changedColors ? (mColor2.a - mColor1.a) / mNumPoints : 0;
                // draw backward from tail to head
                for (int i = 0; i < mNumPoints; i++) {
                    point = mPoints[i];

                    // scale interpolation
                    if (scaleStep != 0) {
                        width = mTextureWidth * scale;
                        height = mTextureWidth * scale;
                        scale += scaleStep;
                    }
                    mMeshBuffer.setRectAt(mNumPoints - i - 1, point.x - width * 0.5f, point.y - height * 0.5f, width, height);

                    // make texture coords
                    if (changedTextureCoords) {
                        // apply the coordinates
                        if (flippedAxis) {
                            mTextureCoordBuffer.setRectFlipVerticalAt(i, 0, 0, mTexture.mCoordScaleX, mTexture.mCoordScaleY);
                        } else {
                            mTextureCoordBuffer.setRectAt(i, 0, 0, mTexture.mCoordScaleX, mTexture.mCoordScaleY);
                        }
                    }

                    // make colors
                    if (changedColors) {
                        // color interpolation
                        mColorBuffer.setColorAt(mNumPoints - i - 1, mColor1.r + dr * i, mColor1.g + dg * i, mColor1.b + db * i, mColor1.a + da * i);
                    }
                }

                // apply values to the vertex buffer
                mMeshBuffer.setIndicesNumUsed(mNumPoints * QuadMeshBuffer.NUM_INDICES_PER_CELL);
                // mMeshBuffer.validate();

                // apply coords
                // if (mTexture != null && mTextureCoordBuffer != null) {
                // mTextureCoordBuffer.validate();
                // }

                // apply colors
                // if (mColor1 != mColor2 && mColorBuffer != null) {
                // mColorBuffer.validate();
                // }

            }
        }

        return super.update(deltaTime);
    }

    @Override
    public boolean draw(final GLState glState) {
        super.draw(glState);

        // validate visual only
        mInvalidateFlags &= ~(VISUAL | CHILDREN);

        return mNumPoints > 0;
    }

    @Override
    protected boolean drawChildren(final GLState glState) {

        if (mNumPoints > 0) {
            // color buffer
            if (mColor1 != mColor2 && mColorBuffer != null) {
                // apply color buffer
                mColorBuffer.apply(glState);
            } else {
                glState.setColorArrayEnabled(false);
            }

            // texture
            if (mTexture != null) {
                // bind the texture
                mTexture.bind();
            } else {
                // unbind the texture
                glState.unbindTexture();
            }

            if (mTexture != null && mTextureCoordBuffer != null) {
                mTextureCoordBuffer.apply(glState);
            }

            // draw
            mMeshBuffer.draw(glState);
        }

        return mNumPoints > 0;
    }

    public Texture getTexture() {
        return mTexture;
    }

    public void setTexture(final Texture texture) {
        mTexture = texture;
        if (mTexture != null) {
            mTextureWidth = mTexture.getSize().x;
            mTextureHeight = mTexture.getSize().y;
        } else {
            // default size
            mTextureWidth = mTextureHeight = DEFAULT_PLOT_SIZE;
        }

        invalidate(TEXTURE | TEXTURE_COORDS);
    }

    public PointF[] getPoints() {
        return mPoints;
    }

    public void setPoints(final PointF... points) {
        mPoints = points;

        invalidate(CHILDREN);
    }

    public int getNumPoints() {
        return mNumPoints;
    }

    public void setNumPoints(final int numPoints) {
        mNumPoints = numPoints;

        if (numPoints < 1) {
            mPoints = null;
            return;
        }

        if (mPoints == null || mPoints.length != numPoints) {
            mPoints = new PointF[numPoints];

            final PointF pos = (mTarget != null) ? mTarget.getPosition() : null;
            for (int i = 0; i < numPoints; i++) {
                mPoints[i] = new PointF();

                if (pos != null) {
                    mPoints[i].set(pos.x + mTargetOffset.x, pos.y + mTargetOffset.y);
                }
            }

            // find the length
            mSegmentLength = mMinLength / (mNumPoints < 2 ? 1 : mNumPoints - 1);

            // optimize
            mFollowingHead = false;

            invalidate(CHILDREN);
        }
    }

    public Manipulatable getTarget() {
        return mTarget;
    }

    public void setTarget(final Manipulatable target) {
        mTarget = target;

        if (mTarget != null) {
            final PointF pos = mTarget.getPosition();
            for (int i = 0; i < mNumPoints; i++) {
                mPoints[i].set(pos.x + mTargetOffset.x, pos.y + mTargetOffset.y);
            }
        }

        // apply
        setPoints(mPoints);
    }

    public void setPointsAt(final float x, final float y, final float z) {
        for (int i = 0; i < mNumPoints; i++) {
            mPoints[i].set(x, y);
        }

        // apply
        setPoints(mPoints);
    }

    public void setPointsAt(final PointF p) {
        for (int i = 0; i < mNumPoints; i++) {
            mPoints[i].set(p.x, p.y);
        }

        // apply
        setPoints(mPoints);
    }

    public int getMinLength() {
        return mMinLength;
    }

    public void setMinLength(final int totalLength) {
        mMinLength = totalLength;
        mSegmentLength = mMinLength / (mNumPoints < 2 ? 1 : mNumPoints - 1);
    }

    public float getMotionEasingX() {
        return mMotionEasingX;
    }

    public float getMotionEasingY() {
        return mMotionEasingY;
    }

    /**
     * @param easing, must be from 0 to 1
     */
    public void setMotionEasing(final float easing) {
        mMotionEasingX = mMotionEasingY = easing;
    }

    public void setMotionEasing(final float easingX, final float easingY) {
        mMotionEasingX = easingX;
        mMotionEasingY = easingY;
    }

    public PointF getTargetOffset() {
        return mTargetOffset;
    }

    public void setTargetOffset(final float offsetX, final float offsetY) {
        mTargetOffset.set(offsetX, offsetY);
    }

    @Override
    public void dispose() {
        super.dispose();

        mPoints = null;

        if (mMeshBuffer != null) {
            mMeshBuffer.dispose();
            mMeshBuffer = null;
        }

        if (mTextureCoordBuffer != null) {
            mTextureCoordBuffer.dispose();
            mTextureCoordBuffer = null;
        }

        if (mColorBuffer != null) {
            mColorBuffer.dispose();
            mColorBuffer = null;
        }

    }

    // Uni implementation ///////////////////////////////////////

    @Override
    public int stack(final GLState glState, final int index, final VertexBuffer vertexBuffer, final ColorBuffer colorBuffer, final TextureCoordBuffer coordBuffer) {

        // update vertices
        ((QuadMeshBuffer) vertexBuffer).setValuesAt(index, mNumPoints, mMeshBuffer.getVertices());

        // update colors
        if (mColorBuffer != null) {
            ((QuadMeshColorBuffer) colorBuffer).setValuesAt(index, mNumPoints, mColorBuffer.getValues());
        }

        // optional
        if (coordBuffer != null && mTextureCoordBuffer != null) {
            ((QuadMeshTextureCoordBuffer) coordBuffer).setValuesAt(index, mNumPoints, mTextureCoordBuffer.getValues());
        }

        return mNumPoints;
    }

    @Override
    public int getNumStackedChildren() {
        return mNumPoints;
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

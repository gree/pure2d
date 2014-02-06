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
package com.funzio.pure2D;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;

import com.funzio.pure2D.animators.Manipulator;
import com.funzio.pure2D.gl.gl10.GLState;

/**
 * @author long
 */
public class Camera implements Manipulatable {
    protected static final float[] mScratch = new float[4];

    // dimensions and size
    protected PointF mCenter = new PointF(0, 0);
    protected PointF mSize = new PointF(0, 0);
    protected float mRotation = 0;
    protected PointF mZoom = new PointF(1, 1);
    protected Scene mScene;
    protected int mAxisSystem;

    protected RectF mZoomRect = new RectF();
    protected PointF mHalfSize = new PointF(0, 0);
    protected boolean mInvalidated;

    protected ArrayList<Manipulator> mManipulators;
    protected int mNumManipulators = 0;

    protected boolean mClipping = false;
    protected RectF mBounds = new RectF(); // the bounds that contains the rect (with/without rotation)
    private Matrix mMatrix;
    private Matrix mInvertedMatrix;

    public Camera(final PointF size) {
        this(size.x, size.y);
    }

    public Camera(final float width, final float height) {
        mSize.x = width;
        mSize.y = height;
        mCenter.x = width / 2;
        mCenter.y = height / 2;
        // pre-cal
        mHalfSize.x = mSize.x / 2;
        mHalfSize.y = mSize.y / 2;
        invalidate();
    }

    public Camera(final PointF center, final PointF size) {
        mSize.x = size.x;
        mSize.y = size.y;
        mCenter.x = center.x;
        mCenter.y = center.y;
        // pre-cal
        mHalfSize.x = mSize.x / 2;
        mHalfSize.y = mSize.y / 2;
        invalidate();
    }

    public Camera(final Scene scene) {
        mSize.x = scene.getSize().x;
        mSize.y = scene.getSize().y;
        mCenter.x = mSize.x / 2;
        mCenter.y = mSize.y / 2;
        // pre-cal
        mHalfSize.x = mSize.x / 2;
        mHalfSize.y = mSize.y / 2;
        invalidate();
    }

    /**
     * @return the Center
     */
    public PointF getPosition() {
        return mCenter;
    }

    /**
     * @param center the Center to set
     */
    final public void setPosition(final PointF center) {
        setPosition(center.x, center.y);
    }

    /**
     * @param Center the Center to set
     */
    public void setPosition(final float x, final float y) {
        mCenter.x = x;
        mCenter.y = y;
        invalidate();
    }

    public void setX(final float x) {
        mCenter.x = x;
        invalidate();
    }

    final public float getX() {
        return mCenter.x;
    }

    public void setY(final float y) {
        mCenter.y = y;
        invalidate();
    }

    final public float getY() {
        return mCenter.y;
    }

    public void moveTo(final float x, final float y) {
        mCenter.x = x;
        mCenter.y = y;
        invalidate();
    }

    public void move(final float x, final float y) {
        mCenter.x += x;
        mCenter.y += y;
        invalidate();
    }

    /**
     * @return the size
     */
    public PointF getSize() {
        return mSize;
    }

    /**
     * @param size the size to set
     */
    final public void setSize(final PointF size) {
        setSize(size.x, size.y);
    }

    /**
     * @param size the size to set
     */
    public void setSize(final float w, final float h) {
        mSize.x = w;
        mSize.y = h;

        // pre-cal
        mHalfSize.x = w / 2;
        mHalfSize.y = h / 2;

        invalidate();
    }

    final public float getWidth() {
        return mSize.x;
    }

    final public float getHeight() {
        return mSize.y;
    }

    public void setRotation(final float degree) {
        mRotation = degree;
        invalidate();
    }

    public float getRotation() {
        return mRotation;
    }

    public void rotate(final float degreeDelta) {
        mRotation += degreeDelta;
        invalidate();
    }

    /**
     * @return the zoom
     */
    public PointF getZoom() {
        return mZoom;
    }

    /**
     * @param zoom the zoom to set
     */
    public void setZoom(final float zoom) {
        mZoom.x = mZoom.y = zoom;
        invalidate();
    }

    public void zoomBy(final float delta) {
        mZoom.x += delta;
        mZoom.y += delta;
        invalidate();
    }

    @Override
    public void setScale(final float sx, final float sy) {
        mZoom.x = 1 / sx;
        mZoom.y = 1 / sy;
        invalidate();
    }

    @Override
    public void setScale(final float scale) {
        mZoom.x = mZoom.y = 1 / scale;
        invalidate();
    }

    @Override
    public PointF getScale() {
        return new PointF(1 / mZoom.x, 1 / mZoom.y);
    }

    /**
     * @return the zoom rect
     */
    public RectF getZoomRect() {
        return mZoomRect;
    }

    /**
     * @return the scene
     */
    public Scene getScene() {
        return mScene;
    }

    /**
     * @param scene the scene to set
     */
    public void setScene(final Scene scene) {
        mScene = scene;

        // invalidate scene to apply
        if (mScene != null) {
            mAxisSystem = mScene.getAxisSystem();
            mInvalidated = true;
            mScene.invalidate();
        }
    }

    public void invalidate() {
        mInvalidated = true;

        if (mScene != null) {
            mScene.invalidate();
        }
    }

    public boolean update(final int deltaTime) {
        // update the manipulators if there's any
        if (mNumManipulators > 0) {
            for (int i = 0; i < mNumManipulators; i++) {
                mManipulators.get(i).update(deltaTime);
            }
            return true;
        }

        return false;
    }

    protected void validate() {
        // prepare the rect
        mZoomRect.left = mCenter.x - mHalfSize.x / mZoom.x;
        mZoomRect.top = mCenter.y - mHalfSize.y / mZoom.y;
        mZoomRect.right = mCenter.x + mHalfSize.x / mZoom.x;
        mZoomRect.bottom = mCenter.y + mHalfSize.y / mZoom.y;

        // re-cal the bounds
        if (mRotation != 0) {
            if (mMatrix == null) {
                mMatrix = new Matrix();
                mInvertedMatrix = new Matrix();
            }
            // apply to matrices
            mMatrix.setRotate(mRotation, mCenter.x, mCenter.y);
            mInvertedMatrix.setRotate(-mRotation, mCenter.x, mCenter.y);

            // map the bounds
            mMatrix.mapRect(mBounds, mZoomRect);
        } else {
            mBounds.set(mZoomRect);
        }

        mInvalidated = false;
    }

    public void apply(final GLState glState) {
        if (mInvalidated) {
            validate();
        }

        final GL10 gl = glState.mGL;
        // Select the projection matrix
        gl.glMatrixMode(GL10.GL_PROJECTION);
        // Reset the projection matrix
        // gl.glLoadIdentity();
        gl.glPushMatrix();
        // camera view and axis system
        glState.setProjection(mAxisSystem, mZoomRect.left, mZoomRect.right, mZoomRect.top, mZoomRect.bottom);

        // camera rotation
        if (mRotation != 0) {
            gl.glTranslatef(mCenter.x, mCenter.y, 0);
            gl.glRotatef(-mRotation, 0, 0, 1);
            gl.glTranslatef(-mCenter.x, -mCenter.y, 0);
        }

        // set matrix back to model view
        gl.glMatrixMode(GL10.GL_MODELVIEW);
    }

    public void unapply(final GLState glState) {
        final GL10 gl = glState.mGL;
        // Select the projection matrix
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glPopMatrix();

        // set matrix back to model view
        gl.glMatrixMode(GL10.GL_MODELVIEW);
    }

    public boolean isInvalidated() {
        return mInvalidated;
    }

    public boolean isViewable(final Manipulatable obj) {
        return !mClipping || RectF.intersects(mBounds, obj.getBounds());
    }

    public boolean isViewable(final RectF rect) {
        return !mClipping || RectF.intersects(mBounds, rect);
    }

    public boolean intersects(final RectF rect) {
        return RectF.intersects(mBounds, rect);
    }

    /**
     * @return the Clipping
     */
    public boolean isClipping() {
        return mClipping;
    }

    /**
     * @param Clipping the Clipping to set
     */
    public void setClipping(final boolean clipping) {
        mClipping = clipping;
    }

    /**
     * @return the world bounds of the Camera's Rect.
     */
    public RectF getBounds() {
        return mBounds;
    }

    /**
     * Find the world coordinates of a point relative to the TL corner of the Camera's Rect.
     * 
     * @param cameraX
     * @param cameraY
     * @param result
     */
    public void localToGlobal(final float cameraX, final float cameraY, final PointF result) {
        mScratch[0] = mZoomRect.left + cameraX;
        mScratch[1] = mZoomRect.top + cameraY;

        if (mRotation != 0 && mMatrix != null) {
            mMatrix.mapPoints(mScratch);
        }

        result.x = mScratch[0];
        result.y = mScratch[1];
    }

    /**
     * Find the local coordinates of a point relative to the TL corner of the Camera's Rect.
     * 
     * @param worldX
     * @param worldY
     * @param result
     */
    public void globalToLocal(final float worldX, final float worldY, final PointF result) {
        mScratch[0] = worldX;
        mScratch[1] = worldY;

        if (mRotation != 0 && mInvertedMatrix != null) {
            mInvertedMatrix.mapPoints(mScratch);
        }

        result.x = mScratch[0] - mZoomRect.left;
        result.y = mScratch[1] - mZoomRect.top;
    }

    /**
     * Find the local coordinates of a Rectangle relative to the TL corner of the Camera's Rect.
     * 
     * @param globalRect
     * @param result
     */
    public void globalToLocal(final RectF globalRect, final RectF result) {
        mScratch[0] = globalRect.left;
        mScratch[1] = globalRect.top;
        mScratch[2] = globalRect.right;
        mScratch[3] = globalRect.bottom;

        if (mRotation != 0 && mInvertedMatrix != null) {
            mInvertedMatrix.mapPoints(mScratch);
        }

        result.left = mScratch[0] - mZoomRect.left;
        result.top = mScratch[1] - mZoomRect.top;
        result.right = mScratch[2] - mZoomRect.left;
        result.bottom = mScratch[3] - mZoomRect.top;
    }

    public boolean addManipulator(final Manipulator manipulator) {
        if (mManipulators == null) {
            // init
            mManipulators = new ArrayList<Manipulator>();
        }

        if (mManipulators.add(manipulator)) {
            manipulator.setTarget(this);
            mNumManipulators++;
            return true;
        }

        return false;
    }

    public boolean removeManipulator(final Manipulator manipulator) {
        if (mManipulators.remove(manipulator)) {
            manipulator.setTarget(null);
            mNumManipulators--;
            return true;
        }

        return false;
    }

    public int removeAllManipulators() {
        final int n = mNumManipulators;
        mManipulators.clear();
        mNumManipulators = 0;

        return n;
    }
}

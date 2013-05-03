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
    // dimensions and size
    protected PointF mCenter = new PointF(0, 0);
    protected PointF mSize = new PointF(0, 0);
    protected float mRotation = 0;
    protected PointF mZoom = new PointF(1, 1);
    protected Scene mScene;
    protected int mAxisSystem;

    private RectF mRect = new RectF();
    private PointF mHalfSize = new PointF(0, 0);
    private boolean mInvalidated;

    protected ArrayList<Manipulator> mManipulators;
    protected int mNumManipulators = 0;

    private boolean mClipping = false;
    private RectF mBounds = new RectF(); // the bounds that contains the rect (with/without rotation)
    private Matrix mMatrix = new Matrix();

    public Camera(final PointF size) {
        mSize.x = size.x;
        mSize.y = size.y;
        mCenter.x = size.x / 2;
        mCenter.y = size.y / 2;
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

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.Manipulatable#setScale(float, float)
     */
    @Override
    public void setScale(final float sx, final float sy) {
        mZoom.x = 1 / sx;
        mZoom.y = 1 / sy;
        invalidate();
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.Manipulatable#setScale(float)
     */
    @Override
    public void setScale(final float scale) {
        mZoom.x = mZoom.y = 1 / scale;
        invalidate();
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.Manipulatable#getScale()
     */
    @Override
    public PointF getScale() {
        return new PointF(1 / mZoom.x, 1 / mZoom.y);
    }

    /**
     * @return the rect
     */
    public RectF getRect() {
        return mRect;
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

    public void validate(final GLState glState) {
        // prepare the rect
        mRect.left = mCenter.x - mHalfSize.x / mZoom.x;
        mRect.top = mCenter.y - mHalfSize.y / mZoom.y;
        mRect.right = mCenter.x + mHalfSize.x / mZoom.x;
        mRect.bottom = mCenter.y + mHalfSize.y / mZoom.y;

        // re-cal the bounds
        if (mRotation != 0) {
            mMatrix.setRotate(mRotation, mCenter.x, mCenter.y);
            mMatrix.mapRect(mBounds, mRect);
        } else {
            mBounds.set(mRect);
        }

        // projection
        project(glState);

        mInvalidated = false;
    }

    protected void project(final GLState glState) {
        final GL10 gl = glState.mGL;
        // Select the projection matrix
        gl.glMatrixMode(GL10.GL_PROJECTION);
        // Reset the projection matrix
        gl.glLoadIdentity();

        // camera view and axis system
        if (mAxisSystem == Scene.AXIS_TOP_LEFT) {
            // invert the y-axis
            gl.glOrthof(mRect.left, mRect.right, mRect.bottom, mRect.top, -1, 1);
        } else {
            gl.glOrthof(mRect.left, mRect.right, mRect.top, mRect.bottom, -1, 1);
        }

        // camera rotation
        if (mRotation != 0) {
            gl.glTranslatef(mCenter.x, mCenter.y, 0);
            gl.glRotatef(-mRotation, 0, 0, 1);
            gl.glTranslatef(-mCenter.x, -mCenter.y, 0);
        }

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
     * @param cameraX Relative X to the Left of the Camera's Rect
     * @param cameraY Relative Y to the Top of the Camera's Rect
     * @return
     * @see #globalToLocal(float, float)
     */
    public PointF localToGlobal(final float cameraX, final float cameraY) {
        final float[] worldPoint = {
                mRect.left + cameraX, mRect.top + cameraY
        };
        if (mRotation != 0) {
            mMatrix.mapPoints(worldPoint);
        }

        return new PointF(worldPoint[0], worldPoint[1]);
    }

    /**
     * Find the local coordinates of a point relative to the TL corner of the Camera's Rect.
     * 
     * @param worldX
     * @param worldY
     * @return
     * @see #localToGlobal(float, float)
     */
    public PointF globalToLocal(final float worldX, final float worldY) {
        final float[] worldPoint = {
                worldX, worldY
        };

        if (mRotation != 0) {
            final Matrix inverse = new Matrix();
            inverse.setRotate(-mRotation, mCenter.x, mCenter.y);
            inverse.mapPoints(worldPoint);
        }

        return new PointF(worldPoint[0] - mRect.left, worldPoint[1] - mRect.top);
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

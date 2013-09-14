/**
 * 
 */
package com.funzio.pure2D.effects.trails;

import android.graphics.PointF;

import com.funzio.pure2D.BaseDisplayObject;
import com.funzio.pure2D.DisplayObject;
import com.funzio.pure2D.Scene;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.gl.gl10.QuadMeshBuffer;
import com.funzio.pure2D.gl.gl10.QuadMeshColorBuffer;
import com.funzio.pure2D.gl.gl10.textures.QuadMeshTextureCoordBuffer;
import com.funzio.pure2D.gl.gl10.textures.Texture;

/**
 * @author long
 */
public class MotionTrailPlot extends BaseDisplayObject implements MotionTrail {
    public static final int DEFAULT_PLOT_SIZE = 10;
    public static final int DEFAULT_NUM_POINTS = 10;
    public static final float DEFAULT_MOTION_EASING = 0.5f;

    protected int mNumPoints = DEFAULT_NUM_POINTS;
    protected float mMotionEasingX = DEFAULT_MOTION_EASING;
    protected float mMotionEasingY = DEFAULT_MOTION_EASING;
    protected int mMinLength = 0;
    protected int mSegmentLength = 0;

    protected PointF[] mPoints;

    protected DisplayObject mTarget;
    protected PointF mTargetOffset = new PointF(0, 0);
    protected Object mData;

    protected Texture mTexture;
    protected float mTextureWidth = DEFAULT_PLOT_SIZE;
    protected float mTextureHeight = DEFAULT_PLOT_SIZE;
    protected float mTextureScaleX = 1, mTextureScaleY = 1;

    protected QuadMeshBuffer mMeshBuffer;
    protected QuadMeshTextureCoordBuffer mTextureCoordBuffer;
    protected QuadMeshColorBuffer mColorBuffer;

    protected float mAlpha1 = 1;
    protected float mAlpha2 = 1;
    protected float mScale1 = 1;
    protected float mScale2 = 1;

    public MotionTrailPlot() {
        this(null);
    }

    public MotionTrailPlot(final DisplayObject target) {
        super();

        mMeshBuffer = new QuadMeshBuffer(mNumPoints);

        // set default num points
        setNumPoints(mNumPoints);

        if (target != null) {
            setTarget(target);
        }

    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.utils.Reusable#reset(java.lang.Object[])
     */
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

    public void setAlphaRange(final float alpha1, final float alpha2) {
        mAlpha1 = alpha1;
        mAlpha2 = alpha2;

        if (mAlpha1 == mAlpha2) {
            mAlpha = alpha1;
        }

        invalidate(ALPHA);
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.BaseDisplayObject#setAlpha(float)
     */
    @Override
    public void setAlpha(final float alpha) {
        mAlpha1 = mAlpha2 = alpha;

        super.setAlpha(alpha);
    }

    public void setScaleRange(final float scale1, final float scale2) {
        mScale1 = scale1;
        mScale2 = scale2;

        invalidate(SCALE);
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.BaseDisplayObject#setPosition(float, float)
     */
    @Override
    public void setPosition(final float x, final float y) {
        if (mNumPoints > 0) {
            mPoints[0].set(x, y);
        }

        invalidate(CHILDREN);
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.BaseDisplayObject#move(float, float)
     */
    @Override
    public void move(final float dx, final float dy) {
        if (mNumPoints > 0) {
            mPoints[0].offset(dx, dy);
        }

        invalidate(CHILDREN);
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.BaseDisplayObject#update(int)
     */
    @Override
    public boolean update(final int deltaTime) {
        if (mNumPoints > 0) {

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

                // invalidate(CHILDREN);
            }

            // validate texture coords
            if (mTexture != null && (mInvalidateFlags & (TEXTURE_COORDS | CHILDREN)) != 0) {

                if (mTextureCoordBuffer == null) {
                    mTextureCoordBuffer = new QuadMeshTextureCoordBuffer(mNumPoints);
                } else if (mTextureCoordBuffer.getNumCells() < mNumPoints) {
                    mTextureCoordBuffer.setNumCells(mNumPoints);
                }
            }

            // validate color
            if (mAlpha1 != mAlpha2 && (mInvalidateFlags & (ALPHA | CHILDREN)) != 0) {

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
            final Scene scene = getScene();
            final boolean flippedAxis = scene != null && scene.getAxisSystem() == Scene.AXIS_TOP_LEFT;
            final float alphaStep = (mAlpha2 - mAlpha1) / mNumPoints;
            float alpha = mAlpha1;
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
                if (mTexture != null && (mInvalidateFlags & (TEXTURE_COORDS | CHILDREN)) != 0) {
                    // apply the coordinates
                    if (flippedAxis) {
                        mTextureCoordBuffer.setRectFlipVerticalAt(i, 0, 0, mTexture.mCoordScaleX, mTexture.mCoordScaleY);
                    } else {
                        mTextureCoordBuffer.setRectAt(i, 0, 0, mTexture.mCoordScaleX, mTexture.mCoordScaleY);
                    }
                }

                // make colors
                if (mAlpha1 != mAlpha2 && (mInvalidateFlags & (ALPHA | CHILDREN)) != 0) {
                    // alpha interpolation
                    mColorBuffer.setAlphaAt(mNumPoints - i - 1, alpha);
                    alpha += alphaStep;
                }
            }

            // apply values to the vertex buffer
            mMeshBuffer.setIndicesNumUsed(mNumPoints * QuadMeshBuffer.NUM_INDICES_PER_CELL);
            mMeshBuffer.validate();

            // apply coords
            if (mTexture != null && mTextureCoordBuffer != null) {
                mTextureCoordBuffer.validate();
            }

            // apply colors
            if (mAlpha1 != mAlpha2 && mColorBuffer != null) {
                mColorBuffer.validate();
            }
        }

        return super.update(deltaTime);
    }

    @Override
    public boolean draw(final GLState glState) {
        drawStart(glState);

        // blend mode
        glState.setBlendFunc(getInheritedBlendFunc());
        // color and alpha
        glState.setColor(getInheritedColor());

        // draw the content
        drawChildren(glState);

        // wrap up
        drawEnd(glState);

        // validate visual only
        mInvalidateFlags &= ~(VISUAL | CHILDREN);

        return true;
    }

    @Override
    protected boolean drawChildren(final GLState glState) {

        // color buffer
        if (mAlpha1 != mAlpha2 && mColorBuffer != null) {
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

        if (numPoints < 2) {
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

            // find the
            mSegmentLength = mMinLength / (numPoints - 1);

            invalidate(CHILDREN);
        }
    }

    public DisplayObject getTarget() {
        return mTarget;
    }

    public void setTarget(final DisplayObject target) {
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

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.BaseDisplayObject#dispose()
     */
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
}

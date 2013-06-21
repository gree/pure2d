/**
 * 
 */
package com.funzio.pure2D.effects.trails;

import android.graphics.PointF;

import com.funzio.pure2D.BaseDisplayObject;
import com.funzio.pure2D.DisplayObject;
import com.funzio.pure2D.Scene;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.gl.gl10.QuadBuffer;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.gl.gl10.textures.TextureCoordBuffer;

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

    protected QuadBuffer mQuadBuffer;
    protected TextureCoordBuffer mTextureCoordBufferScaled;
    protected boolean mTextureFlippedForAxis;

    protected float mAlpha1 = 1;
    protected float mAlpha2 = 1;
    protected float mScale1 = 1;
    protected float mScale2 = 1;

    public MotionTrailPlot() {
        this(null);
    }

    public MotionTrailPlot(final DisplayObject target) {
        super();

        // set default num points
        setNumPoints(mNumPoints);

        if (target != null) {
            setTarget(target);
        }

        mQuadBuffer = new QuadBuffer();
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
        mAlpha = mAlpha1 = alpha1;
        mAlpha2 = alpha2;

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
                    if (mMinLength == 0 || Math.sqrt(dx * dx + dy * dy) > mSegmentLength) {
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

            // apply
            setPoints(mPoints);
        }

        return super.update(deltaTime);
    }

    @Override
    protected boolean drawChildren(final GLState glState) {
        if ((mInvalidateFlags & TEXTURE_COORDS) != 0) {
            validateTextureCoordBuffer();
        }

        // blend mode
        glState.setBlendFunc(mBlendFunc);
        // color and alpha
        glState.setColor(getBlendColor());

        // texture
        if (mTexture != null) {
            // bind the texture
            mTexture.bind();

            // apply the coordinates
            if (mTextureCoordBufferScaled != null) {
                mTextureCoordBufferScaled.apply(glState);
            }
        } else {
            // unbind the texture
            glState.unbindTexture();
        }

        PointF point;
        final float alphaStep = (mAlpha2 - mAlpha1) / mNumPoints;
        mAlpha = mAlpha2;
        final float scaleStep = (mScale2 - mScale1) / mNumPoints;
        float width = mTextureWidth, height = mTextureHeight, scale = mScale2;
        // draw backward from tail to head
        for (int i = mNumPoints - 1; i >= 0; i--) {
            point = mPoints[i];

            // alpha interpolation. This can be slow!
            if (alphaStep != 0) {
                mAlpha -= alphaStep;
                glState.setColor(getBlendColor());
            }

            // scale interpolation
            if (scaleStep != 0) {
                width = mTextureWidth * scale;
                height = mTextureWidth * scale;
                scale -= scaleStep;
            }

            // set position and size
            mQuadBuffer.setRect(point.x - width * 0.5f, point.y - height * 0.5f, width, height);
            // draw
            mQuadBuffer.draw(glState);

            // GLES11Ext.glDrawTexfOES(point.x - width * 0.5f, point.y - height * 0.5f, 0, width, height);
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

    /**
     * validate texture coords
     */
    protected void validateTextureCoordBuffer() {
        if (mTextureCoordBufferScaled == null) {
            mTextureCoordBufferScaled = TextureCoordBuffer.getDefault();
            mTextureFlippedForAxis = false;
        }

        // match texture coordinates with the Axis system
        final Scene scene = getScene();
        if (scene != null && scene.getAxisSystem() == Scene.AXIS_TOP_LEFT && !mTextureFlippedForAxis) {
            // flip vertically
            mTextureCoordBufferScaled.flipVertical();
            mTextureFlippedForAxis = true;
        }

        // scale to match with the Texture scale, for optimization
        if (mTexture != null) {
            // diff check
            if ((mTexture.mCoordScaleX != mTextureScaleX || mTexture.mCoordScaleY != mTextureScaleY)) {
                // apply scale
                mTextureCoordBufferScaled.scale(mTexture.mCoordScaleX / mTextureScaleX, mTexture.mCoordScaleY / mTextureScaleY);
                // store for ref
                mTextureScaleX = mTexture.mCoordScaleX;
                mTextureScaleY = mTexture.mCoordScaleY;
            }
        }

        // clear flag: texture coords
        validate(TEXTURE_COORDS);
    }

    public PointF[] getPoints() {
        return mPoints;
    }

    public void setPoints(final PointF... points) {
        mPoints = points;

        invalidate(VISUAL);
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

        mQuadBuffer.dispose();
        mQuadBuffer = null;

        mTextureCoordBufferScaled.dispose();
        mTextureCoordBufferScaled = null;
    }
}

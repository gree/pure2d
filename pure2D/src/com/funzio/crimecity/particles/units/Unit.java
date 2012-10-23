/**
 * 
 */
package com.funzio.crimecity.particles.units;

import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.PointF;

import com.funzio.crimecity.game.model.CCMapDirection;
import com.funzio.crimecity.particles.ParticleAdapter;
import com.funzio.pure2D.containers.DisplayGroup;
import com.funzio.pure2D.gl.gl10.BlendFunc;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.shapes.Sprite;

/**
 * @author long
 */
public class Unit extends DisplayGroup {
    private static final BlendFunc SHADOW_BLEND_FUNC = new BlendFunc(GL10.GL_ZERO, GL10.GL_ONE_MINUS_SRC_ALPHA);
    private static final float SHADOW_ALPHA = 0.3f;
    protected static final Random sRandom = new Random();

    protected CCMapDirection mDirection = CCMapDirection.NORTHEAST;
    protected Sprite mSprite;
    protected float mSpriteScale = 1;
    protected String mTextureKey;
    protected PointF mShadowOffset;
    protected PointF mVelocity;
    protected int mFrame = 0;
    protected Listener mListener;

    private boolean mFlipped = false;
    private boolean mFinished = false;

    // target
    protected PointF mTargetPosition = new PointF();

    public Unit(final String textureKey, final CCMapDirection direction) {
        mTextureKey = textureKey;

        mSprite = new Sprite();
        addChild(mSprite);

        // apply direction
        setDirection(direction);

        // initially hidden
        mAlpha = 0;
    }

    protected void setSpriteScale(final float value) {
        mSpriteScale = value;
        mSprite.setScale(mSpriteScale, mSpriteScale);

        // re-apply direction
        setDirection(mDirection);
    }

    /**
     * @return the direction
     */
    public CCMapDirection getDirection() {
        return mDirection;
    }

    /**
     * @param direction the direction to set
     */
    public void setDirection(final CCMapDirection direction) {
        mDirection = direction;

        Texture texture = ParticleAdapter.TEXTURE_MANAGER.getUnitTexture(mTextureKey, mDirection);
        if (texture != null) {
            mSprite.setTexture(texture);

            // check and flip image
            if (mDirection.equals(CCMapDirection.NORTHWEST) || mDirection.equals(CCMapDirection.SOUTHWEST)) {
                if (!mFlipped) {
                    mFlipped = true;
                    mSprite.flipTextureCoordBuffer(FLIP_X);
                }
            } else {
                if (mFlipped) {
                    mFlipped = false;
                    mSprite.flipTextureCoordBuffer(FLIP_X);
                }
            }
        }
    }

    /**
     * Set moving speed of this object.
     * 
     * @param speed can be negative for moving backward.
     */
    protected void setSpeed(final float speed) {
        if (speed == 0) {
            mVelocity = null;
        } else {
            if (mVelocity == null) {
                mVelocity = new PointF();
            }

            if (mDirection.equals(CCMapDirection.NORTHWEST)) {
                mVelocity.x = -speed;
                mVelocity.y = speed / 2;
            } else if (mDirection.equals(CCMapDirection.NORTHEAST)) {
                mVelocity.x = speed;
                mVelocity.y = speed / 2;
            } else if (mDirection.equals(CCMapDirection.SOUTHWEST)) {
                mVelocity.x = -speed;
                mVelocity.y = -speed / 2;
            } else if (mDirection.equals(CCMapDirection.SOUTHEAST)) {
                mVelocity.x = speed;
                mVelocity.y = -speed / 2;
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.DisplayGroup#drawStart(javax.microedition.khronos.opengles.GL10)
     */
    @Override
    protected void drawStart(final GLState glState) {
        super.drawStart(glState);

        // draw shadow
        if (mShadowOffset != null) {
            mSprite.setBlendFunc(SHADOW_BLEND_FUNC);
            mSprite.setAlpha(SHADOW_ALPHA * getAlpha());
            mSprite.moveBy(mShadowOffset.x, mShadowOffset.y);
            mSprite.draw(glState);
            mSprite.moveBy(-mShadowOffset.x, -mShadowOffset.y);
            mSprite.setAlpha(1);
            mSprite.setBlendFunc(null);
        }
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.DisplayGroup#update(int)
     */
    @Override
    public boolean update(final int deltaTime) {
        super.update(deltaTime);

        mFrame += ParticleAdapter.FRAME_THROTTLE ? 2 : 1;

        if (mVelocity != null) {
            mPosition.x += mVelocity.x;
            mPosition.y += mVelocity.y;
            invalidate();
        }

        if (mListener != null) {
            mListener.onAnimationUpdate();
        }

        return true;
    }

    protected void finish() {
        if (!mFinished) {
            queueEvent(new Runnable() {

                @Override
                public void run() {
                    removeFromParent();

                    if (mListener != null) {
                        mListener.onAnimationComplete();
                    }
                }
            });

            // flag
            mFinished = true;
        }
    }

    public boolean isFinished() {
        return mFinished;
    }

    public void setTarget(final PointF point) {
        mTargetPosition.x = point.x;
        mTargetPosition.y = point.y;
    }

    public PointF getProgressBarPosition() {
        return localToGlobal(new PointF(0, mSprite.getSize().y * mSpriteScale - 80));
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.BaseDisplayObject#onAdded(com.funzio.pure2D.Container)
     */
    // @Override
    // public void onAdded(final Container parent) {
    // super.onAdded(parent);
    //
    // queueEvent(new Runnable() {
    // @Override
    // public void run() {
    // addChild(mSprite);
    // }
    // });
    // }

    protected void attackStart() {
        if (mListener != null) {
            mListener.onAttackStart();
        }
    }

    protected void attackEnd() {
        if (mListener != null) {
            mListener.onAttackEnd();
        }
    }

    protected void soundStart() {
        if (mListener != null) {
            mListener.onSoundStart();
        }
    }

    /**
     * @return the listener
     */
    public Listener getListener() {
        return mListener;
    }

    /**
     * @param listener the listener to set
     */
    public void setListener(final Listener listener) {
        mListener = listener;
    }

    public static interface Listener {
        public void onAnimationUpdate();

        public void onSoundStart();

        public void onAttackStart();

        public void onAttackEnd();

        public void onAnimationComplete();
    }
}

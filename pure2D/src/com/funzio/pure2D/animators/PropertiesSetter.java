/**
 * 
 */
package com.funzio.pure2D.animators;

import java.util.Map;

import com.funzio.pure2D.DisplayObject;
import com.funzio.pure2D.gl.GLColor;
import com.funzio.pure2D.gl.gl10.BlendFunc;

/**
 * @author long
 */
public class PropertiesSetter extends BaseAnimator {
    public static final String X = "x";
    public static final String Y = "y";
    public static final String Z = "z";
    public static final String ORIGIN_X = "origin_x";
    public static final String ORIGIN_Y = "origin_y";
    public static final String SCALE_X = "scale_x";
    public static final String SCALE_Y = "scale_y";
    public static final String ROTATION = "rotation";
    public static final String ALPHA = "alpha";
    public static final String COLOR = "color";
    public static final String BLEND_MODE = "blend_mode";

    private Map<String, Object> mProperties;

    public PropertiesSetter() {
        super();
    }

    public void start(final Map<String, Object> props) {
        mProperties = props;
        start();
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.animators.BaseAnimator#startElapse(int)
     */
    @Override
    public void startElapse(final int elapsedTime) {
        super.startElapse(elapsedTime);

        if (mProperties != null && mTarget != null) {
            // translate
            if (mProperties.containsKey(X) || mProperties.containsKey(Y)) {
                final float x = mProperties.containsKey(X) ? (Float) mProperties.get(X) : mTarget.getPosition().x;
                final float y = mProperties.containsKey(Y) ? (Float) mProperties.get(Y) : mTarget.getPosition().y;
                mTarget.setPosition(x, y);
            }

            // scale
            if (mProperties.containsKey(SCALE_X) || mProperties.containsKey(SCALE_Y)) {
                final float scaleX = mProperties.containsKey(SCALE_X) ? (Float) mProperties.get(SCALE_X) : mTarget.getScale().x;
                final float scaleY = mProperties.containsKey(SCALE_Y) ? (Float) mProperties.get(SCALE_Y) : mTarget.getScale().y;
                mTarget.setScale(scaleX, scaleY);
            }

            // rotate
            if (mProperties.containsKey(ROTATION)) {
                mTarget.setRotation((Float) mProperties.get(ROTATION));
            }

            // display object's properties
            if (mTarget instanceof DisplayObject) {
                final DisplayObject obj = (DisplayObject) mTarget;

                // z-depth
                if (mProperties.containsKey(Z)) {
                    obj.setZ((Float) mProperties.get(Z));
                }

                // origin
                if (mProperties.containsKey(ORIGIN_X) || mProperties.containsKey(ORIGIN_Y)) {
                    final float x = mProperties.containsKey(ORIGIN_X) ? (Float) mProperties.get(ORIGIN_X) : obj.getOrigin().x;
                    final float y = mProperties.containsKey(ORIGIN_Y) ? (Float) mProperties.get(ORIGIN_Y) : obj.getOrigin().y;

                    if (x == -1 && y == -1) {
                        obj.setOriginAtCenter();
                    } else {
                        obj.setOrigin(x, y);
                    }
                }

                // alpha
                if (mProperties.containsKey(ALPHA)) {
                    obj.setAlpha((Float) mProperties.get(ALPHA));
                }

                // color
                if (mProperties.containsKey(COLOR)) {
                    obj.setColor((GLColor) mProperties.get(COLOR));
                }

                // blending mode
                if (mProperties.containsKey(BLEND_MODE)) {
                    obj.setBlendFunc((BlendFunc) mProperties.get(BLEND_MODE));
                }
            }
        }
    }

    public void setProperties(final Map<String, Object> props) {
        mProperties = props;
    }

    public Map<String, Object> getProperties() {
        return mProperties;
    }

}

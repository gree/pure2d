/**
 * 
 */
package com.funzio.pure2D.particles.nova.vo;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.funzio.pure2D.DisplayObject;
import com.funzio.pure2D.effects.trails.MotionTrail;
import com.funzio.pure2D.effects.trails.MotionTrailShape;
import com.funzio.pure2D.gl.GLColor;
import com.funzio.pure2D.particles.nova.NovaConfig;

/**
 * @author long
 */
public class MotionTrailShapeVO extends MotionTrailVO {

    public ArrayList<Integer> stroke1;
    public ArrayList<Integer> stroke2;
    public ArrayList<GLColor> color1;
    public ArrayList<GLColor> color2;
    public ArrayList<String> stroke_interpolation;
    public ArrayList<Float> easing_x;
    public ArrayList<Float> easing_y;

    public MotionTrailShapeVO() {
        super();
    }

    /**
     * @param json
     * @throws JSONException
     */
    public MotionTrailShapeVO(final JSONObject json) throws JSONException {
        super(json);

        stroke1 = NovaVO.getListInt(json, "stroke1");
        stroke2 = NovaVO.getListInt(json, "stroke2");
        color1 = NovaVO.getListColor(json, "color1");
        color2 = NovaVO.getListColor(json, "color2");
        easing_x = NovaVO.getListFloat(json, "easing_x");
        easing_y = NovaVO.getListFloat(json, "easing_y");
        stroke_interpolation = NovaVO.getListString(json, "stroke_interpolation");
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.particles.nova.vo.TrailVO#createTrail(com.funzio.pure2D.DisplayObject)
     */
    @Override
    public MotionTrail createTrail(final int emitIndex, final DisplayObject target) {
        return init(emitIndex, target, new MotionTrailShape());
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.particles.nova.vo.TrailVO#applyScale(float)
     */
    @Override
    public void applyScale(final float scale) {
        super.applyScale(scale);

        // scale stroke1
        if (stroke1 != null) {
            final int size = stroke1.size();
            for (int i = 0; i < size; i++) {
                stroke1.set(i, Math.round(stroke1.get(i) * scale));
            }
        }

        // scale stroke2
        if (stroke2 != null) {
            final int size = stroke2.size();
            for (int i = 0; i < size; i++) {
                stroke2.set(i, Math.round(stroke2.get(i) * scale));
            }
        }
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.particles.nova.vo.TrailVO#resetTrail(com.funzio.pure2D.DisplayObject, com.funzio.pure2D.effects.trails.Trailable)
     */
    @Override
    public void resetTrail(final int emitIndex, final DisplayObject target, final MotionTrail trail) {
        super.resetTrail(emitIndex, target, trail);

        final MotionTrailShape shape = (MotionTrailShape) trail;
        shape.setStrokeRange(NovaConfig.getInt(stroke1, emitIndex, 1), NovaConfig.getInt(stroke2, emitIndex, 1));
        shape.setStrokeColorRange(NovaConfig.getColor(color1, emitIndex, GLColor.WHITE), NovaConfig.getColor(color2, emitIndex, GLColor.WHITE));
        shape.setStrokeInterpolator(NovaConfig.getInterpolator(NovaConfig.getString(stroke_interpolation, emitIndex)));
        shape.setMotionEasing(NovaConfig.getFloat(easing_x, emitIndex, MotionTrailShape.DEFAULT_MOTION_EASING), NovaConfig.getFloat(easing_y, emitIndex, MotionTrailShape.DEFAULT_MOTION_EASING));
    }
}

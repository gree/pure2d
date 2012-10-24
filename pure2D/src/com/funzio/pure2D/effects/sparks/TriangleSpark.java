/**
 * 
 */
package com.funzio.pure2D.effects.sparks;

import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import com.funzio.pure2D.Playable;
import com.funzio.pure2D.animators.RotateAnimator;
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
    // protected static final BlendFunc DEFAULT_BF = BlendFunc.getAdd();

    protected RotateAnimator mRotateAnimator;

    // protected ScaleAnimator mScaleAnimator;

    public TriangleSpark() {
        super();

        // use default texture coordinates
        setTextureCoordBuffer(DEFAULT_TEXTURE_COORDS);

        // default colors
        setColorBuffer(new ColorBuffer(DEFAULT_COLORS));
        // setBlendFunc(DEFAULT_BF);

        // initial angle
        setRotation(RANDOM.nextInt(360));

        // animators
        mRotateAnimator = new RotateAnimator(null);
        mRotateAnimator.setLoop(Playable.LOOP_REPEAT);
        mRotateAnimator.setDuration(2000 + RANDOM.nextInt(2000));
        addManipulator(mRotateAnimator);
        mRotateAnimator.start(mRotation + (RANDOM.nextInt(2) == 0 ? -360 : 360));
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.DisplayObject#setSize(android.graphics.PointF)
     */
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

/**
 * 
 */
package com.funzio.pure2D.shapes;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.PointF;
import android.view.animation.Interpolator;

import com.funzio.pure2D.InvalidateFlags;
import com.funzio.pure2D.gl.GLColor;
import com.funzio.pure2D.gl.gl10.ColorBuffer;
import com.funzio.pure2D.gl.gl10.VertexBuffer;

/**
 * @author long
 */
public class Polyline extends Shape {

    protected static final int VERTEX_POINTER_SIZE = 2; // xy

    protected PointF[] mPoints;
    protected float mStroke1 = 1;
    protected float mStroke2 = 1;

    protected GLColor[] mStrokeColors;
    protected float[] mColorValues;

    protected float[] mVertices;
    protected int mVerticesNum = 0;
    protected float mTotalLength;
    protected Interpolator mStrokeInterpolator = null;

    public PointF[] getPoints() {
        return mPoints;
    }

    public void setPoints(final PointF... points) {
        mPoints = points;
        final int len = mPoints.length;

        allocateVertices(len * 2, VERTEX_POINTER_SIZE);// each point has upper and lower points

        final float strokeDelta = mStroke2 - mStroke1;
        float dx, dy, segment = 0;
        float angle0 = 0;
        float angle1 = 0;
        float angleDelta = 0;
        float angleCut = 0;
        float rx, ry;
        float stroke = mStroke1;
        int i, vertexIndex = 0;
        float lastRY = 0;
        float lastRX = 0;
        boolean flip = false;
        PointF currentPoint;

        // find total segment
        mTotalLength = 0;
        for (i = 0; i < len - 1; i++) {
            dx = points[i + 1].x - points[i].x;
            dy = points[i + 1].y - points[i].y;

            mTotalLength += Math.sqrt(dx * dx + dy * dy);
        }

        for (i = 0; i < len; i++) {
            currentPoint = points[i];

            if (i < len - 1) {
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
            }

            if (i == 0 || i == len - 1) {
                // beginning and closing cut
                angleCut = angle1 + (float) Math.PI * 0.5f;
            } else {
                angleDelta = (angle1 - angle0);
                angleCut += angleDelta * 0.5f;
            }

            rx = stroke * (float) Math.cos(angleCut) * 0.5f;
            ry = stroke * (float) Math.sin(angleCut) * 0.5f;
            // Log.e("long", "a t r x y: " + angle1 + " " + Math.round(stroke) + " " + Math.round(radius) + " " + Math.round(rx) + " " + Math.round(ry));

            if (lastRY * ry < 0 && lastRX * rx < 0) {
                // flag for flipping the upper and lower points
                flip = !flip;
            }
            lastRX = rx;
            lastRY = ry;
            if (flip) {
                rx = -rx;
                ry = -ry;
            }

            // upper point
            mVertices[vertexIndex] = currentPoint.x + rx;
            mVertices[vertexIndex + 1] = currentPoint.y + ry;
            // lower point
            mVertices[vertexIndex + 2] = currentPoint.x - rx;
            mVertices[vertexIndex + 3] = currentPoint.y - ry;
            vertexIndex += VERTEX_POINTER_SIZE * 2;

            angle0 = angle1;
        }

        if (mVertexBuffer == null) {
            mVertexBuffer = new VertexBuffer(GL10.GL_TRIANGLE_STRIP, mVerticesNum, mVertices);
        } else {
            mVertexBuffer.setVertices(GL10.GL_TRIANGLE_STRIP, mVerticesNum, mVertices);
        }

        invalidate(InvalidateFlags.VISUAL);
    }

    protected void allocateVertices(final int numVertices, final int vertexSize) {
        mVerticesNum = numVertices; // each point has upper and lower points
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

        if (mPoints != null && mPoints.length > 0) {
            setPoints(mPoints);
        }
    }

    @Deprecated
    /**
     * @param color1
     * @param color2
     * @see #setStrokeColors
     */
    public void setStrokeColorRange(final GLColor color1, final GLColor color2) {
        setStrokeColors(color1, color2);
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

}

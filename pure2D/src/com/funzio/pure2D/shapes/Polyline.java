/**
 * 
 */
package com.funzio.pure2D.shapes;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.PointF;
import android.view.animation.Interpolator;

import com.funzio.pure2D.gl.GLColor;
import com.funzio.pure2D.gl.gl10.ColorBuffer;
import com.funzio.pure2D.gl.gl10.VertexBuffer;

/**
 * @author long
 */
public class Polyline extends Shape {

    protected PointF[] mPoints;
    protected float mStroke1 = 1;
    protected float mStroke2 = 1;

    protected GLColor mStrokeColor1;
    protected GLColor mStrokeColor2;
    protected float[] mColorValues;

    protected float[] mVertices;
    protected int mVerticesNum = 0;
    protected float mTotalLength;
    protected Interpolator mStrokeInterpolator = null;// new DecelerateInterpolator();

    public PointF[] getPoints() {
        return mPoints;
    }

    public void setPoints(final PointF... points) {
        mPoints = points;
        final int len = mPoints.length;

        mVerticesNum = len * 2; // each point has upper and lower points
        if (mVertices == null || mVerticesNum > mVertices.length) {
            mVertices = new float[mVerticesNum * 2];

            // only set colors ONCE!
            setStrokeColorRange(mStrokeColor1, mStrokeColor2);
        }

        final float strokeDelta = mStroke2 - mStroke1;
        float dx, dy, segment = 0;
        float angle0 = 0;
        float angle1 = 0;
        float angleDelta = 0;
        float angleCut = 0;
        float rx, ry;
        float stroke = mStroke1;
        int i, vertexIndex = 0;

        // find total segment
        mTotalLength = 0;
        for (i = 0; i < len - 1; i++) {
            dx = points[i + 1].x - points[i].x;
            dy = points[i + 1].y - points[i].y;

            mTotalLength += Math.sqrt(dx * dx + dy * dy);
        }

        for (i = 0; i < len; i++) {

            if (i < len - 1) {
                dx = points[i + 1].x - points[i].x;
                dy = points[i + 1].y - points[i].y;
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
                angleCut = angle1 + (float) Math.PI / 2f;
            } else {
                angleDelta = (angle1 - angle0);
                angleCut += angleDelta / 2;
            }

            rx = stroke * (float) Math.cos(angleCut) / 2f;
            ry = stroke * (float) Math.sin(angleCut) / 2f;
            // Log.e("long", "a t r x y: " + angle1 + " " + Math.round(stroke) + " " + Math.round(radius) + " " + Math.round(rx) + " " + Math.round(ry));

            // upper point
            mVertices[vertexIndex] = points[i].x + rx;
            mVertices[vertexIndex + 1] = points[i].y + ry;
            // lower point
            mVertices[vertexIndex + 2] = points[i].x - rx;
            mVertices[vertexIndex + 3] = points[i].y - ry;
            vertexIndex += 4;

            angle0 = angle1;
        }

        if (mVertexBuffer == null) {
            mVertexBuffer = new VertexBuffer(GL10.GL_TRIANGLE_STRIP, mVerticesNum, mVertices);
        } else {
            mVertexBuffer.setVertices(GL10.GL_TRIANGLE_STRIP, mVerticesNum, mVertices);
        }

        invalidate();
    }

    public void setStrokeRange(final float stroke1, final float stroke2) {
        mStroke1 = stroke1;
        mStroke2 = stroke2;
    }

    public void setStrokeColorRange(final GLColor color1, final GLColor color2) {
        mStrokeColor1 = color1;
        mStrokeColor2 = color2;

        // null check
        if (mPoints != null && color1 != null && color2 != null) {
            if (mColorValues == null || (mVerticesNum * 4) > mColorValues.length) {
                mColorValues = new float[mVerticesNum * 4]; // each vertex has 4 floats
            }

            float dr = (color2.r - color1.r) / (mPoints.length - 1);
            float dg = (color2.g - color1.g) / (mPoints.length - 1);
            float db = (color2.b - color1.b) / (mPoints.length - 1);
            float da = (color2.a - color1.a) / (mPoints.length - 1);
            int index = 0;
            float r = color1.r;
            float g = color1.g;
            float b = color1.b;
            float a = color1.a;
            for (int i = 0; i < mPoints.length; i++) {
                // upper point
                mColorValues[index++] = r;
                mColorValues[index++] = g;
                mColorValues[index++] = b;
                mColorValues[index++] = a;

                // lower point
                mColorValues[index++] = r;
                mColorValues[index++] = g;
                mColorValues[index++] = b;
                mColorValues[index++] = a;

                r += dr;
                g += dg;
                b += db;
                a += da;
            }

            if (mColorBuffer == null) {
                mColorBuffer = new ColorBuffer(mColorValues);
            } else {
                mColorBuffer.setValues(mColorValues);
            }
        } else {
            mColorValues = null;
            mColorBuffer = null;
        }
    }

    public Interpolator getStrokeInterpolator() {
        return mStrokeInterpolator;
    }

    public void setStrokeInterpolator(final Interpolator strokeInterpolator) {
        mStrokeInterpolator = strokeInterpolator;
    }

}

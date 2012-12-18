/**
 * 
 */
package com.funzio.pure2D.shapes;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.PointF;
import android.util.FloatMath;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import com.funzio.pure2D.gl.gl10.VertexBuffer;

/**
 * @author long
 */
public class PolyLine extends Shape {

    protected PointF[] mPoints;
    protected float mStroke1 = 1;
    protected float mStroke2 = 1;

    protected float[] mVertices;
    // private short[] mIndices;
    protected int mVerticesNum = 0;
    protected float mTotalSegment;
    protected Interpolator mStrokeInterpolator = new DecelerateInterpolator();

    public PointF[] getPoints() {
        return mPoints;
    }

    public void setPoints(final PointF... points) {
        mPoints = points;
        mVerticesNum = mPoints.length * 2; // each point has upper and lower points
        if (mVertices == null || mVerticesNum > mVertices.length) {
            mVertices = new float[mVerticesNum * 2];
            // mIndices = new short[(mPoints.length - 1) * 6];
        }

        final float strokeDelta = (mStroke2 - mStroke1);
        float dx, dy, segment = 0;
        float angle0 = 0;
        float angle1 = 0;
        float angleDelta = 0;
        float angleCut = 0;
        float rx, ry;
        float stroke = mStroke1;
        int i, vertexIndex = 0;

        // find total segment
        mTotalSegment = 0;
        for (i = 0; i < mPoints.length - 1; i++) {
            dx = points[i + 1].x - points[i].x;
            dy = points[i + 1].y - points[i].y;

            mTotalSegment += FloatMath.sqrt(dx * dx + dy * dy);
        }

        for (i = 0; i < mPoints.length; i++) {

            if (i < mPoints.length - 1) {
                dx = points[i + 1].x - points[i].x;
                dy = points[i + 1].y - points[i].y;
                segment += FloatMath.sqrt(dx * dx + dy * dy);
                if (mStrokeInterpolator != null) {
                    // interpolating
                    stroke = mStroke1 + mStrokeInterpolator.getInterpolation(segment / mTotalSegment) * strokeDelta;
                } else {
                    // linear
                    stroke = mStroke1 + (segment / mTotalSegment) * strokeDelta;
                }

                angle1 = (float) Math.atan2(dy, dx);
            }

            if (i == 0 || i == mPoints.length - 1) {
                // beginning and closing cut
                angleCut = angle1 + (float) Math.PI / 2f;
            } else {
                angleDelta = (angle1 - angle0);
                angleCut += angleDelta / 2;
            }

            rx = stroke * FloatMath.cos(angleCut) / 2f;
            ry = stroke * FloatMath.sin(angleCut) / 2f;
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

    public Interpolator getStrokeInterpolator() {
        return mStrokeInterpolator;
    }

    public void setStrokeInterpolator(final Interpolator strokeInterpolator) {
        mStrokeInterpolator = strokeInterpolator;
    }

}

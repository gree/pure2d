/**
 * 
 */
package com.funzio.pure2D.shapes;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.PointF;
import android.util.FloatMath;

import com.funzio.pure2D.gl.gl10.VertexBuffer;

/**
 * @author long
 */
public class PolyLine extends Shape {

    private PointF[] mPoints;
    private float mThick1 = 1;
    private float mThick2 = 1;

    private float[] mVertices;
    private int mVerticesNum = 0;

    public PolyLine() {
    }

    public PointF[] getPoints() {
        return mPoints;
    }

    public void setPoints(final PointF... points) {
        mPoints = points;
        mVerticesNum = mPoints.length * 2; // each point has upper and lower points
        if (mVertices == null || mVerticesNum > mVertices.length) {
            mVertices = new float[mVerticesNum * 2];
        }

        final float thickDelta = (mThick2 - mThick1) / (mPoints.length - 1);
        float dx, dy;
        float angle0 = 0;
        float angle1 = 0;
        float angleDelta = 0;
        float angleCut = 0;
        float rx, ry;
        float thick = mThick1;
        float radius = thick;
        int i, index = 0;
        for (i = 0; i < mPoints.length - 1; i++) {
            dx = points[i + 1].x - points[i].x;
            dy = points[i + 1].y - points[i].y;

            angle1 = (float) Math.atan2(dy, dx);
            if (i == 0) {
                // beginning cut
                angleCut = angle1 + (float) Math.PI / 2f;
            } else {
                angleDelta = angle1 - angle0;
                float temp = FloatMath.cos(angleDelta / 2);
                radius = thick / (temp == 0 ? 1 : temp);
                angleCut += angleDelta / 2;
            }

            rx = radius * FloatMath.cos(angleCut) / 2f;
            ry = radius * FloatMath.sin(angleCut) / 2f;

            // upper point
            mVertices[index++] = points[i].x + rx;
            mVertices[index++] = points[i].y + ry;
            // lower point
            mVertices[index++] = points[i].x - rx;
            mVertices[index++] = points[i].y - ry;

            angle0 = angle1;
            thick += thickDelta;
        }

        // closing point
        angleCut = angle1 + (float) Math.PI / 2f;
        rx = thick * FloatMath.cos(angleCut) / 2f;
        ry = thick * FloatMath.sin(angleCut) / 2f;
        // upper point
        mVertices[index++] = points[i].x + rx;
        mVertices[index++] = points[i].y + ry;
        // lower point
        mVertices[index++] = points[i].x - rx;
        mVertices[index++] = points[i].y - ry;

        if (mVertexBuffer == null) {
            mVertexBuffer = new VertexBuffer(GL10.GL_TRIANGLE_STRIP, mVerticesNum, mVertices);
        } else {
            mVertexBuffer.setVertices(GL10.GL_TRIANGLE_STRIP, mVerticesNum, mVertices);
        }
    }

    public void setThickRange(final float thick1, final float thick2) {
        mThick1 = thick1;
        mThick2 = thick2;
    }

}

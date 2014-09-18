/*******************************************************************************
 * Copyright (C) 2012-2014 GREE, Inc.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/
/**
 * 
 */
package com.funzio.pure2D.shapes;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.PointF;

import com.funzio.pure2D.InvalidateFlags;
import com.funzio.pure2D.geom.Point3D;
import com.funzio.pure2D.gl.gl10.VertexBuffer;

/**
 * @author long
 */
public class Polyline3D extends Polyline {

    protected static final int VERTEX_POINTER_SIZE = 3; // xyz

    @Override
    protected void validateVertices() {
        final PointF[] points = mPoints;

        if (points instanceof Point3D[]) {
            final Point3D[] point3Ds = (Point3D[]) points;

            final int len = mPoints.length;

            allocateVertices(len * 2, VERTEX_POINTER_SIZE);// each point has upper and lower points

            final float strokeDelta = mStroke2 - mStroke1;
            float dx, dy, dz, segment = 0;
            float angle0 = 0;
            float angle1 = 0;
            float angleDelta = 0;
            float angleCut = 0;
            float rx, ry;
            float stroke = mStroke1;
            int i, vertexIndex = 0;
            float lastRX = 0;
            float lastRY = 0;
            boolean flip = false;
            Point3D currentPoint;

            // find total segment
            mTotalLength = 0;
            for (i = 0; i < len - 1; i++) {
                dx = point3Ds[i + 1].x - point3Ds[i].x;
                dy = point3Ds[i + 1].y - point3Ds[i].y;
                dz = point3Ds[i + 1].z - point3Ds[i].z;

                mTotalLength += Math.sqrt(dx * dx + dy * dy + dz * dz);
            }

            for (i = 0; i < len; i++) {
                currentPoint = point3Ds[i];

                if (i < len - 1) {
                    dx = point3Ds[i + 1].x - currentPoint.x;
                    dy = point3Ds[i + 1].y - currentPoint.y;
                    dz = point3Ds[i + 1].z - currentPoint.z;
                    segment += Math.sqrt(dx * dx + dy * dy + dz * dz);
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
                mVertices[vertexIndex + 2] = currentPoint.z;
                // lower point
                mVertices[vertexIndex + 3] = currentPoint.x - rx;
                mVertices[vertexIndex + 4] = currentPoint.y - ry;
                mVertices[vertexIndex + 5] = currentPoint.z;
                vertexIndex += VERTEX_POINTER_SIZE * 2;

                angle0 = angle1;
            }

            if (mVertexBuffer == null) {
                mVertexBuffer = new VertexBuffer(GL10.GL_TRIANGLE_STRIP, mVerticesNum, mVertices);
                mVertexBuffer.setVertexPointerSize(VERTEX_POINTER_SIZE); // xyz
            } else {
                mVertexBuffer.setVertices(GL10.GL_TRIANGLE_STRIP, mVerticesNum, mVertices);
            }

            invalidate(InvalidateFlags.VISUAL);
        } else {
            super.setPoints(points);
        }
    }

}

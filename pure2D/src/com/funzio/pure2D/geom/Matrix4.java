package com.funzio.pure2D.geom;

import android.annotation.SuppressLint;
import android.opengl.Matrix;

import com.funzio.pure2D.Pure2D;

/**
 * Class to conveniently encapsulate a 4x4, column-major matrix for use with {@link android.opengl.Matrix} methods.
 * 
 * @author hyungjoon.kim
 */
public class Matrix4 {
    public final float[] values = new float[16];

    private final float[] mPrivateTemp = new float[16];

    /**
     * Construct a new identity matrix.
     */
    public Matrix4() {
        identity();
    }

    /**
     * Construct a copy of the given <code>Matrix4</code>.
     * 
     * @param other The <code>Matrix4</code> to copy
     */
    public Matrix4(final Matrix4 other) {
        set(other);
    }

    private static void new_frustumM(float[] m, int offset,
                                float width, float height,
                                float near, float far) {
        if (near == far) {
            throw new IllegalArgumentException("near == far");
        }
        if (near <= 0.0f) {
            throw new IllegalArgumentException("near <= 0.0f");
        }
        if (far <= 0.0f) {
            throw new IllegalArgumentException("far <= 0.0f");
        }
        float aspectRatio = height / width;
        float DEG2RAD = 3.14159f / 180.0f;
        float fov = Pure2D.GL_PERSPECTIVE_FOVY * DEG2RAD;
        float h = (float)(Math.cos(0.5f*fov)/Math.sin(0.5f*fov));
        float w = h * aspectRatio;
        float a =  - (near+far)/(near - far);
        float b = - ((2*far*near)/(far-near));

        m[offset + 0] = w;
        m[offset + 1] = 0f;
        m[offset + 2] = 0f;
        m[offset + 3] = 0f;
        m[offset + 4] = 0f;
        m[offset + 5] = h;
        m[offset + 6] = 0f;
        m[offset + 7] = 0f;
        m[offset + 8] = 0f;
        m[offset + 9] = 0f;
        m[offset + 10] = -a;
        m[offset + 11] = -1f;
        m[offset + 12] = 0f;
        m[offset + 13] = 0f;
        m[offset + 14] = b;
        m[offset + 15] = 0f;
    }
    /**
     * Set the values of this <code>Matrix4</code> as a projection matrix defined by the given clip panes.
     * 
     * @param left Must not be equal to <code>right</code>
     * @param right Must not be equal to <code>left</code>
     * @param bottom Must not be equal to <code>top</code>
     * @param top Must not be equal to <code>bottom</code>
     * @param near Must not be equal to <code>far</code> and must be positive
     * @param far Must not be equal to <code>near</code> and must be positive
     * @see #ortho(left, right, bottom, top, near, far)
     */
    public void frustum(final float left, final float right, final float bottom, final float top, final float near, final float far) {
//        Matrix.frustumM(values, 0, left, right, bottom, top, near, far);
        new_frustumM(values, 0, right-left, bottom-top, near, far);
    }

    /**
     * Set this <code>Matrix4</code> into an identity matrix.
     */
    public void identity() {
        Matrix.setIdentityM(this.values, 0);
    }

    /**
     * Set this <code>Matrix4</code> to be the inverse of itself. <br />
     * Does nothing if it cannot be inverted.
     */
    public void invert() {
        if (Matrix.invertM(mPrivateTemp, 0, values, 0)) {
            set(mPrivateTemp);
        }
    }

    /**
     * Set this <code>Matrix4</code> to a viewing matrix positioned at <code>(eyeX, eyeY, eyeZ)</code> looking at point <code>(centerX, centerY, centerZ)</code> oriented with the given vector as the
     * up direction
     * 
     * @param eyeX The x-component of the position on which this viewing matrix is positioned
     * @param eyeY The y-component of the position on which this viewing matrix is positioned
     * @param eyeZ The z-component of the position on which this viewing matrix is positioned
     * @param centerX The x-component of the point that this viewing matrix is oriented towards
     * @param centerY The y-component of the point that this viewing matrix is oriented towards
     * @param centerZ The z-component of the point that this viewing matrix is oriented towards
     * @param upX The x-component of the up vector
     * @param upY The y-component of the up vector
     * @param upZ The z-component of the up vector
     */
    public void lookAt(final float eyeX, final float eyeY, final float eyeZ, final float centerX, final float centerY, final float centerZ, final float upX, final float upY, final float upZ) {
        Matrix.setLookAtM(values, 0, eyeX, eyeY, eyeZ, centerX, centerY, centerZ, upX, upY, upZ);
    }

    /**
     * Multiply this <code>Matrix4</code> with the other and store the result in this instance. <br />
     * This instance is the right hand matrix.
     * 
     * @param other The left hand side <code>Matrix4</code>
     */
    public void multiply(final Matrix4 other) {
        Matrix.multiplyMM(values, 0, other.values, 0, values, 0);
    }
    public void multiply(final float[] other) {
        Matrix.multiplyMM(values, 0, other, 0, values, 0);
    }
    public void multiplyRev(final Matrix4 other) {
        Matrix.multiplyMM(values, 0, values, 0, other.values, 0);
    }
    public void multiplyRev(final float[] other) {
        Matrix.multiplyMM(values, 0, values, 0, other, 0);
    }

    // Matrix.orthoM(m, mOffset, left, right, bottom, top, near, far)

    /**
     * Set the values of this <code>Matrix4</code> as an orthographic projection matrix defined by the given clip panes.
     * 
     * @param left Must not be equal to <code>right</code>
     * @param right Must not be equal to <code>left</code>
     * @param bottom Must not be equal to <code>top</code>
     * @param top Must not be equal to <code>bottom</code>
     * @param near Must not be equal to <code>far</code> and must be positive
     * @param far Must not be equal to <code>near</code> and must be positive
     * @see #frustum(left, right, bottom, top, near, far)
     */
    public void ortho(final float left, final float right, final float bottom, final float top, final float near, final float far) {
        Matrix.orthoM(values, 0, left, right, bottom, top, near, far);
    }

    /**
     * Set the values of this <code>Matrix4</code> as a projection matrix defined by a field of view, an aspect ratio, and z-axis clip panes.
     * 
     * @param fov The field of view in the y direction, in degrees
     * @param aspect The width to height aspect ratio
     * @param near Must not be equal to <code>far</code>
     * @param far Must not be equal to <code>near</code>
     */
    @SuppressLint("NewApi")
    public void perspective(final float fov, final float aspect, final float near, final float far) {
        Matrix.perspectiveM(values, 0, fov, aspect, near, far);
    }

    /**
     * Rotate this <code>Matrix4</code> by the given angle around the given axis.
     * 
     * @param degrees Angle in degrees to rotate
     * @param x The x-component of the rotation axis
     * @param y The y-component of the rotation axis
     * @param z The z-component of the rotation axis
     */
    public void rotate(final float degrees, final float x, final float y, final float z) {
        Matrix.rotateM(values, 0, degrees, x, y, z);
    }

    /**
     * Scale this <code>Matrix4</code> by the given scale components
     * 
     * @param sx The x-component of the scale
     * @param sy The y-component of the scale
     * @param sz The z-component of the scale
     */
    public void scale(final float sx, final float sy, final float sz) {
        Matrix.scaleM(values, 0, sx, sy, sz);
    }

    /**
     * Set the values of this <code>Matrix4</code> to those given in the array.
     * 
     * @param val The values to set
     */
    public void set(final float[] val) {
        System.arraycopy(val, 0, values, 0, values.length);
    }

    /**
     * Set the values of this <code>Matrix4</code> to those of the other.
     * 
     * @param other The <code>Matrix4</code> from which to copy values
     */
    public void set(final Matrix4 other) {
        set(other.values);
    }

    /**
     * Set this <code>Matrix4</code> to the rotation matrix rotating <code>degrees</code> degrees around the given axis.
     * 
     * @param degrees The angle in degrees to rotate
     * @param x The x-component of the axis around which the rotation occurs
     * @param y The y-component of the axis around which the rotation occurs
     * @param z The z-component of the axis around which the rotation occurs
     */
    public void setRotate(final float degrees, final float x, final float y, final float z) {
        Matrix.setRotateM(values, 0, degrees, x, y, z);
    }

    /**
     * Set this <code>Matrix4</code> to the rotation matrix described by the given Euler angles.
     * 
     * @param ax The x-component of the rotation, in degrees
     * @param ay The y-component of the rotation, in degrees
     * @param az The z-component of the rotation, in degrees
     */
    public void setRotateEuler(final float ax, final float ay, final float az) {
        Matrix.setRotateEulerM(values, 0, ax, ay, az);
    }

    /**
     * Translate this <code>Matrix4</code> with the given values.
     * 
     * @param x The x-component of the translation
     * @param y The y-component of the translation
     * @param z The z-component of the translation
     */
    public void translate(final float x, final float y, final float z) {
        Matrix.translateM(values, 0, x, y, z);
    }

    /**
     * Transpose this <code>Matrix4</code> in place.
     */
    public void transpose() {
        Matrix.transposeM(mPrivateTemp, 0, values, 0);
        set(mPrivateTemp);
    }
}

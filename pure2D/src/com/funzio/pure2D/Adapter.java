/**
 * 
 */
package com.funzio.pure2D;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.opengl.GLSurfaceView;

/**
 * @author long
 */
public interface Adapter {
    public void setSurface(GLSurfaceView view);

    public GLSurfaceView getSurface();

    public void onSurfaceCreated(final GL10 gl, final EGLConfig config);

    public void onSurfaceChanged(final GL10 gl, final int width, int height);

    public void onDrawFrame(final GL10 gl);

    public void onActivityStart(final Activity activity);

    public void onActivityStop(final Activity activity);
}

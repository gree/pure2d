/**
 * 
 */
package com.funzio.pure2D.gl.gl10;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

import android.opengl.GLSurfaceView.EGLConfigChooser;

/**
 * @author long
 */
public class StencilEGLConfig implements EGLConfigChooser {

    private static int[] CONFIG_SPEC = {
            EGL10.EGL_STENCIL_SIZE, 8, EGL10.EGL_NONE
    };

    private static int[] MAJOR_MINOR = new int[] {
            2, 0
    };

    public StencilEGLConfig() {
        // TODO Auto-generated constructor stub
    }

    /*
     * (non-Javadoc)
     * @see android.opengl.GLSurfaceView.EGLConfigChooser#chooseConfig(javax.microedition.khronos.egl.EGL10, javax.microedition.khronos.egl.EGLDisplay)
     */
    @Override
    public EGLConfig chooseConfig(final EGL10 egl, final EGLDisplay display) {
        final EGLConfig[] configs = new EGLConfig[1];
        final int[] num_config = new int[1];

        if (egl.eglInitialize(display, MAJOR_MINOR) && egl.eglChooseConfig(display, CONFIG_SPEC, configs, 1, num_config)) {
            return configs[0];
        }

        return null;
    }

}

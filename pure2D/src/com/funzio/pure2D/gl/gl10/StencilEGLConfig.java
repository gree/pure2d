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
            EGL10.EGL_STENCIL_SIZE, 8, //
            EGL10.EGL_DEPTH_SIZE, 8, //
            EGL10.EGL_NONE
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
        // Find up to 5 EGL configurations which match our spec. 5 is an arbitrary number.
        final EGLConfig[] configs = new EGLConfig[5];
        final int[] num_config = new int[1];

        if (egl.eglInitialize(display, MAJOR_MINOR) && egl.eglChooseConfig(display, CONFIG_SPEC, configs, configs.length, num_config)) {
            final int[] value = new int[1];

            // return the first config with DEPTH_SIZE >= 16, if available
            for (int i = 0; i < num_config[0]; i++) {
                egl.eglGetConfigAttrib(display, configs[i], EGL10.EGL_DEPTH_SIZE, value);
                if (value[0] >= 16) {
                    return configs[i];
                }
            }

            return configs[0];
        }

        return null;
    }
}

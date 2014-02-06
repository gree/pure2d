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

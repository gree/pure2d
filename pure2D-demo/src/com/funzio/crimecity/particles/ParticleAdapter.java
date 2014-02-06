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
package com.funzio.crimecity.particles;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.opengl.GLSurfaceView;

import com.funzio.crimecity.particles.explosions.ExplosionSparkParticle;
import com.funzio.crimecity.particles.gunshots.GunshotSmokeParticle;
import com.funzio.crimecity.particles.smoke.SmokeParticle;
import com.funzio.pure2D.Adapter;
import com.funzio.pure2D.Pure2D;
import com.funzio.pure2D.gl.gl10.BlendFunc;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.utils.ObjectPool;

/**
 * @author long
 */
public class ParticleAdapter implements Adapter {

    // enable / disable frame throttling, used for slow devices
    public static boolean FRAME_THROTTLE = false;

    // some reuseable blending functions
    public static final BlendFunc BF_ADD = BlendFunc.getAdd();

    // Particle Pools
    public static final ObjectPool<SmokeParticle> SMOKE_PARTICLES = new ObjectPool<SmokeParticle>(50);
    public static final ObjectPool<ExplosionSparkParticle> EXPLOSION_SPARK_PARTICLES = new ObjectPool<ExplosionSparkParticle>(50);
    public static final ObjectPool<GunshotSmokeParticle> GUNSHOT_SMOKE_PARTICLES = new ObjectPool<GunshotSmokeParticle>(50);
    public static ParticleTextureManager TEXTURE_MANAGER;

    private static ParticleAdapter sInstance = new ParticleAdapter();

    private GLSurfaceView mSurface;
    private GLState mGLState;

    private ParticleAdapter() {
        // TODO Auto-generated constructor stub
    }

    public static ParticleAdapter getInstance() {
        return sInstance;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.Adapter#setSurface(android.opengl.GLSurfaceView)
     */
    @Override
    public void setSurface(final GLSurfaceView view) {
        mSurface = view;

        // plug the adapter in
        Pure2D.ADAPTER = this;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.Adapter#getSurface()
     */
    @Override
    public GLSurfaceView getSurface() {
        return mSurface;
    }

    public void resetGLState() {
        mGLState.setColor(null);
        mGLState.setBlendFunc(null);
        mGLState.unbindTexture();
        mGLState.setTextureEnabled(true);
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.Adapter#onSurfaceCreated(javax.microedition.khronos.opengles.GL10, javax.microedition.khronos.egl.EGLConfig)
     */
    @Override
    public void onSurfaceCreated(final GL10 gl, final EGLConfig config) {
        // init the gl state, this is part of the Pure2D engine which we don't fully use in this game;
        // so we need to do this manually

        if (TEXTURE_MANAGER != null) {
            // unload the textures on the old GL
            TEXTURE_MANAGER.unloadAllTextures();
        }

        mGLState = new GLState(gl);
        // init textures
        if (TEXTURE_MANAGER == null) {
            // init
            TEXTURE_MANAGER = new ParticleTextureManager(mGLState, mSurface.getResources());
        } else {
            // just reload
            TEXTURE_MANAGER.reload(mGLState, mSurface.getResources());
        }
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.Adapter#onSurfaceChanged(javax.microedition.khronos.opengles.GL10, int, int)
     */
    @Override
    public void onSurfaceChanged(final GL10 gl, final int width, final int height) {
        // TODO nothing yet
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.Adapter#onDrawFrame(javax.microedition.khronos.opengles.GL10)
     */
    @Override
    public void onDrawFrame(final GL10 gl) {
        // TODO nothing yet
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.Adapter#onActivityStop()
     */
    @Override
    public void onActivityStart(final Activity activity) {
        // TODO nothing
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.Adapter#onActivityStop()
     */
    @Override
    public void onActivityStop(final Activity activity) {
        // if (TEXTURE_MANAGER != null) {
        // TEXTURE_MANAGER.removeAllTextures();
        // TEXTURE_MANAGER = null;
        // TEXTURE_MANAGER = null;
        //
        // // unplug the adapter
        // Pure2D.ADAPTER = null;
        // }
    }

}

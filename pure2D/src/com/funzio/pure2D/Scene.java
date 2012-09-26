/**
 * 
 */
package com.funzio.pure2D;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView.Renderer;

import com.funzio.pure2D.containers.Container;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.gl.gl10.textures.TextureManager;

/**
 * @author long
 */
public interface Scene extends Renderer, Container {
    public static final int AXIS_BOTTOM_LEFT = 0;
    public static final int AXIS_TOP_LEFT = 1;
    public static final int DEFAULT_FPS = 60; // frame per second
    public static final int DEFAULT_MSPF = 1000 / DEFAULT_FPS; // ms per frame

    public void setStage(Stage stage);

    public Stage getStage();

    public void setAxisSystem(int value);

    public int getAxisSystem();

    public GLState getGLState();

    public TextureManager getTextureManager();

    public void queueEvent(Runnable r);

    public void pause();

    public void resume();

    public void dispose();

    public interface Listener {
        void onSurfaceCreated(GL10 gl);
    }
}

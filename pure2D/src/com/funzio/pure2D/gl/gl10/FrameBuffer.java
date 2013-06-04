/**
 * 
 */
package com.funzio.pure2D.gl.gl10;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11ExtensionPack;

import android.graphics.PointF;
import android.opengl.GLU;
import android.util.Log;

import com.funzio.pure2D.Pure2D;
import com.funzio.pure2D.Scene;
import com.funzio.pure2D.gl.gl10.textures.BufferTexture;
import com.funzio.pure2D.gl.gl10.textures.Texture;

/**
 * @author long
 */
public class FrameBuffer {
    private static final String TAG = FrameBuffer.class.getSimpleName();

    private GLState mGLState;
    private GL10 mGL;
    private GL11ExtensionPack mGL11Ex;
    private Texture mTexture;
    private int mFrameBuffer = 0;
    private int mDepthBuffer = 0;
    private int mOriginalBuffer = 0;
    private int mWidth = 0;
    private int mHeight = 0;
    private final boolean mDepthEnabled;

    private int[] mOriginalViewport = new int[4];
    private boolean mBinded = false;

    public FrameBuffer(final GLState glState, final int width, final int height, final boolean checkPo2) {
        this(glState, width, height, checkPo2, false);
    }

    public FrameBuffer(final GLState glState, final int width, final int height, final boolean checkPo2, final boolean depthEnabled) {
        mGLState = glState;
        mGL = glState.mGL;
        mGL11Ex = (GL11ExtensionPack) mGL;
        mDepthEnabled = depthEnabled;

        init();

        // auto create a new texture
        attachTexture(new BufferTexture(mGLState, width, height, checkPo2));
    }

    @Deprecated
    /**
     * This method somehow does not work for some certain devices such as Samsung S2
     * @param gl
     * @param texture
     */
    public FrameBuffer(final GL10 gl, final Texture texture) {
        mGL = gl;
        mGL11Ex = (GL11ExtensionPack) gl;
        mDepthEnabled = false;

        init();

        // attach the texture
        attachTexture(texture);
    }

    private void init() {
        // this is a must for some certain devices such as Samsung S2
        mGLState.unbindTexture();

        // get the original buffer
        int[] originalBuffers = new int[1];
        mGL11Ex.glGetIntegerv(GL11ExtensionPack.GL_FRAMEBUFFER_BINDING_OES, originalBuffers, 0);
        mOriginalBuffer = originalBuffers[0];
        // create frame buffer
        int[] framebuffers = new int[1];
        mGL11Ex.glGenFramebuffersOES(1, framebuffers, 0);
        mFrameBuffer = framebuffers[0];

        // error checking
        int error = mGL.glGetError();
        Log.v(TAG, "init(); id: " + mFrameBuffer + ", error: " + error + " - " + GLU.gluErrorString(error));
    }

    public void attachTexture(final Texture texture) {
        mTexture = texture;
        mWidth = (int) mTexture.getSize().x;
        mHeight = (int) mTexture.getSize().y;

        // bind frame buffer temporarily
        mGL11Ex.glBindFramebufferOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES, mFrameBuffer);

        // attach the texture
        mGL11Ex.glFramebufferTexture2DOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES, GL11ExtensionPack.GL_COLOR_ATTACHMENT0_OES, GL10.GL_TEXTURE_2D, mTexture.getTextureID(), 0);

        if (mDepthEnabled) {
            // create depth buffer
            int[] depthBuffers = new int[1];
            mGL11Ex.glGenRenderbuffersOES(1, depthBuffers, 0);
            mDepthBuffer = depthBuffers[0];
            mGL11Ex.glBindRenderbufferOES(GL11ExtensionPack.GL_RENDERBUFFER_OES, mDepthBuffer);

            // Depth enable
            // A renderbuffer are just objects which are used to support offscreen rendering,
            // often for sections of the framebuffer which don't have a texture format associated with them such as the stencil or depth buffer.
            mGL11Ex.glRenderbufferStorageOES(GL11ExtensionPack.GL_RENDERBUFFER_OES, GL11ExtensionPack.GL_DEPTH_COMPONENT24, mWidth, mHeight);
            mGL11Ex.glFramebufferRenderbufferOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES, GL11ExtensionPack.GL_DEPTH_ATTACHMENT_OES, GL11ExtensionPack.GL_RENDERBUFFER_OES, mDepthBuffer);

            // stencil enable
            // mGL11Ex.glRenderbufferStorageOES(GL11ExtensionPack.GL_RENDERBUFFER_OES, GL11ExtensionPack.GL_STENCIL_INDEX8_OES, mWidth, mHeight);
            // mGL11Ex.glFramebufferRenderbufferOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES, GL11ExtensionPack.GL_STENCIL_ATTACHMENT_OES, GL11ExtensionPack.GL_RENDERBUFFER_OES, mDepthBuffer);
        }

        final int status = mGL11Ex.glCheckFramebufferStatusOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES);
        if (status != GL11ExtensionPack.GL_FRAMEBUFFER_COMPLETE_OES) {
            Log.e(TAG, "Failed to generate FrameBuffer: " + getStatusString(status) + "\n" + Log.getStackTraceString(new Exception()));
            // throw new RuntimeException(msg + ": " + Integer.toHexString(status));
        } else {
            // make sure to clear all the pixels initially. Needed for some devices such as Nexus 7 (Jelly Bean)
            mGL.glClearColor(0f, 0f, 0f, 0f);
            mGL.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        }

        // restore back to the original buffer
        mGL11Ex.glBindFramebufferOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES, mOriginalBuffer);
    }

    // public void bind() {
    // bind(Scene.AXIS_BOTTOM_LEFT);
    // }

    /**
     * Bind this frame buffer
     */
    public void bind(final int projection) {
        if (mBinded) {
            return;
        }

        // flag
        mBinded = true;

        mGL11Ex.glBindFramebufferOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES, mFrameBuffer);

        // Select the projection matrix
        mGL.glMatrixMode(GL10.GL_PROJECTION);
        // Reset the projection matrix
        mGL.glPushMatrix();
        mGL.glLoadIdentity();

        // back up the viewport
        mOriginalViewport = mGLState.getViewport();
        // set new viewport
        mGLState.setViewport(0, 0, mWidth, mHeight);

        // set new projection matrix
        if (projection == Scene.PROJECTION_PERSPECTIVE) {
            GLU.gluPerspective(mGL, Pure2D.GL_PERSPECTIVE_FOVY, (float) mWidth / (float) mHeight, 0.001f, Math.max(mWidth, mHeight));
            GLU.gluLookAt(mGL, 0, 0, mHeight, 0, 0, 0, 0, 1, 0); // always based on Screen-Y
        } else if (projection == Scene.AXIS_TOP_LEFT) {
            // NOTE: frame-buffer has the Axis inverted
            mGL.glOrthof(0, mWidth, mHeight, 0, -1, 1);
        } else {
            mGL.glOrthof(0, mWidth, 0, mHeight, -1, 1);
        }

        // back to model
        mGL.glMatrixMode(GL10.GL_MODELVIEW);
        // Reset the modelview matrix
        mGL.glPushMatrix();
        mGL.glLoadIdentity();

        if (projection == Scene.PROJECTION_PERSPECTIVE) {
            // offset the translation
            mGL.glTranslatef(-(float) mWidth / 2f, -(float) mHeight / 2f, 0);
        }

        // toggle depth test
        mGLState.setDepthTestEnabled(mDepthEnabled);
    }

    /**
     * Unbind this frame buffer and switch back to the original buffer
     */
    public void unbind() {
        if (!mBinded) {
            return;
        }

        // unflag
        mBinded = false;

        // back to the previous buffer
        mGL11Ex.glBindFramebufferOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES, mOriginalBuffer);

        mGL.glMatrixMode(GL10.GL_PROJECTION);
        // Restore the projection matrix
        mGL.glPopMatrix();

        // Restore the view port
        mGLState.setViewport(mOriginalViewport[0], mOriginalViewport[1], mOriginalViewport[2], mOriginalViewport[3]);

        // back to model
        mGL.glMatrixMode(GL10.GL_MODELVIEW);
        // Reset the modelview matrix
        mGL.glPopMatrix();
    }

    public boolean isBinded() {
        return mBinded;
    }

    public void clear() {
        if (!mBinded) {
            return;
        }

        mGL.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
    }

    /**
     * Unbind and delete the frame and depth buffers
     */
    public void unload() {
        Log.v(TAG, "unload(): " + mFrameBuffer);
        unbind();

        // delete depth buffer
        if (mDepthBuffer > 0) {
            int[] buffers = {
                mDepthBuffer
            };
            mGL11Ex.glDeleteRenderbuffersOES(1, buffers, 0);
            mDepthBuffer = 0;
        }

        // delete frame buffer
        if (mFrameBuffer > 0) {
            int[] buffers = {
                mFrameBuffer
            };
            mGL11Ex.glDeleteFramebuffersOES(1, buffers, 0);
            mFrameBuffer = 0;
        }

        // this is a must for some certain devices such as Samsung S2
        mGLState.unbindTexture();
    }

    public Texture getTexture() {
        return mTexture;
    }

    public int getWidth() {
        return mWidth;
    }

    public int getHeight() {
        return mHeight;
    }

    public boolean hasSize(final PointF size) {
        return mWidth == Math.round(size.x) && mHeight == Math.round(size.y);
    }

    public static boolean isSupported(final GL10 gl) {
        // check for the extension
        String extensions = gl.glGetString(GL10.GL_EXTENSIONS);
        return (extensions.indexOf(" GL_OES_framebuffer_object ") >= 0);
    }

    public static String getStatusString(final int status) {
        String msg = "GL_FRAMEBUFFER_COMPLETE_OES";
        if (status == GL11ExtensionPack.GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_OES) {
            msg = "GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT_OES";
        } else if (status == GL11ExtensionPack.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_OES) {
            msg = "GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS_OES";
        } else if (status == GL11ExtensionPack.GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_OES) {
            msg = "GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER_OES";
        } else if (status == GL11ExtensionPack.GL_FRAMEBUFFER_INCOMPLETE_FORMATS_OES) {
            msg = "GL_FRAMEBUFFER_INCOMPLETE_FORMATS_OES";
        } else if (status == GL11ExtensionPack.GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_OES) {
            msg = "GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT_OES";
        } else if (status == GL11ExtensionPack.GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_OES) {
            msg = "GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER_OES";
        }
        return msg;
    }

}

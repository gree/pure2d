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
package com.funzio.pure2D.lwf;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.util.Log;

import com.funzio.pure2D.gl.gl10.textures.Texture;

public class LWFData {
    static {
        LWF.loadLibrary();
    }

    public static boolean LOG_ENABLED = true;
    private static final String TAG = LWFData.class.getSimpleName();
    private static final int HEADER_SIZE = 324;

    private LWFManager mManager;
    private int mId = -1;

    private native int create(byte[] data);

    private native String getName(int lwfDataId);

    private native int getTextureNum(int lwfDataId);

    private native String getTextureName(int lwfDataId, int textureNo);

    private native void setGLTexture(int lwfDataId, int[] glTextureIds, float[] glTextureUs, float[] glTextureVs);

    private native void destroy(int lwfDataId);

    public static native void disposeAll();

    public LWFData(final InputStream stream) throws Exception {
        init(null, stream);
    }

    public LWFData(final LWFManager lwfManager, final InputStream stream) throws Exception {
        init(lwfManager, stream);
    }

    private void init(final LWFManager lwfManager, final InputStream stream) throws Exception {
        mManager = lwfManager;

        byte[] header = new byte[HEADER_SIZE];
        stream.read(header);

        int length = ByteBuffer.wrap(header, header.length - 4, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();

        byte[] data = new byte[length];
        System.arraycopy(header, 0, data, 0, header.length);
        stream.read(data, header.length, length - header.length);

        mId = create(data);
    }

    public LWFManager getLWFManager() {
        return mManager;
    }

    public void setLWFManager(final LWFManager lwfManager) {
        mManager = lwfManager;
    }

    public int getId() {
        return mId;
    }

    public String getName() {
        return getName(mId);
    }

    public int getTextureNum() {
        return getTextureNum(mId);
    }

    public String getTextureName(final int textureNo) {
        return getTextureName(mId, textureNo);
    }

    public void setTextures(final Texture[] textures) {
        int textureNum = getTextureNum(mId);
        int[] glTextureIds = new int[textureNum];
        float[] glTextureUs = new float[textureNum];
        float[] glTextureVs = new float[textureNum];
        for (int i = 0; i < textureNum; ++i) {
            Texture texture = textures[i];
            if (texture != null) {
                glTextureIds[i] = texture.getTextureID();
                glTextureUs[i] = texture.mCoordScaleX;
                glTextureVs[i] = texture.mCoordScaleY;
            } else {
                // fail safe
                glTextureIds[i] = -1;
                glTextureUs[i] = 0;
                glTextureVs[i] = 0;
            }
        }
        setGLTexture(mId, glTextureIds, glTextureUs, glTextureVs);
    }

    public void dispose() {
        if (mId != -1) {
            if (LOG_ENABLED) {
                Log.i(TAG, "dispose()");
            }
            destroy(mId);
            mId = -1;
            if (mManager != null) {
                mManager.removeLWFData(this);
            }
            mManager = null;
        }
    }
}

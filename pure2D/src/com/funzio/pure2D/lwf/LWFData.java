package com.funzio.pure2D.lwf;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.util.Log;

import com.funzio.pure2D.gl.gl10.textures.Texture;

public class LWFData {
    static {
        System.loadLibrary("pure2d");
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

    public LWFData(final LWFManager lwfManager, InputStream stream) throws Exception {
        mManager = lwfManager;

        byte[] header = new byte[HEADER_SIZE];
        stream.read(header);

        int length = ByteBuffer.wrap(header,
            header.length - 4, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();

        byte[] data = new byte[length];
        System.arraycopy(header, 0, data, 0, header.length);
        stream.read(data, header.length, length - header.length);

        mId = create(data);
    }

    public LWFManager getLWFManager() {
        return mManager;
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

    public String getTextureName(int textureNo) {
        return getTextureName(mId, textureNo);
    }

    public void setTextures(Texture[] textures) {
        int textureNum = getTextureNum(mId);
        int[] glTextureIds = new int[textureNum];
        float[] glTextureUs = new float[textureNum];
        float[] glTextureVs = new float[textureNum];
        for (int i = 0; i < textureNum; ++i) {
            Texture texture = textures[i];
            glTextureIds[i] = texture.getTextureID();
            glTextureUs[i] = texture.mCoordScaleX;
            glTextureVs[i] = texture.mCoordScaleY;
        }
        setGLTexture(mId, glTextureIds, glTextureUs, glTextureVs);
    }

    public void dispose() {
        if (mId != -1) {
            if (LOG_ENABLED) {
                Log.e(TAG, "dispose()");
            }
            destroy(mId);
            mManager = null;
            mId = -1;
        }
    }
}

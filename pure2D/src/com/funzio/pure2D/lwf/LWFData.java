package com.funzio.pure2D.lwf;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.content.res.AssetManager;
import android.util.Log;

import com.funzio.pure2D.Scene;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.gl.gl10.textures.TextureOptions;

public class LWFData {
    static {
        System.loadLibrary("pure2d");
    }

    public static boolean LOG_ENABLED = true;
    private static final String TAG = LWFData.class.getSimpleName();
    private static final int HEADER_SIZE = 324;

    private LWFManager mManager;
    private String mPath;
    private int mId = -1;
    private int mRefCount = 0;
    private Texture[] mTextures;

    private native int create(byte[] data);
    private native int getTextureNum(int lwfDataId);
    private native String getTextureName(int lwfDataId, int textureNo);
    private native void setGLTexture(int lwfDataId, int[] glTextureIds, float[] glTextureUs, float[] glTextureVs);
    private native void destroy(int lwfDataId);

    public LWFData(final Scene scene, final AssetManager assetManager, final String path) {
        try {
            mManager = scene.getLWFManager();
            mPath = path;
            InputStream stream = assetManager.open(path);

            byte[] header = new byte[HEADER_SIZE];
            stream.read(header);

            int length = ByteBuffer.wrap(header,
                header.length - 4, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();

            byte[] data = new byte[length];
            System.arraycopy(header, 0, data, 0, header.length);
            stream.read(data, header.length, length - header.length);

            mId = create(data);

            String base = "";
            int index = path.lastIndexOf('/');
            if (index > 0)
                base = path.substring(0, index + 1);

            int textureNum = getTextureNum(mId);
            mTextures = new Texture[textureNum];
            int[] glTextureIds = new int[textureNum];
            float[] glTextureUs = new float[textureNum];
            float[] glTextureVs = new float[textureNum];
            for (int i = 0; i < textureNum; ++i) {
                String name = getTextureName(mId, i);
                Texture texture = scene.getTextureManager().createAssetTexture(base + name, null);
                mTextures[i] = texture;
                glTextureIds[i] = texture.getTextureID();
                glTextureUs[i] = texture.mCoordScaleX;
                glTextureVs[i] = texture.mCoordScaleY;
            }
            setGLTexture(mId, glTextureIds, glTextureUs, glTextureVs);
        } catch (Exception e) {
            Log.e(TAG, "ERROR", e);
        }
    }

    public int getId() {
        return mId;
    }

    public void deleteReference() {
        if (--mRefCount <= 0) {
            mManager.removeLWFData(this);
            dispose();
        }
    }

    public void addReference() {
        ++mRefCount;
    }

    public String getPath() {
        return mPath;
    }

    public void dispose() {
        if (mId != -1) {
            if (LOG_ENABLED) {
                Log.e(TAG, "dispose()");
            }
            destroy(mId);
            // unload textures?
            mTextures = null;
            mManager = null;
            mId = -1;
        }
    }
}

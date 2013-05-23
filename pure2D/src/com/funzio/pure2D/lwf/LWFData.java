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

    private static final String TAG = LWFData.class.getSimpleName();
    private static final int HEADER_SIZE = 324;

    private int mId = -1;

    private native int create(byte[] data);
    private native int getTextureNum(int lwfDataId);
    private native String getTextureName(int lwfDataId, int textureNo);
    private native void setGLTextureId(int lwfDataId, int[] glTextureIds);
    private native void destroy(int lwfDataId);

    public LWFData(final Scene scene, final AssetManager assetManager, final String filePath) {
        try {
            InputStream stream = assetManager.open(filePath);

            byte[] header = new byte[HEADER_SIZE];
            stream.read(header);

            int length = ByteBuffer.wrap(header,
                header.length - 4, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();

            byte[] data = new byte[length];
            System.arraycopy(header, 0, data, 0, header.length);
            stream.read(data, header.length, length - header.length);

            mId = create(data);

            String base = "";
            int index = filePath.lastIndexOf('/');
            if (index > 0)
                base = filePath.substring(0, index + 1);

            int textureNum = getTextureNum(mId);
            int[] glTextureIds = new int[textureNum];
            for (int i = 0; i < textureNum; ++i) {
                String name = getTextureName(mId, i);
                Texture texture = scene.getTextureManager().createAssetTexture(base + name, null);
                glTextureIds[i] = texture.getTextureID();
            }
            setGLTextureId(mId, glTextureIds);
        } catch (Exception e) {
            Log.e(TAG, "ERROR", e);
        }
    }

    public int getId() {
        return mId;
    }

    public void dispose() {
        destroy(mId);
        mId = -1;
    }
}

package com.funzio.pure2D.lwf;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import android.content.res.AssetManager;
import android.util.Log;

public class LWFData {
    static {
        System.loadLibrary("pure2d");
    }

    private static final String TAG = LWFData.class.getSimpleName();
    private static final int HEADER_SIZE = 324;

    private int mId = -1;

    private native int create(byte[] data);
    private native void destroy(int lwfDataId);

    public LWFData(final AssetManager assetManager, final String filePath) {
        try {
            load(assetManager.open(filePath));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public LWFData(final InputStream stream) {
        try {
            load(stream);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void load(final InputStream stream) throws Exception {
        byte[] header = new byte[HEADER_SIZE];
        stream.read(header);

        int length = ByteBuffer.wrap(header,
            header.length - 4, 4).order(ByteOrder.LITTLE_ENDIAN).getInt();

        byte[] data = new byte[length];
        System.arraycopy(header, 0, data, 0, header.length);
        stream.read(data, header.length, length - header.length);

        mId = create(data);
    }

    public int getId() {
        return mId;
    }

    public void dispose() {
        destroy(mId);
        mId = -1;
    }
}

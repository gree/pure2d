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
package com.funzio.pure2D.sounds;

import java.io.IOException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.util.Log;

/**
 * @author sajjadtabib
 */
public class AssetMedia extends AbstractMedia {
    protected static final String TAG = AssetMedia.class.getSimpleName();

    private AssetFileDescriptor mAssetFd;

    /**
     * @param key
     * @param assets
     * @param filePath
     */
    public AssetMedia(final int key, final AssetManager assets, final String filePath) {
        super(key);

        try {
            mAssetFd = assets.openFd(filePath);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public AssetFileDescriptor getAssetFileDescriptor() {
        return mAssetFd;
    }

    public int load(final MediaPlayer player, final Context context) {
        try {
            player.setDataSource(mAssetFd.getFileDescriptor(), mAssetFd.getStartOffset(), mAssetFd.getLength());
        } catch (Exception e) {
            String message = e.getMessage();
            Log.e(TAG, message == null ? "Failed to load AssetMedia" : message);
            return 0;
        }

        return 1;
    }
}

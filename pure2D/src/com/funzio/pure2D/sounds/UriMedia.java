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

import java.io.File;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

/**
 * @author sajjadtabib
 */
public class UriMedia extends AbstractMedia {
    protected static final String TAG = UriMedia.class.getSimpleName();

    protected Uri mUri;

    /**
     * @param key
     * @param filePath
     */
    public UriMedia(final int key, final String filePath) {
        super(key);

        mUri = Uri.fromFile(new File(filePath));

    }

    public UriMedia(final int key, final Uri uri) {
        super(key);
        mUri = uri;

    }

    public Uri getMediaUri() {
        return mUri;
    }

    public int load(final MediaPlayer player, final Context context) {
        try {
            player.setDataSource(context, mUri);
        } catch (Exception e) {
            String message = e.getMessage();
            Log.e(TAG, message == null ? "Failed to load AssetMedia" : message);
            return 0;
        }

        return 1;
    }
}

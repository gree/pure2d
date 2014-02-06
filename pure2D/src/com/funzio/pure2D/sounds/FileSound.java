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

import android.media.SoundPool;
import android.util.Log;

/**
 * @author long
 */
public class FileSound extends AbstractSound {
    protected static final String TAG = FileSound.class.getSimpleName();

    public static final long BITRATE = 16L; // in kbps

    private final String mFilePath;

    public FileSound(final int key, final String filePath) {
        super(key);

        mFilePath = filePath;
    }

    public int load(final SoundPool soundPool) {
        Log.v(TAG, "load(" + mFilePath + ")");

        File file = new File(mFilePath);
        long fileSize = file.length();
        mLength = fileSize / BITRATE;

        return mSoundID = soundPool.load(mFilePath, mPriority);
    }
}

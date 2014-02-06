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
import java.util.HashSet;

public class LWFManager {
    public static boolean LOG_ENABLED = true;
    // private static final String TAG = LWFManager.class.getSimpleName();

    private HashSet<LWFData> mDatas;
    private HashSet<LWF> mLWFs;
    private boolean mRemoving = false;

    public LWFManager() {
        mDatas = new HashSet<LWFData>();
        mLWFs = new HashSet<LWF>();
    }

    public LWFData createLWFData(final InputStream stream) throws Exception {
        LWFData data = new LWFData(this, stream);
        synchronized (mDatas) {
            mDatas.add(data);
        }
        return data;
    }

    public void addLWFData(final LWFData data) {
        data.setLWFManager(this);
        synchronized (mDatas) {
            mDatas.add(data);
        }
    }

    public LWF createLWF() {
        LWF lwf = new LWF(this);
        synchronized (mLWFs) {
            mLWFs.add(lwf);
        }
        return lwf;
    }

    public LWF createLWF(final LWFData data) {
        LWF lwf = new LWF(this, data);
        synchronized (mLWFs) {
            mLWFs.add(lwf);
        }
        return lwf;
    }

    public void removeLWFData(final LWFData data) {
        if (!mRemoving) {
            synchronized (mDatas) {
                mDatas.remove(data);
            }
        }
    }

    public void removeLWF(final LWF lwf) {
        if (!mRemoving) {
            synchronized (mLWFs) {
                mLWFs.remove(lwf);
            }
        }
    }

    public void dispose() {
        mRemoving = true;
        synchronized (mLWFs) {
            for (LWF lwf : mLWFs) {
                lwf.dispose();
            }
        }
        synchronized (mDatas) {
            for (LWFData data : mDatas) {
                data.dispose();
            }
        }
        mRemoving = false;
        synchronized (mLWFs) {
            mLWFs.clear();
        }
        synchronized (mDatas) {
            mDatas.clear();
        }
    }
}

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
package com.funzio.pure2D.app;

import android.app.Application;
import android.os.Handler;
import android.widget.Toast;

import com.funzio.pure2D.sounds.SoundManager;
import com.funzio.pure2D.ui.UIManager;

/**
 * @author long.ngo
 */
public class Pure2DAppplication extends Application {

    protected UIManager mUIManager;
    protected SoundManager mSoundManager;

    protected Handler mHandler;
    protected Toast mCurrentToast;

    public Pure2DAppplication() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mHandler = new Handler();

        // init UI manager
        mUIManager = UIManager.getInstance();
        mUIManager.setContext(this);
        // load ui config
        mUIManager.loadConfig("ui_config.json");// .applyScale(0.5f);

        // create sound manager
        mSoundManager = createSoundManager();
    }

    protected SoundManager createSoundManager() {
        return new SoundManager(this, 10);
    }

    public UIManager getUIManager() {
        return mUIManager;
    }

    public SoundManager getSoundManager() {
        return mSoundManager;
    }

    public Handler getHandler() {
        return mHandler;
    }

    public void showToast(final int stringResId, final int duration) {
        showToast(getString(stringResId), duration);
    }

    public void showToast(final String text, final int duration) {

        mHandler.post(new Runnable() {

            @Override
            public void run() {
                if (mCurrentToast != null) {
                    mCurrentToast.cancel();
                    mCurrentToast = null;
                }

                mCurrentToast = Toast.makeText(Pure2DAppplication.this, text, duration);
                mCurrentToast.show();
            }
        });
    }

}

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
package jp.gree.casino.machine;

import jp.gree.casino.scene.CasinoScene;
import jp.gree.casino.scene.CasinoTextureManager;

import android.os.Handler;

import com.funzio.pure2D.containers.DisplayGroup;
import com.funzio.pure2D.shapes.Sprite;

/**
 * @author long
 */
public class SlotMachine extends DisplayGroup {
    public static final int STATUS_STILL = 0;
    public static final int STATUS_SPINNING = 1;

    // XXX this constants need to be from the db
    protected static final String ASSET_DIR = "mayan";
    protected static final int BACK_OFFSET_X = 88;
    protected static final int BACK_OFFSET_Y = 115;
    protected static final int REEL_OFFSET_X = BACK_OFFSET_X + 10;
    protected static final int REEL_OFFSET_Y = BACK_OFFSET_Y + 10;
    protected static final int REEL_GAP = 15;
    protected static final int REEL_WIDTH = 140;
    protected static final int REEL_HEIGHT = (REEL_WIDTH + REEL_GAP) * 3;

    protected String mName;
    protected String mAssetDir;
    protected Sprite mFront;
    protected Sprite mBack;
    protected Reel[] mReels;

    protected CasinoTextureManager mTextureManager;
    protected Handler mHandler;
    protected int mSpinIndex = 0;

    protected int mStatus = STATUS_STILL;

    public SlotMachine(final CasinoScene scene, final int numReels) {
        super();

        mName = ASSET_DIR;
        mAssetDir = ASSET_DIR;
        mReels = new Reel[numReels];
        mScene = scene;

        mTextureManager = (CasinoTextureManager) mScene.getTextureManager();
        mHandler = mScene.getStage().getHandler();

        createChildren();
    }

    /**
     * @return the name
     */
    public String getName() {
        return mName;
    }

    /**
     * @return the assetDir
     */
    public String getAssetDir() {
        return mAssetDir;
    }

    protected void createChildren() {
        // the back
        createBack();

        // the reels
        for (int i = 0; i < mReels.length; i++) {
            createReel(i);
        }

        // the front
        createFront();
    }

    private void createBack() {
        mBack = new Sprite();
        mBack.setPosition(BACK_OFFSET_X * mTextureManager.mTextureScaleX, BACK_OFFSET_Y * mTextureManager.mTextureScaleY);
        mBack.setTexture(mTextureManager.mBackTexture);
        addChild(mBack);
    }

    protected void createFront() {
        mFront = new Sprite();
        mFront.setTexture(mTextureManager.mFrontTexture);
        addChild(mFront);
    }

    protected void createReel(final int index) {
        Reel reel = new Reel(this);
        reel.setGap(REEL_GAP * mTextureManager.mTextureScaleY);
        reel.setSize(REEL_WIDTH * mTextureManager.mTextureScaleX, REEL_HEIGHT * mTextureManager.mTextureScaleY);
        reel.setPosition((REEL_OFFSET_X + (REEL_WIDTH + REEL_GAP) * index) * mTextureManager.mTextureScaleX, REEL_OFFSET_Y * mTextureManager.mTextureScaleY);
        addChild(reel);

        // add to the array
        mReels[index] = reel;
    }

    public Sprite getFront() {
        return mFront;
    }

    public Sprite getBack() {
        return mBack;
    }

    public void spin() {
        if (mStatus == STATUS_SPINNING) {
            return;
        }

        mStatus = STATUS_SPINNING;
        mSpinIndex = 0;
        spinNextReel();
    }

    // public void spin() {
    // mReels[0].scrollBy(0, 10);
    // }

    protected void spinNextReel() {
        mReels[mSpinIndex++].spin(-5, 0.002f);
        // mReels[mSpinIndex++].spin(-0.25f, 0);

        if (mSpinIndex < mReels.length) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    spinNextReel();
                }
            }, 100);
        } else {
            mStatus = STATUS_STILL;
        }
    }
}

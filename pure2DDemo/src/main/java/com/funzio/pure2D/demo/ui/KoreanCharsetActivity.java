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
package com.funzio.pure2D.demo.ui;

import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.CheckBox;

import com.funzio.pure2D.DisplayObject;
import com.funzio.pure2D.LoopModes;
import com.funzio.pure2D.Scene;
import com.funzio.pure2D.animators.AlphaAnimator;
import com.funzio.pure2D.animators.ColorAnimator;
import com.funzio.pure2D.containers.Alignment;
import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.gl.GLColor;
import com.funzio.pure2D.gl.gl10.BlendModes;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.shapes.Sprite;
import com.funzio.pure2D.text.BitmapFont;
import com.funzio.pure2D.text.BmfTextObject;
import com.funzio.pure2D.text.Characters;
import com.funzio.pure2D.text.TextOptions;
import com.longo.pure2D.demo.R;

/**
 * @author juni.kim
 */
public class KoreanCharsetActivity extends StageActivity {
    private static final String FONT_PATH = "fonts/handwriting_korean.ttf";
    private static final Interpolator INTERPOLATOR = new AccelerateDecelerateInterpolator();

    private Sprite mAtlasSprite;
    private BitmapFont mBitmapFont;
    private Typeface mTypeface;
    private PointF mTempPoint = new PointF();
    private boolean mCacheEnabled = false;

    private String[] mSayings;
    private int mNumSayings;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mSayings = getResources().getStringArray(R.array.korean_sayings);
        mNumSayings = mSayings.length;

        mScene.setRenderContinueously(true);
        mScene.setListener(new Scene.Listener() {

            @Override
            public void onSurfaceCreated(final GLState glState, final boolean firstTime) {
                if (firstTime) {
                    // load the textures
                    loadTexture();

                    if (mBitmapFont != null) {
                        mAtlasSprite = new Sprite();
                        mAtlasSprite.setTexture(mBitmapFont.getTexture());
                        mScene.addChild(mAtlasSprite);

                        addObject(mDisplaySizeDiv2.x, mDisplaySizeDiv2.y);
                    }
                }
            }
        });
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.samples.activities.StageActivity#getLayout()
     */
    @Override
    protected int getLayout() {
        return R.layout.stage_bmf;
    }

    private void loadTexture() {
        try {
            // find in assets folder
            mTypeface = Typeface.createFromAsset(getAssets(), FONT_PATH);
        } catch (Exception e) {
            return;
        }

        final TextOptions options = TextOptions.getDefault();
        options.inTextPaint.setTypeface(mTypeface);
        options.inTextPaint.setColor(Color.WHITE);
        options.inTextPaint.setTextSize(30f);

        mBitmapFont = new BitmapFont(Characters.getUniqueCharacters(mSayings), options);
        mBitmapFont.load(mScene.getGLState());
    }

    private BmfTextObject addObject(final float x, final float y) {
        mScene.screenToGlobal(x, y, mTempPoint);

        // create object
        final BmfTextObject obj = new BmfTextObject();
        obj.setTextAlignment(Alignment.HORIZONTAL_CENTER);
        obj.setCacheEnabled(mCacheEnabled);
        obj.setBitmapFont(mBitmapFont);
        obj.setText(mSayings[RANDOM.nextInt(mNumSayings)]);
        obj.setColor(GLColor.WHITE);
        obj.setBlendFunc(BlendModes.ADD_FUNC);

        obj.setPosition(mTempPoint);

        mScene.addChild(obj);

        // some animator
        final AlphaAnimator animator1 = new AlphaAnimator(INTERPOLATOR);
        obj.addManipulator(animator1);
        animator1.setLoop(LoopModes.LOOP_REVERSE);
        animator1.setDuration(5000);
        animator1.start(1f, 0f);

        final ColorAnimator animator2 = new ColorAnimator(INTERPOLATOR);
        obj.addManipulator(animator2);
        animator2.setLoop(LoopModes.LOOP_REVERSE);
        animator2.setDuration(2000);
        animator2.start(1f, 0.5f, 0.5f, 1f, 0.5f, 0.5f, 1f, 1f);

        return obj;
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        final int action = event.getAction();

        if (action == MotionEvent.ACTION_DOWN) {
            if (mBitmapFont.getTexture() != null) {
                mStage.queueEvent(new Runnable() {
                    @Override
                    public void run() {
                        int len = event.getPointerCount();
                        for (int i = 0; i < len; i++) {
                            addObject(event.getX(i), event.getY(i));
                        }
                    }
                });
            }
        }

        return true;
    }

    public void onClickAtlas(final View view) {
        if (mAtlasSprite != null) {
            mAtlasSprite.setVisible(((CheckBox) findViewById(R.id.cb_show_atlas)).isChecked());
        }
    }

    public void onClickCache(final View view) {
        mCacheEnabled = ((CheckBox) view).isChecked();

        mScene.queueEvent(new Runnable() {

            @Override
            public void run() {
                final int size = mScene.getNumChildren();
                for (int i = 0; i < size; i++) {
                    final DisplayObject child = mScene.getChildAt(i);
                    if (child instanceof BmfTextObject) {
                        ((BmfTextObject) child).setCacheEnabled(mCacheEnabled);
                    }
                }
            }
        });
    }
}

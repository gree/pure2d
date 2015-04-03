/**
 * ****************************************************************************
 * Copyright (C) 2012-2014 GREE, Inc.
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * ****************************************************************************
 */
package com.funzio.pure2D.demo.containers;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;

import com.funzio.pure2D.BaseScene;
import com.funzio.pure2D.containers.HList;
import com.funzio.pure2D.containers.renderers.GroupItemRenderer;
import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.gl.GLColor;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.shapes.Rectangular;
import com.funzio.pure2D.text.BitmapFont;
import com.funzio.pure2D.text.BmfTextObject;
import com.funzio.pure2D.text.Characters;
import com.funzio.pure2D.text.TextOptions;
import com.longo.pure2D.demo.R;

import java.util.ArrayList;

public class HListActivity extends StageActivity {
    private static final String TAG = HListActivity.class.getSimpleName();
    private static final String FONT_PATH = "fonts/foo.ttf";

    private BitmapFont mBitmapFont;
    private Typeface mTypeface;
    private HList<ItemData> mList;

    @Override
    protected int getLayout() {
        return R.layout.stage_hlist;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // for swiping
        mScene.setUIEnabled(true);
        // need to get the GL reference first
        mScene.setListener(new BaseScene.Listener() {

            @Override
            public void onSurfaceCreated(final GLState glState, final boolean firstTime) {
                if (firstTime) {

                    // load the textures
                    loadTextures();

                    // generate a lot of wheels
                    addList();
                }
            }
        });
    }

    private void loadTextures() {
        try {
            // find in assets folder
            mTypeface = Typeface.createFromAsset(getAssets(), FONT_PATH);
        } catch (Exception e) {
            Log.e(TAG, "Error creating font: " + FONT_PATH, e);
            return;
        }

        final TextOptions options = TextOptions.getDefault();
        options.inTextPaint.setTypeface(mTypeface);
        // options.inScaleX = options.inScaleY = 0.75f;
        options.inTextPaint.setColor(Color.YELLOW);
        options.inTextPaint.setTextSize(50);
        options.inPaddingX = options.inPaddingY = 4;
        options.inStrokePaint = new Paint(options.inTextPaint);
        options.inStrokePaint.setStrokeWidth(options.inTextPaint.getTextSize() / 5);
        options.inStrokePaint.setStyle(Paint.Style.STROKE);
        options.inStrokePaint.setColor(Color.BLACK);

        mBitmapFont = new BitmapFont(Characters.BASIC_SET, options);
        mBitmapFont.load(mScene.getGLState());
    }

    private void addList() {
        // data
        ArrayList<ItemData> data = new ArrayList<ItemData>();
        for (int i = 0; i < 20; i++) {
            data.add(new ItemData(GLColor.createRandom(), mBitmapFont, "Item " + i));
        }

        mList = new HList<ItemData>();
        mList.setGap(10);
//        mList.setSnapEnabled(true);
        mList.setSwipeEnabled(true);
        mList.setSize(mDisplaySize.x, mDisplaySize.y);
        try {
            mList.setItemRenderer(MyListItem.class);
        } catch (Exception e) {
            Log.e(TAG, null, e);
        }
        mList.setData(data);

        // add to scene
        mScene.addChild(mList);
    }

    @Override
    protected int getNumObjects() {
        return mScene.getNumGrandChildren();
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        // forward the event to scene
        mScene.onTouchEvent(event);

        return true;
    }

    public void onClickAdd(final View v) {
        mStage.queueEvent(new Runnable() {
            @Override
            public void run() {
                final int size = mList.getData().size();
                if (size > 0) {
                    final int index = (int) Math.round(Math.random() * (size - 1));
                    mList.addItem(new ItemData(GLColor.createRandom(), mBitmapFont, "Item Random " + size), index);
                } else {
                    mList.addItem(new ItemData(GLColor.createRandom(), mBitmapFont, "Item 0"));
                }
            }
        });
    }

    public void onClickRemove(final View v) {
        mStage.queueEvent(new Runnable() {
            @Override
            public void run() {
                final int index = (int) Math.round(Math.random() * (mList.getData().size() - 1));
                mList.removeItem(index);
            }
        });
    }

    public void onClickClear(final View v) {
        mStage.queueEvent(new Runnable() {
            @Override
            public void run() {
                mList.removeAllItems();
            }
        });
    }

    public void onClickRepeating(final View view) {
        mList.setRepeating(((CheckBox) view).isChecked());
    }

    public static class ItemData {
        public GLColor bgColor;
        public BitmapFont font;
        public String label;

        public ItemData(GLColor color, BitmapFont f, String l) {
            bgColor = color;
            bgColor.a = 0.5f;
            font = f;
            label = l;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    public static class MyListItem extends GroupItemRenderer<ItemData> {
        private Rectangular mRect;
        private BmfTextObject mText;

        public MyListItem() {
            super();

            // default size
            setSize(100, 1);

            mRect = new Rectangular();
            //mRect.setSize(mSize.x, mSize.y);
            mRect.setColor(new GLColor(0x99FFFFFF));
            mRect.setTouchable(true); // for fun
            addChild(mRect);

            mText = new BmfTextObject();
            mText.setOriginAtCenter();
            mText.setRotation(90);
            addChild(mText);
        }

        @Override
        public void setSize(float w, float h) {
            super.setSize(w, h);

            if (mRect != null) {
                // match size with parent
                mRect.setSize(mSize.x, mSize.y);
            }
        }

        @Override
        public boolean setData(int index, ItemData data) {
            // diff check
            if (super.setData(index, data)) {
                HListActivity.ItemData itemData = (HListActivity.ItemData) data;
                mRect.setColor(itemData.bgColor);
                mText.setBitmapFont(itemData.font);
                mText.setText(itemData.label);
                mText.setPosition(mSize.x * 0.5f, mSize.y * 0.5f);

                return true;
            }

            return false;
        }
    }
}

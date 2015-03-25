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

import com.funzio.pure2D.BaseScene;
import com.funzio.pure2D.containers.DisplayGroup;
import com.funzio.pure2D.containers.ListItem;
import com.funzio.pure2D.containers.VList;
import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.gl.GLColor;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.shapes.Rectangular;
import com.funzio.pure2D.text.BitmapFont;
import com.funzio.pure2D.text.BmfTextObject;
import com.funzio.pure2D.text.Characters;
import com.funzio.pure2D.text.TextOptions;

import java.util.ArrayList;

public class VListActivity extends StageActivity {
    private static final String TAG = VListActivity.class.getSimpleName();
    private static final String FONT_PATH = "fonts/foo.ttf";

    private BitmapFont mBitmapFont;
    private Typeface mTypeface;

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
            data.add(new ItemData(mBitmapFont, "Item " + i));
        }

        VList<ItemData> vlist = new VList<ItemData>();
        vlist.setGap(10);
//        vlist.setSnapEnabled(true);
        vlist.setSwipeEnabled(true);
        vlist.setSize(mDisplaySize.x, mDisplaySize.y);
        vlist.setItemClass(MyListItem.class);
        vlist.setData(data);

        // add to scene
        mScene.addChild(vlist);
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

    public static class ItemData {
        public BitmapFont font;
        public String label;

        public ItemData(BitmapFont f, String l) {
            font = f;
            label = l;
        }
    }

    public static class MyListItem extends DisplayGroup implements ListItem {
        private Rectangular mRect;
        private BmfTextObject mText;

        public MyListItem() {
            super();

            mRect = new Rectangular();
            mRect.setSize(500, 100);
            mRect.setColor(new GLColor(0x99FFFFFF));
            addChild(mRect);

            mText = new BmfTextObject();
            addChild(mText);

            // match size
            setSize(mRect.getSize());
        }

        @Override
        public void setData(Object data) {
            VListActivity.ItemData itemData = (VListActivity.ItemData) data;
            mText.setBitmapFont(itemData.font);
            mText.setText(itemData.label);
            //(mRect.getWidth() - mText.getWidth()) * 0.5f
            mText.setPosition(30, (mRect.getHeight() - mText.getHeight()) * 0.5f);
        }

        @Override
        public Object getData() {
            // TODO
            return null;
        }
    }
}

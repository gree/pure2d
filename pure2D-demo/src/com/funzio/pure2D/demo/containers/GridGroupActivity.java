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
package com.funzio.pure2D.demo.containers;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Point;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.funzio.pure2D.BaseScene;
import com.funzio.pure2D.DisplayObject;
import com.funzio.pure2D.containers.GridGroup;
import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.grid.RectGrid;
import com.funzio.pure2D.shapes.Sprite;
import com.longo.pure2D.demo.R;

public class GridGroupActivity extends StageActivity {
    private static final int GRID_WIDTH = 5;
    private static final int GRID_HEIGHT = 5;
    private static final int GRID_CELL = 200;

    private List<Texture> mTextures = new ArrayList<Texture>();
    private RectGrid<DisplayObject> mRectGrid;
    private GridGroup<DisplayObject> mContainer;
    private PointF mTempPoint = new PointF();
    private Point mTempCell = new Point();

    // @Override
    // protected int getLayout() {
    // return R.layout.stage_orientation;
    // }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // to allow swiping
        mScene.setUIEnabled(true);
        mScene.setListener(new BaseScene.Listener() {

            @Override
            public void onSurfaceCreated(final GLState glState, final boolean firstTime) {
                if (firstTime) {
                    // load the textures
                    loadTextures();

                    // generate a lot of squares
                    addGroup(mRandom.nextInt(mDisplaySize.x), mRandom.nextInt(mDisplaySize.y));
                }
            }
        });
    }

    private void loadTextures() {
        final int[] ids = {
                R.drawable.cc_175, // cc
                R.drawable.mw_175, // mw
                R.drawable.ka_175, // ka
        };

        for (int id : ids) {
            // add texture to list
            mTextures.add(mScene.getTextureManager().createDrawableTexture(id, null));
        }
    }

    private void addGroup(final float x, final float y) {
        mRectGrid = new RectGrid<DisplayObject>(GRID_WIDTH, GRID_HEIGHT);
        mRectGrid.flipVertical(true); // flip the y-orientation
        mRectGrid.setCellSize(GRID_CELL, GRID_CELL);

        mContainer = new GridGroup<DisplayObject>(mRectGrid);

        // create children
        for (int row = 0; row < GRID_HEIGHT; row++) {
            for (int col = 0; col < GRID_WIDTH; col++) {
                // random texture
                final Texture texture = mTextures.get(row % mTextures.size());
                // create object
                final Sprite sprite = new Sprite();
                sprite.setTexture(texture);
                sprite.setOriginAtCenter();

                // add to container
                mContainer.addChildAt(sprite, col, row);
            }
        }
        // center on screen
        mContainer.setPosition(mDisplaySizeDiv2.x - mContainer.getWidth() / 2, mDisplaySizeDiv2.y - mContainer.getHeight() / 2);

        // add to scene
        mScene.addChild(mContainer);
    }

    @Override
    protected int getNumObjects() {
        return mScene.getNumGrandChildren();
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        final int action = event.getActionMasked();
        // final int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;

        // forward the event to scene
        mScene.onTouchEvent(event);

        if (action == MotionEvent.ACTION_DOWN) { // || action == MotionEvent.ACTION_POINTER_DOWN
            mContainer.globalToLocal(mScene.getTouchedPoint(), mTempPoint);
            if (mTempPoint.x > 0 && mTempPoint.y > 0) {
                mRectGrid.pointToCell(mTempPoint, mTempCell);

                final DisplayObject child = mContainer.getChildAt(mTempCell.x, mTempCell.y);
                if (child != null) {
                    child.setAlpha(1.25f - child.getAlpha());
                }
            }
        }

        return true;
    }

    public void onClickOrientation(final View view) {
    }
}

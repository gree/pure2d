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
package com.funzio.pure2D.demo.astar;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Point;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;

import com.funzio.pure2D.BaseScene;
import com.funzio.pure2D.DisplayObject;
import com.funzio.pure2D.animators.PathAnimator;
import com.funzio.pure2D.astar.Astar;
import com.funzio.pure2D.astar.AstarAdapter;
import com.funzio.pure2D.astar.AstarNode;
import com.funzio.pure2D.astar.AstarNodeSet;
import com.funzio.pure2D.containers.GridGroup;
import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.effects.trails.MotionTrailShape;
import com.funzio.pure2D.gl.GLColor;
import com.funzio.pure2D.gl.gl10.BlendModes;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.grid.HexGrid;
import com.funzio.pure2D.grid.VerticalHexGrid;
import com.funzio.pure2D.shapes.Sprite;
import com.longo.pure2D.demo.R;

public class AstarHexGridActivity extends StageActivity {
    private static final int GRID_CELL_RADIUS = 32;
    private int GRID_WIDTH;
    private int GRID_HEIGHT;

    private List<Texture> mTextures = new ArrayList<Texture>();
    private Texture mNeighborTexture1, mNeighborTexture2;
    private Sprite[] mNeighborSprites;
    private Point[] mNeighborCells;
    private VerticalHexGrid<DisplayObject> mHexGrid;
    private GridGroup<DisplayObject> mGridGroup;
    private PointF mTempPoint = new PointF();

    private DisplayObject mSelectedObject;

    private Astar mAstar = new Astar(new AstarAdapter() {
        @Override
        public int getNodeMaxNeighbors() {
            return HexGrid.CELL_MAX_NEIGHBORS;
        }

        @Override
        public void getNodeNeighbors(final AstarNode node, final AstarNodeSet openNodes, final AstarNodeSet closedNodes, final AstarNode[] neighbors) {
            final int[][] indices = mHexGrid.getNeighborOffets();
            final int start = (node.x % 2) * HexGrid.CELL_MAX_NEIGHBORS;
            int index = 0, x, y;
            // find empty neighbors
            for (int i = 0; i < HexGrid.CELL_MAX_NEIGHBORS; i++) {
                x = node.x + indices[start + i][0];
                y = node.y + indices[start + i][1];
                if (x >= 0 && x < GRID_WIDTH && y >= 0 && y < GRID_HEIGHT && mHexGrid.getDataAt(x, y) == null) {
                    neighbors[index++] = mAstar.createNode(x, y);
                }
            }
        }

        @Override
        public int getHeuristic(final AstarNode node1, final AstarNode node2) {
            return mHexGrid.getCellsDistance(node1.x, node1.y, node2.x, node2.y);
        }
    });

    private boolean mShowOffMode = true;
    private Runnable mShowOffRunnable = new Runnable() {

        @Override
        public void run() {
            showOff();
        }
    };

    @Override
    protected int getLayout() {
        return R.layout.stage_astar;
    };

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // define the grid dimensions based on the screen size
        GRID_WIDTH = (int) (mDisplaySize.x / (GRID_CELL_RADIUS * 1.5f + 0.5f));
        GRID_HEIGHT = (int) (mDisplaySize.y / (GRID_CELL_RADIUS * (float) Math.sqrt(3)) - 0.5f);
        mHexGrid = new VerticalHexGrid<DisplayObject>(GRID_WIDTH, GRID_HEIGHT, false);
        // mHexGrid.flipVertical(true); // flip the y-orientation
        mHexGrid.setCellSize(GRID_CELL_RADIUS);
        // set pool size for recycling nodes
        mAstar.setNodePoolSize(GRID_WIDTH * GRID_HEIGHT);

        // to allow touching
        mScene.setUIEnabled(true);
        mScene.setListener(new BaseScene.Listener() {

            @Override
            public void onSurfaceCreated(final GLState glState, final boolean firstTime) {
                if (firstTime) {
                    // load the textures
                    loadTextures();

                    // generate a lot of squares
                    createChildren(mRandom.nextInt(mDisplaySize.x), mRandom.nextInt(mDisplaySize.y));
                }
            }
        });
    }

    private void loadTextures() {
        final int[] ids = {
            // R.drawable.hex_red_64, //
            R.drawable.hex_white_64, //
        // R.drawable.hex_blue_64, //
        };

        for (int id : ids) {
            // add texture to list
            mTextures.add(mScene.getTextureManager().createDrawableTexture(id, null));
        }
        mNeighborTexture1 = mScene.getTextureManager().createDrawableTexture(R.drawable.hex_green_64, null);
        mNeighborTexture2 = mScene.getTextureManager().createDrawableTexture(R.drawable.hex_red_64, null);
    }

    private void createChildren(final float x, final float y) {
        mGridGroup = new GridGroup<DisplayObject>(mHexGrid);

        // create children
        for (int row = 0; row < GRID_HEIGHT; row++) {
            for (int col = 0; col < GRID_WIDTH; col++) {
                if (mRandom.nextInt(3) > 0) {
                    continue;
                }

                // if (col % 2 == 0) {
                // continue;
                // }

                // random texture
                final Texture texture = mTextures.get(row % mTextures.size());
                // create object
                final Sprite sprite = new Sprite();
                sprite.setTexture(texture);
                // sprite.setOriginAtCenter();
                sprite.setBlendFunc(BlendModes.PREMULTIPLIED_ALPHA_FUNC);
                mGridGroup.addChildAt(sprite, col, row);

                // motion trail
                final GLColor color1 = new GLColor(1, mRandom.nextFloat(), 0, 1f);
                final GLColor color2 = new GLColor(1, mRandom.nextFloat(), 0, 1f);
                final MotionTrailShape trail = new MotionTrailShape();
                trail.setMotionEasing(0.98f);
                // trail.setBlendFunc(BlendModes.ADD_FUNC);
                trail.setNumPoints(20);
                trail.setStrokeRange(GRID_CELL_RADIUS / 2f, GRID_CELL_RADIUS / 4f);
                trail.setStrokeColors(color1, color2);
                trail.setTargetOffset(sprite.getWidth() * 0.5f, sprite.getHeight() * 0.5f);
                trail.setTarget(sprite);
                mGridGroup.addChild(trail, 0);

            }
        }

        // create the neighbor sprites
        mNeighborSprites = new Sprite[HexGrid.CELL_MAX_NEIGHBORS];
        for (int i = 0; i < mNeighborSprites.length; i++) {
            final Sprite sprite = new Sprite();
            sprite.setVisible(false);
            // sprite.setAlpha(0.5f);
            sprite.setBlendFunc(BlendModes.ADD_FUNC);
            mGridGroup.addChild(sprite);
            mNeighborSprites[i] = sprite;
        }

        // center on screen
        mGridGroup.setPosition(mDisplaySizeDiv2.x - mGridGroup.getWidth() / 2, mDisplaySizeDiv2.y - mGridGroup.getHeight() / 2);

        // add to scene
        mScene.addChild(mGridGroup);
    }

    private void selectObjectAt(final Point cell) {
        if (mSelectedObject != null) {
            mSelectedObject.setColor(null);
        }

        // toggle selection
        final DisplayObject newObject = mHexGrid.getDataAt(cell.x, cell.y);
        if (newObject != null && newObject != mSelectedObject) {
            mSelectedObject = newObject;
            mSelectedObject.setColor(COLOR_YELLOW);
            showNeighborsAt(cell);
        } else {
            mSelectedObject = null;
            hideNeighbors();
        }
    }

    private void showNeighborsAt(final Point cell) {
        // init the array
        if (mNeighborCells == null) {
            mNeighborCells = new Point[HexGrid.CELL_MAX_NEIGHBORS];
            for (int i = 0; i < mNeighborCells.length; i++) {
                mNeighborCells[i] = new Point();
            }
        }

        // find empty neighbors
        final int num = mHexGrid.getNeighborsAt(cell, mNeighborCells);
        final PointF temp = new PointF();
        for (int i = 0; i < mNeighborCells.length; i++) {
            if (i < num) {
                mHexGrid.cellToPoint(mNeighborCells[i], temp);
                mNeighborSprites[i].setPosition(temp);
                mNeighborSprites[i].setVisible(true);
                if (mHexGrid.getDataAt(mNeighborCells[i].x, mNeighborCells[i].y) == null) {
                    mNeighborSprites[i].setTexture(mNeighborTexture1);
                } else {
                    mNeighborSprites[i].setTexture(mNeighborTexture2);
                }
            } else {
                // hide the out-of-bounds neighbors
                mNeighborSprites[i].setVisible(false);
            }
        }
    }

    private void hideNeighbors() {
        if (mNeighborCells != null) {
            for (int i = 0; i < mNeighborCells.length; i++) {
                mNeighborSprites[i].setVisible(false);
            }
        }
    }

    /**
     * @param selectedObject
     * @param tempCell
     */
    private boolean moveObject(final DisplayObject object, final Point dest) {
        // end the old animator
        PathAnimator animator = null;
        if (object.getNumManipulators() > 0) {
            // existing animator
            animator = (PathAnimator) object.getManipulator(0);
            // this object is running?
            if (animator.isRunning()) {
                return false;
            }
        }

        // long time = SystemClock.elapsedRealtime();
        final Point start = new Point();
        mHexGrid.pointToCell(object.getPosition().x + object.getSize().x * 0.5f, object.getPosition().y + object.getSize().y * 0.5f, start);
        if (mHexGrid.getDataAt(start.x, start.y) != object) {
            // FIXME this is because of the precision error in HexGrid.pointToCell()
            Log.e("long", "Precision Error!");
            return false;
        }

        final List<AstarNode> path = mAstar.findPath(mAstar.createNode(start), mAstar.createNode(dest), 0, false); // hex is not linear, no optimized path
        // Log.e("long", "Time taken: " + (SystemClock.elapsedRealtime() - time) + " ms");
        if (path != null) {

            // apply to the grid/group
            mGridGroup.swapChildren(start, dest, false);

            // convert grid points to pixel points
            final PointF[] points = mHexGrid.cellToPointPath(path, true); // compress here
            // optional: recycle nodes
            mAstar.recycleNodes(path);

            // end the old animator
            if (animator == null) {
                // new animator
                animator = new PathAnimator(null);
                object.addManipulator(animator);
            }
            // apply and play
            animator.setValues(points);
            animator.setDuration((int) animator.getTotalLength());
            animator.start();

            return true;
        }

        return false;
    }

    private void showOff() {

        for (int i = 0; i < 2; i++) {
            Point dest = null;
            DisplayObject obj = null;
            do {
                int cellX = mRandom.nextInt(GRID_WIDTH);
                int cellY = mRandom.nextInt(GRID_HEIGHT);

                if (mHexGrid.getDataAt(cellX, cellY) != null) {
                    if (obj == null) {
                        obj = mHexGrid.getDataAt(cellX, cellY);
                    }
                } else if (dest == null) {
                    dest = new Point(cellX, cellY);
                }

            } while (obj == null || dest == null);

            // move now
            moveObject(obj, dest);
        }

        // next
        if (mShowOffMode) {
            mHandler.postDelayed(mShowOffRunnable, 100);
        }
    }

    @Override
    protected int getNumObjects() {
        return mScene.getNumGrandChildren();
    }

    @Override
    public void finish() {
        super.finish();

        mHandler.removeCallbacks(mShowOffRunnable);
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        final int action = event.getActionMasked();

        // forward the event to scene
        mScene.onTouchEvent(event);

        if (action == MotionEvent.ACTION_DOWN) {
            mGridGroup.globalToLocal(mScene.getTouchedPoint(), mTempPoint);
            if (mTempPoint.x > 0 && mTempPoint.y > 0) {
                final Point temp = new Point();
                mHexGrid.pointToCell(mTempPoint, temp);

                final DisplayObject child = mHexGrid.getDataAt(temp.x, temp.y);
                if (child != null) {
                    selectObjectAt(temp);
                } else if (mSelectedObject != null) {
                    hideNeighbors();
                    moveObject(mSelectedObject, temp);
                }
            }
        }

        return true;
    }

    public void onClickShowOff(final View view) {
        final CheckBox cb = (CheckBox) view;
        mShowOffMode = cb.isChecked();
        Astar.LOG_ENABLED = !mShowOffMode; // turn off log in Show off mode

        mHandler.removeCallbacks(mShowOffRunnable);
        if (mShowOffMode) {
            showOff();
        }
    }

}

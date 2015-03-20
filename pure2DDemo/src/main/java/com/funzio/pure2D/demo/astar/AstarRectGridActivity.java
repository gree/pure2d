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
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.gl.gl10.textures.TextureOptions;
import com.funzio.pure2D.grid.RectGrid;
import com.funzio.pure2D.shapes.Sprite;
import com.longo.pure2D.demo.R;

public class AstarRectGridActivity extends StageActivity {
    private static final int GRID_CELL_SIZE = 64;
    private int GRID_WIDTH;
    private int GRID_HEIGHT;

    private List<Texture> mTextures = new ArrayList<Texture>();
    private RectGrid<DisplayObject> mRectGrid;
    private GridGroup<DisplayObject> mGridGroup;
    private PointF mTempPoint = new PointF();
    // private ObjectPool<ReusablePointF> mPointPool = new ObjectPool<ReusablePointF>(100);

    private DisplayObject mSelectedObject;

    private Astar mAstar = new Astar(new AstarAdapter() {
        @Override
        public int getNodeMaxNeighbors() {
            return 4;
        }

        @Override
        public void getNodeNeighbors(final AstarNode node, final AstarNodeSet openNodes, final AstarNodeSet closedNodes, final AstarNode[] neighbors) {
            int index = 0, x, y;

            // left
            x = node.x - 1;
            y = node.y;
            if (x >= 0 && !openNodes.containsXY(x, y) && !closedNodes.containsXY(x, y) && mRectGrid.getDataAt(x, y) == null) {
                neighbors[index++] = mAstar.createNode(x, y);
            }

            // right
            x = node.x + 1;
            y = node.y;
            if (x < GRID_WIDTH && !openNodes.containsXY(x, y) && !closedNodes.containsXY(x, y) && mRectGrid.getDataAt(x, y) == null) {
                neighbors[index++] = mAstar.createNode(x, y);
            }

            // top
            x = node.x;
            y = node.y - 1;
            if (y >= 0 && !openNodes.containsXY(x, y) && !closedNodes.containsXY(x, y) && mRectGrid.getDataAt(x, y) == null) {
                neighbors[index++] = mAstar.createNode(x, y);
            }

            // bottom
            x = node.x;
            y = node.y + 1;
            if (y < GRID_HEIGHT && !openNodes.containsXY(x, y) && !closedNodes.containsXY(x, y) && mRectGrid.getDataAt(x, y) == null) {
                neighbors[index++] = mAstar.createNode(x, y);
            }

        }

        @Override
        public int getHeuristic(final AstarNode node1, final AstarNode node2) {
            return Math.abs(node2.x - node1.x) + Math.abs(node2.y - node1.y);
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
        GRID_WIDTH = mDisplaySize.x / GRID_CELL_SIZE;
        GRID_HEIGHT = mDisplaySize.y / GRID_CELL_SIZE;
        mRectGrid = new RectGrid<DisplayObject>(GRID_WIDTH, GRID_HEIGHT);
        mRectGrid.flipVertical(true); // flip the y-orientation
        mRectGrid.setCellSize(GRID_CELL_SIZE, GRID_CELL_SIZE);
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
                R.drawable.cc_128, // cc
                R.drawable.mw_128, // mw
                R.drawable.ka_128, // ka
        };

        TextureOptions options = TextureOptions.getDefault();
        options.inMipmaps = 1;
        for (int id : ids) {
            // add texture to list
            mTextures.add(mScene.getTextureManager().createDrawableTexture(id, options));
        }
    }

    private void createChildren(final float x, final float y) {
        mGridGroup = new GridGroup<DisplayObject>(mRectGrid);

        // create children
        for (int row = 0; row < GRID_HEIGHT; row++) {
            for (int col = 0; col < GRID_WIDTH; col++) {
                if (mRandom.nextInt(4) > 0) {
                    continue;
                }

                // random texture
                final Texture texture = mTextures.get(row % mTextures.size());
                // create object
                final Sprite sprite = new Sprite();
                sprite.setTexture(texture);
                sprite.setOriginAtCenter();
                sprite.setScale(0.5f);
                mGridGroup.addChildAt(sprite, col, row);

                // motion trail
                final GLColor color1 = new GLColor(1, mRandom.nextFloat(), 0, 1f);
                final GLColor color2 = new GLColor(1, mRandom.nextFloat(), 0, 1f);
                final MotionTrailShape trail = new MotionTrailShape();
                trail.setMotionEasing(0.98f);
                trail.setNumPoints(20);
                trail.setStrokeRange(GRID_CELL_SIZE / 4f, GRID_CELL_SIZE / 6f);
                trail.setStrokeColors(color1, color2);
                trail.setTarget(sprite);
                mGridGroup.addChild(trail, 0);

            }
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

        // toogle selection
        final DisplayObject newObject = mRectGrid.getDataAt(cell.x, cell.y);
        if (newObject != null && newObject != mSelectedObject) {
            mSelectedObject = newObject;
            mSelectedObject.setColor(COLOR_YELLOW);
        } else {
            mSelectedObject = null;
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
        mRectGrid.pointToCell(object.getPosition(), start);
        final List<AstarNode> path = mAstar.findPath(mAstar.createNode(start), mAstar.createNode(dest), 0, true); // also compress here since RectGrid is linear
        // Log.e("long", "Time taken: " + (SystemClock.elapsedRealtime() - time) + " ms");
        if (path != null) {
            // apply to the grid/group
            mGridGroup.swapChildren(start, dest, false);

            // convert grid points to pixel points
            final PointF[] points = mRectGrid.cellToPointPath(path, false); // already compressed above, so don't compress here
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

                if (mRectGrid.getDataAt(cellX, cellY) != null) {
                    if (obj == null) {
                        obj = mRectGrid.getDataAt(cellX, cellY);
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

    // private void recyclePoint(final List<ReusablePointF> points) {
    // if (points == null) {
    // return;
    // }
    //
    // int size = points.size();
    // for (int i = 0; i < size; i++) {
    // if (mPointPool.release(points.get(i))) {
    // // pool full
    // return;
    // }
    // }
    // }
    //
    // private ReusablePointF createPoint(final float x, final float y) {
    // ReusablePointF point = mPointPool.acquire();
    // if (point != null) {
    // point.reset(x, y);
    // } else {
    // point = new ReusablePointF(x, y);
    // }
    //
    // return point;
    // }

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
                mRectGrid.pointToCell(mTempPoint, temp);

                final DisplayObject child = mRectGrid.getDataAt(temp.x, temp.y);
                if (child != null) {
                    selectObjectAt(temp);
                } else if (mSelectedObject != null) {
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

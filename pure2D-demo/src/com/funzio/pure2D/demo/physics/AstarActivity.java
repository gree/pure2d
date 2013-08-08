package com.funzio.pure2D.demo.physics;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Point;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.SystemClock;
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
import com.funzio.pure2D.demo.R;
import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.effects.trails.MotionTrailShape;
import com.funzio.pure2D.gl.GLColor;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.gl.gl10.textures.TextureOptions;
import com.funzio.pure2D.grid.RectGrid;
import com.funzio.pure2D.shapes.Sprite;

public class AstarActivity extends StageActivity {
    private static final int GRID_CELL = 64;
    private int GRID_WIDTH;
    private int GRID_HEIGHT;

    private List<Texture> mTextures = new ArrayList<Texture>();
    private RectGrid<DisplayObject> mRectGrid;
    private GridGroup<DisplayObject> mContainer;
    private PointF mTempPoint = new PointF();
    private Point mTempCell = new Point();

    private DisplayObject mSelectedObject;
    private Point mSelectedCell = new Point();

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
                neighbors[index++] = new AstarNode(x, y);
            }

            // right
            x = node.x + 1;
            y = node.y;
            if (x < GRID_WIDTH && !openNodes.containsXY(x, y) && !closedNodes.containsXY(x, y) && mRectGrid.getDataAt(x, y) == null) {
                neighbors[index++] = new AstarNode(x, y);
            }

            // top
            x = node.x;
            y = node.y - 1;
            if (y >= 0 && !openNodes.containsXY(x, y) && !closedNodes.containsXY(x, y) && mRectGrid.getDataAt(x, y) == null) {
                neighbors[index++] = new AstarNode(x, y);
            }

            // bottom
            x = node.x;
            y = node.y + 1;
            if (y < GRID_HEIGHT && !openNodes.containsXY(x, y) && !closedNodes.containsXY(x, y) && mRectGrid.getDataAt(x, y) == null) {
                neighbors[index++] = new AstarNode(x, y);
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
        GRID_WIDTH = mDisplaySize.x / GRID_CELL;
        GRID_HEIGHT = mDisplaySize.y / GRID_CELL;

        // to allow touching
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

    private void addGroup(final float x, final float y) {
        mRectGrid = new RectGrid<DisplayObject>(GRID_WIDTH, GRID_HEIGHT);
        mRectGrid.flipVertical(true); // flip the y-orientation
        mRectGrid.setCellSize(GRID_CELL, GRID_CELL);

        mContainer = new GridGroup<DisplayObject>(mRectGrid);

        // create children
        for (int row = 0; row < GRID_HEIGHT; row++) {
            for (int col = 0; col < GRID_WIDTH; col++) {
                if (mRandom.nextInt(3) > 0) {
                    continue;
                }

                // random texture
                final Texture texture = mTextures.get(row % mTextures.size());
                // create object
                final Sprite sprite = new Sprite();
                sprite.setTexture(texture);
                sprite.setOriginAtCenter();
                sprite.setScale(0.5f);

                // motion trail
                final GLColor color1 = new GLColor(mRandom.nextFloat(), mRandom.nextFloat(), mRandom.nextFloat(), 1f);
                final GLColor color2 = new GLColor(color1);
                color2.a = 0.5f;
                final MotionTrailShape trail = new MotionTrailShape();
                trail.setMotionEasing(0.95f);
                trail.setNumPoints(30);
                trail.setStrokeRange(GRID_CELL / 2f, GRID_CELL / 3f);
                trail.setStrokeColors(color1, color2);
                trail.setTarget(sprite);
                mContainer.addChild(trail, 0);

                // add to container
                mContainer.addChildAt(sprite, col, row);
            }
        }
        // center on screen
        mContainer.setPosition(mDisplaySizeDiv2.x - mContainer.getWidth() / 2, mDisplaySizeDiv2.y - mContainer.getHeight() / 2);

        // add to scene
        mScene.addChild(mContainer);
    }

    private void selectObjectAt(final Point cell) {
        if (mSelectedObject != null) {
            mSelectedObject.setColor(null);
        }

        mSelectedCell.set(cell.x, cell.y);
        mSelectedObject = mContainer.getChildAt(cell.x, cell.y);
        if (mSelectedObject != null) {
            mSelectedObject.setColor(COLOR_GREEN);
        }
    }

    /**
     * @param selectedObject
     * @param tempCell
     */
    private void moveObject(final DisplayObject object, final Point dest) {
        long time = SystemClock.elapsedRealtime();
        final List<AstarNode> path = mAstar.findPath(new AstarNode(mSelectedCell), new AstarNode(dest), 0, true);
        Log.e("long", "Time taken: " + (SystemClock.elapsedRealtime() - time) + " ms");
        if (path != null) {
            // convert grid points to pixel points
            final PointF[] points = new PointF[path.size()];
            for (int i = 0; i < points.length; i++) {
                Log.e("long", i + ": " + path.get(i));

                PointF point = new PointF();
                mRectGrid.cellToPoint(path.get(i), point);
                points[i] = point;
            }

            mScene.queueEvent(new Runnable() {

                @Override
                public void run() {
                    // clear the current pos
                    final AstarNode destCell = path.get(path.size() - 1);
                    mContainer.swapChildren(mSelectedCell, destCell);
                    mSelectedCell = destCell;

                    object.removeAllManipulators();
                    PathAnimator animator = new PathAnimator(null);
                    animator.setValues(points);
                    animator.setDuration((int) animator.getTotalLength());
                    animator.start();
                    object.addManipulator(animator);
                }
            });

        }

    }

    private void showOff() {
        Point dest = null;
        DisplayObject obj = null;
        do {
            int cellX = mRandom.nextInt(GRID_WIDTH);
            int cellY = mRandom.nextInt(GRID_HEIGHT);

            if (mContainer.getChildAt(cellX, cellY) != null) {
                if (obj == null) {
                    mSelectedCell.set(cellX, cellY);
                    obj = mContainer.getChildAt(cellX, cellY);
                }
            } else if (dest == null) {
                dest = new Point(cellX, cellY);
            }

        } while (obj == null || dest == null);

        // move now
        moveObject(obj, dest);

        // next
        if (mShowOffMode) {
            mHandler.postDelayed(mShowOffRunnable, 1000);
        }
    }

    @Override
    protected int getNumObjects() {
        return mScene.getNumGrandChildren();
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        final int action = event.getActionMasked();

        // forward the event to scene
        mScene.onTouchEvent(event);

        if (action == MotionEvent.ACTION_DOWN) {
            mContainer.globalToLocal(mScene.getTouchedPoint(), mTempPoint);
            if (mTempPoint.x > 0 && mTempPoint.y > 0) {
                mRectGrid.pointToCell(mTempPoint, mTempCell);

                final DisplayObject child = mContainer.getChildAt(mTempCell.x, mTempCell.y);
                if (child != null) {
                    selectObjectAt(mTempCell);
                } else if (mSelectedObject != null) {
                    moveObject(mSelectedObject, mTempCell);
                }
            }
        }

        return true;
    }

    public void onClickShowOff(final View view) {
        final CheckBox cb = (CheckBox) view;
        mShowOffMode = cb.isChecked();

        mHandler.removeCallbacks(mShowOffRunnable);
        if (mShowOffMode) {
            showOff();
        }
    }

}

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
    private static final int GRID_WIDTH = 20;
    private static final int GRID_HEIGHT = 20;
    private static final int GRID_CELL = 64;

    private List<Texture> mTextures = new ArrayList<Texture>();
    private RectGrid<DisplayObject> mRectGrid;
    private GridGroup<DisplayObject> mContainer;
    private PointF mTempPoint = new PointF();
    private Point mTempCell = new Point();

    private DisplayObject mSelectedObject;
    private Point mSelectedCell = new Point();
    private PathAnimator mPathAnimator = new PathAnimator(null);
    private MotionTrailShape mMotionTrail;

    private Astar mAstar = new Astar(new AstarAdapter() {

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
        public int getNodeMaxNeighbors() {
            return 4;
        }

        @Override
        public int getHeuristic(final Point point1, final Point point2) {
            return Math.abs(point2.x - point1.x) + Math.abs(point2.y - point1.y);
        }
    });

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

                // add to container
                mContainer.addChildAt(sprite, col, row);
            }
        }
        // center on screen
        mContainer.setPosition(mDisplaySizeDiv2.x - mContainer.getWidth() / 2, mDisplaySizeDiv2.y - mContainer.getHeight() / 2);

        // add to scene
        mScene.addChild(mContainer);

        // motion trail
        mMotionTrail = new MotionTrailShape();
        mMotionTrail.setMotionEasing(0.98f);
        mMotionTrail.setNumPoints(30);
        mMotionTrail.setStrokeRange(10, 1);
        mMotionTrail.setStrokeColors(new GLColor(1f, 0, 0, 1f), new GLColor(1f, 0, 0, 0.5f));
        mContainer.addChild(mMotionTrail);
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
        Log.e("long", "before: " + time);
        final List<AstarNode> path = mAstar.findPath(new AstarNode(mSelectedCell), new AstarNode(dest), 0, true);
        Log.e("long", "after: " + SystemClock.elapsedRealtime() + " " + (SystemClock.elapsedRealtime() - time));
        if (path != null) {
            // convert grid points to pixel points
            final PointF[] points = new PointF[path.size()];
            for (int i = 0; i < points.length; i++) {
                PointF point = new PointF();
                mRectGrid.cellToPoint(path.get(i), point);
                points[i] = point;
            }

            mScene.queueEvent(new Runnable() {

                @Override
                public void run() {
                    // clear the current pos
                    mContainer.setChildAt(null, mSelectedCell, false);
                    mSelectedCell = path.get(path.size() - 1);
                    // move to the new position
                    mContainer.setChildAt(mSelectedObject, mSelectedCell, true);

                    // end the other object
                    if (mPathAnimator.getTarget() != null) {
                        mPathAnimator.end();
                        ((DisplayObject) mPathAnimator.getTarget()).removeAllManipulators();
                    }
                    mPathAnimator.setValues(points);
                    mPathAnimator.setDuration((int) mPathAnimator.getTotalLength());
                    mPathAnimator.start();
                    mSelectedObject.addManipulator(mPathAnimator);
                    mMotionTrail.setTarget(mSelectedObject);
                }
            });

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

}

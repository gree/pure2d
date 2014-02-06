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
package com.funzio.pure2D.astar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

import android.graphics.Point;
import android.util.Log;

import com.funzio.pure2D.utils.ObjectPool;

/**
 * @author long.ngo
 */
public class Astar {

    public static boolean LOG_ENABLED = true;
    private static final String TAG = Astar.class.getSimpleName();

    protected AstarAdapter mAdapter;
    protected ObjectPool<AstarNode> mNodePool;
    protected boolean mClosedNodeRevision;

    public Astar(final AstarAdapter adapter) {
        this(adapter, 0, false);
    }

    public Astar(final AstarAdapter adapter, final int nodePoolSize, final boolean closedNodeRevision) {
        mAdapter = adapter;
        mNodePool = new ObjectPool<AstarNode>(nodePoolSize);
        mClosedNodeRevision = closedNodeRevision;
    }

    public void setNodePoolSize(final int nodePoolSize) {
        mNodePool.setMaxSize(nodePoolSize);
    }

    /**
     * Find a path between 2 nodes
     * 
     * @param start
     * @param end
     * @param maxCost
     * @param optimizePath
     * @return
     */
    public List<AstarNode> findPath(final AstarNode start, final AstarNode end, final int maxCost, final boolean compressPath) {
        return findPath(start, end, maxCost, compressPath, null, null);
    }

    /**
     * Find a path between 2 nodes
     * 
     * @param start
     * @param end
     * @param maxCost
     * @param closedNodes
     * @param openNodes
     * @return
     */
    public List<AstarNode> findPath(final AstarNode start, final AstarNode end, final int maxCost, final boolean compressPath, final List<AstarNode> closedNodes, final List<AstarNode> openNodes) {
        if (LOG_ENABLED) {
            Log.v(TAG, "findPath(): " + start + ", " + end);
        }

        final AstarNodeSet openSet = new AstarNodeSet(openNodes);
        final AstarNodeSet closedSet = new AstarNodeSet(closedNodes);
        final AstarNode[] neighbors = new AstarNode[mAdapter.getNodeMaxNeighbors()];
        AstarNode currentNode;

        final PriorityQueue<AstarNode> openQueue = new PriorityQueue<AstarNode>(1, AstarNode.COMPARATOR);
        start.h = mAdapter.getHeuristic(end, start);
        openQueue.add(start);
        if (openNodes != null) {
            openQueue.addAll(openNodes);
        }

        do {
            // get the lower cost node from the open list
            currentNode = openQueue.poll();

            // close it
            openSet.removeNode(currentNode);

            // close node? ignore it
            if (closedSet.containsSimilarNode(currentNode)) {
                continue;
            }

            // add to close list
            closedSet.addNode(currentNode);

            if (currentNode.equals(end)) {

                // awesome! path found!
                final List<AstarNode> path = extractPath(currentNode, compressPath);
                // recycle open and closed nodes
                recycleNodes(openSet);
                // but exclude nodes in the path
                recycleNodes(closedSet, path);
                return path;
            }

            // otherwise find the neighbors
            Arrays.fill(neighbors, null); // fill with null first
            mAdapter.getNodeNeighbors(currentNode, openSet, closedSet, neighbors);
            for (int i = 0; i < neighbors.length; i++) {
                final AstarNode neighbor = neighbors[i];
                if (neighbor == null) {
                    break;
                }

                // close node?
                // if (closeSet.get(newNode)) continue;

                // add the G
                neighbor.g += currentNode.g;
                neighbor.parent = currentNode;

                // check the cost
                if (maxCost > 0 && neighbor.g > maxCost) {
                    continue;
                }

                final AstarNode closedNode = closedSet.getSimilarNode(neighbor);
                // closed node? but is it shorter?
                if (closedNode != null) {
                    if (mClosedNodeRevision && neighbor.g < closedNode.g) {
                        // re-vision close node
                        closedSet.removeNode(closedNode);
                    } else {
                        continue;
                    }
                }

                // open node?
                final AstarNode openNode = openSet.getSimilarNode(neighbor);
                if (openNode != null) {
                    if (neighbor.g < openNode.g) {
                        openNode.g = neighbor.g;
                        openNode.parent = neighbor.parent;
                    }
                } else {
                    neighbor.h = mAdapter.getHeuristic(neighbor, end);

                    // add to open list
                    openQueue.add(neighbor);
                    openSet.addNode(neighbor);
                }
            }
        } while (openQueue.size() > 0);

        // recycle open and closed nodes
        recycleNodes(openSet);
        recycleNodes(closedSet);

        return null;
    }

    public AstarNodeSet findPosibleNodes(final AstarNode start, final AstarNode end, final int maxCost, final List<AstarNode> closedNodes, final List<AstarNode> openNodes) {
        if (LOG_ENABLED) {
            Log.v(TAG, "findRegion(): " + start + ", " + end);
        }

        final AstarNodeSet openSet = new AstarNodeSet(openNodes);
        final AstarNodeSet closedSet = new AstarNodeSet(closedNodes);
        final AstarNode[] neighbors = new AstarNode[mAdapter.getNodeMaxNeighbors()];
        final AstarNodeSet returnSet = new AstarNodeSet();
        AstarNode currentNode;

        final PriorityQueue<AstarNode> openQueue = new PriorityQueue<AstarNode>(1, AstarNode.COMPARATOR);
        start.h = mAdapter.getHeuristic(end, start);
        openQueue.add(start);
        if (openNodes != null) {
            openQueue.addAll(openNodes);
        }

        do {
            // get the lower cost node from the open list
            currentNode = openQueue.poll();

            // close it
            openSet.removeNode(currentNode);

            // close node? skip it
            if (closedSet.containsSimilarNode(currentNode)) {
                continue;
            }

            // add to close list
            closedSet.addNode(currentNode);

            // otherwise, loop to the neighbors
            Arrays.fill(neighbors, null); // fill with null first
            mAdapter.getNodeNeighbors(currentNode, openSet, closedSet, neighbors);
            for (int i = 0; i < neighbors.length; i++) {
                final AstarNode neighbor = neighbors[i];
                if (neighbor == null) {
                    break;
                }

                // close node?
                if (closedSet.containsSimilarNode(neighbor)) {
                    continue;
                }

                // add the G
                neighbor.g += currentNode.g;
                neighbor.parent = currentNode;

                // check the cost
                if (maxCost > 0 && neighbor.g > maxCost) {
                    continue;
                }

                // open node?
                final AstarNode openNode = openSet.getSimilarNode(neighbor);
                if (openNode != null) {
                    if (neighbor.g < openNode.g) {
                        openNode.g = neighbor.g;
                        openNode.parent = neighbor.parent;
                    }
                } else {
                    neighbor.h = mAdapter.getHeuristic(neighbor, end);

                    // add to open list
                    openQueue.add(neighbor);
                    openSet.addNode(neighbor);

                    // for return
                    returnSet.addNode(neighbor);
                }
            }
        } while (openQueue.size() > 0);

        // recycle open nodes only
        recycleNodes(openSet);

        return returnSet;
    }

    protected List<AstarNode> extractPath(final AstarNode fromNode, final boolean compression) {
        final ArrayList<AstarNode> path = new ArrayList<AstarNode>();
        AstarNode node = fromNode;

        if (compression) {
            // optimize the path
            AstarNode lastNode = null;
            int lastVectorX = 0, lastVectorY = 0;
            int newVectorX, newVectorY;
            do {
                if (lastNode == null) {
                    // first node
                    path.add(0, node);
                } else {
                    newVectorX = node.x - lastNode.x;
                    newVectorY = node.y - lastNode.y;
                    if ((lastVectorX == newVectorX && lastVectorY == newVectorY)) {
                        // override the node
                        path.set(0, node);
                    } else {
                        // add new node
                        path.add(0, node);
                    }

                    lastVectorX = newVectorX;
                    lastVectorY = newVectorY;
                }

                lastNode = node;
                // go to upper node
                node = node.parent;
            } while (node != null);
        } else {
            // non optimized path, with a lot of segments
            do {
                path.add(0, node);

                // upper node
                node = node.parent;
            } while (node != null);
        }

        if (LOG_ENABLED) {
            Log.v(TAG, "extractPath(): " + fromNode + " -> " + path.size() + " nodes");
        }

        return path;
    }

    /**
     * Check the pool first to either recycle or create a new node
     * 
     * @param x
     * @param y
     * @return
     */
    public AstarNode createNode(final int x, final int y) {
        AstarNode node = mNodePool.acquire();
        if (node != null) {
            node.reset(x, y);
        } else {
            node = new AstarNode(x, y);
        }
        return node;
    }

    /**
     * Check the pool first to either recycle or create a new node
     * 
     * @param p
     * @return
     */
    public AstarNode createNode(final Point p) {
        AstarNode node = mNodePool.acquire();
        if (node != null) {
            node.reset(p.x, p.y);
        } else {
            node = new AstarNode(p.x, p.y);
        }
        return node;
    }

    public void recycleNodes(final AstarNodeSet nodes) {
        recycleNodes(nodes, null);
    }

    public void recycleNodes(final AstarNodeSet nodes, final List<AstarNode> excludes) {
        if (nodes == null) {
            return;
        }

        // put into pool
        int i, size = nodes.size();
        AstarNode node;
        for (i = 0; i < size; i++) {
            node = nodes.get(nodes.keyAt(i));
            if (excludes == null || !excludes.contains(node)) {
                if (!mNodePool.release(node)) {
                    // pool full
                    break;
                }
            }
        }

        if (LOG_ENABLED) {
            Log.v(TAG, "recycleNodes(): " + i + " / " + size + ", available: " + mNodePool.size());
        }

        // clear the source also
        nodes.clear();
    }

    public void recycleNodes(final List<AstarNode> nodes) {
        if (nodes == null) {
            return;
        }

        // put into pool
        int i, size = nodes.size();
        for (i = 0; i < size; i++) {
            if (!mNodePool.release(nodes.get(i))) {
                // pool full
                break;
            }
        }

        if (LOG_ENABLED) {
            Log.v(TAG, "recycleNodes(): " + i + " / " + size + ", available: " + mNodePool.size());
        }

        // clear the source also
        nodes.clear();
    }

}

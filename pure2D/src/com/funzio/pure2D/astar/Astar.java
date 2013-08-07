package com.funzio.pure2D.astar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

import android.graphics.Point;
import android.util.Log;

/**
 * @author long.ngo
 */
public class Astar {

    private static final String TAG = Astar.class.getSimpleName();

    protected AstarAdapter mAdapter;
    protected boolean mClosedNodeRevision;

    public Astar(final AstarAdapter adapter) {
        this(adapter, false);
    }

    public Astar(final AstarAdapter adapter, final boolean closedNodeRevision) {
        mAdapter = adapter;
        mClosedNodeRevision = closedNodeRevision;
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
    public List<AstarNode> findPath(final AstarNode start, final AstarNode end, final int maxCost, final boolean optimizePath) {
        return findPath(start, end, maxCost, optimizePath, null, null);
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
        Log.v(TAG, "findPath(): " + start + ", " + end);

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
                return extractPath(currentNode, compressPath);
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

        return null;
    }

    public AstarNodeSet findPosibleNodes(final AstarNode start, final AstarNode end, final int maxCost, final List<AstarNode> closedNodes, final List<AstarNode> openNodes) {
        Log.v(TAG, "findRegion(): " + start + ", " + end);

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

        return returnSet;
    }

    protected List<AstarNode> extractPath(final AstarNode fromNode, final boolean compressPath) {
        Log.v(TAG, "extractPath(): " + fromNode + ", " + fromNode.parent);
        final ArrayList<AstarNode> path = new ArrayList<AstarNode>();
        AstarNode node = fromNode;

        if (compressPath) {
            // optimize the path
            Point lastPoint = null, newPoint;
            Point lastAngle = null, newAngle;
            do {
                newPoint = node;
                newAngle = (lastPoint != null) ? new Point(newPoint.x - lastPoint.x, newPoint.y - lastPoint.y) : null;
                // same angle?
                if (lastAngle != null && newAngle.equals(lastAngle)) {
                    // override the node
                    path.set(0, node);
                } else {
                    // angle changes
                    path.add(0, node);
                }

                lastPoint = newPoint;
                lastAngle = newAngle;

                node = node.parent;
            } while (node != null);
        } else {
            // non optimized path, with a lot of segments
            do {
                path.add(0, node);

                node = node.parent;
            } while (node != null);
        }

        return path;
    }

}

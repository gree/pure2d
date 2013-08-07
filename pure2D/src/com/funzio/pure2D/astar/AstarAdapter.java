/**
 * 
 */
package com.funzio.pure2D.astar;

import android.graphics.Point;

/**
 * @author long.ngo
 */
public interface AstarAdapter {
    public int getNodeMaxNeighbors();

    public void getNodeNeighbors(final AstarNode node, AstarNodeSet openNodes, AstarNodeSet closedNodes, final AstarNode[] neighbors);

    public int getHeuristic(final Point point1, final Point point2);

}

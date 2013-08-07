/**
 * 
 */
package com.funzio.pure2D.astar;


/**
 * @author long.ngo
 */
public interface AstarAdapter {
    public int getNodeMaxNeighbors();

    public void getNodeNeighbors(final AstarNode node, final AstarNodeSet openNodes, final AstarNodeSet closedNodes, final AstarNode[] neighbors);

    public int getHeuristic(final AstarNode node1, final AstarNode node2);

}

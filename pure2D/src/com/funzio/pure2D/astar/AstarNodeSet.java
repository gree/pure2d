/**
 * 
 */
package com.funzio.pure2D.astar;

import java.util.List;

import android.util.SparseArray;

/**
 * @author long.ngo
 */
public class AstarNodeSet extends SparseArray<AstarNode> {

    public AstarNodeSet() {
        super();
    }

    public AstarNodeSet(final List<AstarNode> nodes) {
        if (nodes != null) {
            for (AstarNode node : nodes) {
                addNode(node);
            }
        }
    }

    public void addNode(final AstarNode node) {
        // put(node.key, node);
        append(node.key, node);
    }

    public void removeNode(final AstarNode node) {
        remove(node.key);
    }

    public boolean containsSimilarNode(final AstarNode node) {
        return this.indexOfKey(node.key) >= 0;
    }

    public boolean containsXY(final int x, final int y) {
        return this.indexOfKey(AstarNode.generateKey(x, y)) >= 0;
    }

    public AstarNode getSimilarNode(final AstarNode node) {
        return get(node.key);
    }
}

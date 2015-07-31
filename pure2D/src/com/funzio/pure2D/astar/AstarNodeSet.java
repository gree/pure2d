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
        // put(node.getKey(), node);
        append(node.getKey(), node);
    }

    public void removeNode(final AstarNode node) {
        remove(node.getKey());
    }

    public boolean containsSimilarNode(final AstarNode node) {
        return this.indexOfKey(node.getKey()) >= 0;
    }

    public boolean containsXY(final int x, final int y) {
        return this.indexOfKey(AstarNode.generateKey(x, y)) >= 0;
    }

    public AstarNode getSimilarNode(final AstarNode node) {
        return get(node.getKey());
    }
}

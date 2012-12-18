/**
 * 
 */
package com.funzio.pure2D.atlas;

import android.graphics.RectF;

/**
 * @author long
 */
public class GridAtlas extends Atlas {
    public GridAtlas(final float width, final float height, final int col, final int row) {
        super(width, height);

        float dx = width / col;
        float dy = height / row;

        int index = 0;
        for (int r = 0; r < row; r++) {
            for (int c = 0; c < col; c++) {
                final AtlasFrame frame = new AtlasFrame(this, index, String.valueOf(index), new RectF(c * dx, r * dy, (c + 1) * dx - 1, (r + 1) * dy - 1));
                addFrame(frame);

                index++;
            }
        }
    }
}

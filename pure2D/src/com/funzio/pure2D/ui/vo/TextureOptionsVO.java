/**
 * 
 */
package com.funzio.pure2D.ui.vo;

import javax.microedition.khronos.opengles.GL10;

import org.json.JSONObject;

/**
 * @author long.ngo
 */
public class TextureOptionsVO {

    private static final String NEAREST = "nearest";
    private static final String LINEAR = "linear";

    public int filter;

    public TextureOptionsVO(final JSONObject json) {

        final String filterSt = json.optString("filter");
        if (NEAREST.equals(filterSt)) {
            filter = GL10.GL_NEAREST;
        } else if (LINEAR.equals(filterSt)) {
            filter = GL10.GL_LINEAR;
        } else {
            filter = 0;
        }
    }
}

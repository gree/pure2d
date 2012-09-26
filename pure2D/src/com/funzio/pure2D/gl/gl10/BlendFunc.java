/**
 * 
 */
package com.funzio.pure2D.gl.gl10;

import javax.microedition.khronos.opengles.GL10;

/**
 * @author long
 */
public class BlendFunc {
    public final static int DEFAULT_SRC = GL10.GL_SRC_ALPHA;
    public final static int DEFAULT_DST = GL10.GL_ONE_MINUS_SRC_ALPHA;

    public int src = DEFAULT_SRC;
    public int dst = DEFAULT_DST;

    public BlendFunc() {
        // nothing
    }

    public BlendFunc(final int src, final int dst) {
        this.src = src;
        this.dst = dst;
    }

    public void reset() {
        src = DEFAULT_SRC;
        dst = DEFAULT_DST;
    }

    public void setValues(final int src, final int dst) {
        this.src = src;
        this.dst = dst;
    }

    public boolean isSet() {
        return src != DEFAULT_SRC || dst != DEFAULT_DST;
    }

    public boolean equals(final BlendFunc func) {
        return src == func.src && dst == func.dst;
    }

    /**
     * Addictive blending
     * 
     * @return
     */
    public static BlendFunc getAdd() {
        // (src * 1) + (dst * 1) = src + dst
        return new BlendFunc(GL10.GL_ONE, GL10.GL_ONE);
    }

    /**
     * Multiplicative blending
     * 
     * @return
     */
    public static BlendFunc getMultiply() {
        // (src * dst) + (dst * 0) = src * dst
        return new BlendFunc(GL10.GL_DST_COLOR, GL10.GL_ZERO);
    }

    /**
     * Interpolative blending
     * 
     * @return
     */
    public static BlendFunc getInterpolate() {
        // (src * src_alpha) + (dst * (1 - src_apha)) = src * src_alpha + dst - (dst * src_alpha)
        return new BlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
    }

    /**
     * Screen blending
     * 
     * @return
     */
    public static BlendFunc getScreen() {
        // (src * 1) + (dst * (1 - src)) = src + dst - dst * src = dst + src * (1 - dst)
        return new BlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_COLOR);
    }
}

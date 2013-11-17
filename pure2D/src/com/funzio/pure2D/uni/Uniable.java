/**
 * 
 */
package com.funzio.pure2D.uni;

import com.funzio.pure2D.Manipulatable;

/**
 * @author long.ngo
 */
public interface Uniable extends Manipulatable {
    public float[] getVertices();

    public float[] getTextureCoords();

    // public GLColor[] getColors();
}

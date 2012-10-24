/**
 * 
 */
package com.funzio.pure2D.effects.sparks;

import com.funzio.pure2D.DisplayObject;
import com.funzio.pure2D.gl.GLColor;

/**
 * @author long
 */
public interface Sparkable extends DisplayObject {
    public void setColors(GLColor... colors);
}

/**
 * 
 */
package com.funzio.pure2D;

import org.xmlpull.v1.XmlPullParser;

import com.funzio.pure2D.containers.Container;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.ui.UIManager;

/**
 * @author long
 */
public interface DisplayObject extends Displayable {
    public static final int FLIP_X = 1;
    public static final int FLIP_Y = 1 << 1;

    public boolean draw(final GLState glState);

    public boolean isPerspectiveEnabled();

    public void setPerspectiveEnabled(final boolean perspectiveEnabled);

    public Container getParent();

    /**
     * @hide For internal use
     */
    public void setXMLAttributes(XmlPullParser xmlParser, UIManager manager);

    /**
     * @hide For internal use
     */
    public void onAdded(Container container);

    /**
     * @hide For internal use
     */
    public void onCreateChildren(final UIManager manager);
}

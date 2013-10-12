/**
 * 
 */
package com.funzio.pure2D.ui.vo;

import org.xmlpull.v1.XmlPullParser;

import com.funzio.pure2D.ui.UIConstraint;

/**
 * @author long.ngo
 */
public class DisplayObjectVO {

    protected UIConstraint mUIConstraint;

    public DisplayObjectVO(final XmlPullParser parser) {
        mUIConstraint = new UIConstraint();
        // TODO
    }

    public UIConstraint getUIConstraint() {
        return mUIConstraint;
    }

}

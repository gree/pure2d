/**
 * 
 */
package com.funzio.pure2D.ui;

import android.graphics.PointF;

import org.xmlpull.v1.XmlPullParser;

import com.funzio.pure2D.containers.DisplayGroup;

/**
 * @author long.ngo
 */
public class NovaContainer extends DisplayGroup {

    protected static final String ATT_SOURCE = "source";
    protected static final String ATT_ASYNC = "async";

    public NovaContainer() {
        super();
    }

    @Override
    public void setXMLAttributes(final XmlPullParser xmlParser, final UIManager manager) {
        super.setXMLAttributes(xmlParser, manager);

        final String source = xmlParser.getAttributeValue(null, ATT_SOURCE);
        if (source != null && source.endsWith(UIConfig.FILE_JSON)) {
            // final String async = xmlParser.getAttributeValue(null, ATT_ASYNC);
            manager.getNovaManager().addEmittersTo(this, manager.getPathFromUri(source), new PointF());
        }
    }

}

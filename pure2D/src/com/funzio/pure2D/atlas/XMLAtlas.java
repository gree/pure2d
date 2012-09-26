/**
 * 
 */
package com.funzio.pure2D.atlas;

import android.content.res.XmlResourceParser;

/**
 * @author long
 */
public abstract class XMLAtlas extends Atlas {

    abstract protected void parseXML(String xml);

    abstract protected void parseXML(XmlResourceParser parser);

    public XMLAtlas(final String xml) {
        parseXML(xml);
    }

    public XMLAtlas(final XmlResourceParser parser) {
        parseXML(parser);
    }
}

/**
 * ****************************************************************************
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
 * ****************************************************************************
 */
/**
 *
 */
package com.funzio.pure2D.ui;

import android.content.res.XmlResourceParser;
import android.util.Log;

import com.funzio.pure2D.DisplayObject;
import com.funzio.pure2D.Pure2DURI;
import com.funzio.pure2D.containers.Container;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;

/**
 * @author long.ngo
 */
public class UILoader {
    protected static final String TAG = UILoader.class.getSimpleName();

    private static final String INCLUDE = "include";
    private static final String MERGE = "merge";
    private static final String ATT_LAYOUT = "layout";

    private XmlPullParserFactory mFactory;

    private UIManager mUIManager;

    public UILoader(final UIManager manager) {
        mUIManager = manager;
    }

    public DisplayObject load(final String xmlString) throws Exception {
        Log.v(TAG, "load()");

        if (mFactory == null) {
            mFactory = XmlPullParserFactory.newInstance();
            mFactory.setNamespaceAware(false);
        }

        final XmlPullParser xpp = mFactory.newPullParser();
        xpp.setInput(new StringReader(xmlString));

        return load(xpp);
    }

    public DisplayObject load(final XmlPullParser parser) throws Exception {
        Log.v(TAG, "load(): " + parser);

        return loadMerge(parser, null);
    }

    /**
     * Load content of <merge> in a XML into a specified parent
     *
     * @param parser
     * @param parent
     * @return
     * @throws Exception
     */
    public DisplayObject loadMerge(final XmlPullParser parser, final Container parent) throws Exception {
        Log.v(TAG, "loadMerge(): " + parser + ", " + parent);

        int eventType = parser.next();
        if (eventType == XmlResourceParser.START_DOCUMENT) {
            parser.next();
        }

        return parseNode(parser, parent);
    }

    /**
     * Load a XML node
     *
     * @param parser
     * @param parent: this only works for xml starts with a <merge> or will be ignored
     * @return
     * @throws Exception
     */
    protected DisplayObject parseNode(final XmlPullParser parser, final Container parent) throws Exception {
        int eventType = -1;
        String nodeName = "";
        eventType = parser.getEventType();
        nodeName = parser.getName();
        // Log.v(TAG, "parseNode(): " + nodeName);

        DisplayObject displayObject = null;
        if (eventType == XmlResourceParser.START_TAG) {
            if (nodeName.equals(INCLUDE)) {
                // include other xml layout
                final String layout = parser.getAttributeValue(null, ATT_LAYOUT);
                if (layout != null) {
                    displayObject = mUIManager.load(mUIManager.getXMLByName(layout.substring(Pure2DURI.XML.length())));
                    if (displayObject != null) {
                        // you can also override the attributes
                        displayObject.setXMLAttributes(parser, mUIManager);
                    }
                }
            } else if (nodeName.equals(MERGE)) {
                displayObject = (DisplayObject) parent;
                if (displayObject != null) {
                    // you can also override the attributes
                    displayObject.setXMLAttributes(parser, mUIManager);
                }
            } else {
                // create by class name
                final Class<? extends DisplayObject> theClass = UIConfig.getClassByName(nodeName);
                if (theClass != null) {
                    displayObject = theClass.newInstance();
                    // set the attributes
                    displayObject.setXMLAttributes(parser, mUIManager);
                }
            }

            // find children, recursively
            do {
                eventType = parser.next();

                if (eventType == XmlResourceParser.START_TAG) {
                    if (displayObject instanceof Container) {
                        final DisplayObject child = parseNode(parser, null);
                        if (child != null) {
                            ((Container) displayObject).addChild(child);
                        }
                    }
                }


            } while (eventType != XmlResourceParser.END_TAG);

        } else {
            Log.e(TAG, "Invalid Event: " + eventType + ", " + nodeName);
        }

        if (displayObject != null) {
            // callback
            displayObject.onCreateChildren(mUIManager);
        }

        return displayObject;
    }
}

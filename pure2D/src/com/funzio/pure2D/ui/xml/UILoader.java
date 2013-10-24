/**
 * 
 */
package com.funzio.pure2D.ui.xml;

import java.io.StringReader;

import android.content.res.XmlResourceParser;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.funzio.pure2D.DisplayObject;
import com.funzio.pure2D.containers.Container;

/**
 * @author long.ngo
 */
public class UILoader {

    protected static final String TAG = UILoader.class.getSimpleName();

    private XmlPullParserFactory mFactory;

    public UILoader() {
    }

    public DisplayObject load(final String xmlString) {
        Log.v(TAG, "load()");

        try {
            if (mFactory == null) {
                mFactory = XmlPullParserFactory.newInstance();
            }

            final XmlPullParser xpp = mFactory.newPullParser();
            xpp.setInput(new StringReader(xmlString));

            return load(xpp);
        } catch (Exception e) {
            Log.e(TAG, "XML Parsing Error!", e);
        }

        return null;
    }

    public DisplayObject load(final XmlPullParser parser) {
        Log.v(TAG, "load(): " + parser);

        try {
            int eventType = parser.next();
            if (eventType == XmlResourceParser.START_DOCUMENT) {
                eventType = parser.next();
            }

            return parseNode(parser);
        } catch (Exception e) {
            Log.e(TAG, "XML Parsing Error!", e);
        }

        return null;
    }

    protected DisplayObject parseNode(final XmlPullParser parser) {
        int eventType = -1;
        String nodeName = "";
        try {
            eventType = parser.getEventType();
            nodeName = parser.getName();
            Log.v(TAG, "parseNode(): " + nodeName);
        } catch (Exception e) {
            Log.e(TAG, "XML Parsing Error!", e);
            return null;
        }

        DisplayObject displayObject = null;
        if (eventType == XmlResourceParser.START_TAG) {
            final Class<? extends DisplayObject> theClass = UIConfig.getClassByName(nodeName);

            if (theClass != null) {
                try {
                    displayObject = theClass.newInstance();
                    // set the attributes
                    displayObject.setXMLAttributes(parser);
                } catch (Exception e) {
                    Log.e(TAG, "Class Instantiating Error!", e);
                }

            }

            // find children, recursively
            do {
                try {
                    eventType = parser.next();

                    if (eventType == XmlResourceParser.START_TAG) {
                        if (displayObject instanceof Container) {
                            final DisplayObject child = parseNode(parser);
                            if (child != null) {
                                ((Container) displayObject).addChild(child);
                            }
                        }
                    }

                } catch (Exception e) {
                    Log.e(TAG, "XML Parsing Error!", e);
                }
            } while (eventType != XmlResourceParser.END_TAG);

        } else {
            Log.e(TAG, "Invalid Event: " + eventType + ", " + nodeName);
        }

        if (displayObject != null) {
            // callback
            displayObject.onXMLComplete(parser);
        }

        return displayObject;
    }
}

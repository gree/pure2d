/**
 * 
 */
package com.funzio.pure2D.ui.xml;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.funzio.pure2D.DisplayObject;
import com.funzio.pure2D.containers.DisplayGroup;
import com.funzio.pure2D.containers.HGroup;
import com.funzio.pure2D.containers.HList;
import com.funzio.pure2D.containers.HWheel;
import com.funzio.pure2D.containers.VGroup;
import com.funzio.pure2D.containers.VList;
import com.funzio.pure2D.containers.VWheel;
import com.funzio.pure2D.gl.gl10.textures.TextureOptions;
import com.funzio.pure2D.shapes.Clip;
import com.funzio.pure2D.shapes.Rectangular;
import com.funzio.pure2D.shapes.Sprite;
import com.funzio.pure2D.shapes.Sprite9;
import com.funzio.pure2D.text.BmfTextObject;
import com.funzio.pure2D.text.TextOptions;
import com.funzio.pure2D.ui.Button;
import com.funzio.pure2D.ui.UIManager;

/**
 * @author long.ngo
 */
public class UIConfig {
    private static final String TAG = UIConfig.class.getSimpleName();

    public static final String URI_STRING = "@string/";
    public static final String TYPE_STRING = "string";
    public static final String URI_DRAWABLE = "@drawable/";
    public static final String TYPE_DRAWABLE = "drawable";
    public static final String URI_ASSET = "asset://";
    public static final String URI_FILE = "file://";
    public static final String URI_HTTP = "http://";
    public static final String URI_CACHE = "cache://";

    private static final HashMap<String, Class<? extends DisplayObject>> CLASS_MAP = new HashMap<String, Class<? extends DisplayObject>>();
    static {
        CLASS_MAP.put("Group", DisplayGroup.class);
        CLASS_MAP.put("VGroup", VGroup.class);
        CLASS_MAP.put("HGroup", HGroup.class);
        CLASS_MAP.put("VWheel", VWheel.class);
        CLASS_MAP.put("HWheel", HWheel.class);
        CLASS_MAP.put("VList", VList.class);
        CLASS_MAP.put("HList", HList.class);
        CLASS_MAP.put("Rect", Rectangular.class);
        CLASS_MAP.put("Sprite", Sprite.class);
        CLASS_MAP.put("Sprite9", Sprite9.class);
        CLASS_MAP.put("Clip", Clip.class);
        CLASS_MAP.put("Button", Button.class);
        CLASS_MAP.put("Text", BmfTextObject.class);
    }

    private XmlPullParserFactory mFactory;
    private ArrayList<TextOptions> mFonts = new ArrayList<TextOptions>();

    // texture settings
    public boolean mTextureAsync = true;

    private UIManager mUIManager;

    @SuppressWarnings("unchecked")
    public static Class<? extends DisplayObject> getClassByName(final String name) {
        // Log.v(TAG, "getClassByName(): " + name);

        if (CLASS_MAP.containsKey(name)) {
            return CLASS_MAP.get(name);
        } else {

            try {
                Class<?> theClass = Class.forName(name);
                if (theClass.isAssignableFrom(DisplayObject.class)) {
                    return (Class<? extends DisplayObject>) theClass;
                } else {
                    Log.e(TAG, "Class is NOT a DisplayObject: " + name, new Exception());
                }
            } catch (ClassNotFoundException e) {
                Log.e(TAG, "Class NOT Found: " + name, e);
            }
        }

        return null;
    }

    public UIConfig(final UIManager manager) {
        mUIManager = manager;
    }

    public void reset(final Resources resource) {
        mFonts.clear();
    }

    public boolean load(final String xmlString) {
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

        return false;
    }

    public boolean load(final XmlPullParser parser) {
        Log.v(TAG, "load(): " + parser);

        try {
            int eventType = parser.next();
            String nodeName = "";

            if (eventType == XmlResourceParser.START_DOCUMENT) {
                eventType = parser.next();
            }

            do {
                if (eventType == XmlResourceParser.START_TAG) {
                    nodeName = parser.getName();

                    if (nodeName.equalsIgnoreCase("fonts")) {
                        parseFonts(parser);
                    }

                }

                // next
                eventType = parser.next();
            } while (eventType != XmlResourceParser.END_DOCUMENT);
        } catch (Exception e) {
            Log.e(TAG, "XML Parsing Error!", e);
        }

        return false;
    }

    private void parseFonts(final XmlPullParser parser) throws Exception {
        while (true) {
            int eventType = parser.next();
            String nodeName = parser.getName();

            if (eventType == XmlResourceParser.START_TAG) {

                if (nodeName.equalsIgnoreCase("Font")) {
                    final TextOptions options = TextOptions.getDefault();
                    options.setXMLAttributes(parser, mUIManager.getContext().getAssets());

                    // add to map
                    mFonts.add(options);
                }
            } else if (eventType == XmlResourceParser.END_TAG && nodeName.equalsIgnoreCase("fonts")) {
                break;
            }
        }
    }

    public List<TextOptions> getFonts() {
        return mFonts;
    }

    public TextureOptions getTextureOptions() {
        return null;
    }
}

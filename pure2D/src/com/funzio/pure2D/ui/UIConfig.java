/*******************************************************************************
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
 ******************************************************************************/
/**
 * 
 */
package com.funzio.pure2D.ui;

import java.util.HashMap;

import android.util.Log;

import com.funzio.pure2D.DisplayObject;
import com.funzio.pure2D.Pure2DURI;
import com.funzio.pure2D.containers.Alignment;
import com.funzio.pure2D.containers.DisplayGroup;
import com.funzio.pure2D.containers.HGroup;
import com.funzio.pure2D.containers.HScroll;
import com.funzio.pure2D.containers.HWheel;
import com.funzio.pure2D.containers.VGroup;
import com.funzio.pure2D.containers.VScroll;
import com.funzio.pure2D.containers.VWheel;
import com.funzio.pure2D.shapes.Clip;
import com.funzio.pure2D.shapes.Rectangular;
import com.funzio.pure2D.shapes.Sprite;
import com.funzio.pure2D.shapes.Sprite9;
import com.funzio.pure2D.text.BmfTextObject;

/**
 * @author long.ngo
 */
public class UIConfig {
    private static final String TAG = UIConfig.class.getSimpleName();

    public static final boolean DEFAULT_ASYNC = true;

    public static final String TYPE_STRING = "string";
    public static final String TYPE_DRAWABLE = "drawable";

    public static final String LAYER_SCENE = "$SCENE";
    public static final String LAYER_PARENT = "$PARENT";

    // variables
    public static final String $CDN_URL = "$CDN_URL";
    public static final String $CACHE_DIR = "$CACHE_DIR";

    public static final String FILE_JSON = ".json";
    public static final String FILE_PNG = ".png";

    private static final HashMap<String, Class<? extends DisplayObject>> CLASS_MAP = new HashMap<String, Class<? extends DisplayObject>>();
    static {
        CLASS_MAP.put("DisplayGroup", DisplayGroup.class);
        CLASS_MAP.put("Group", DisplayGroup.class);
        CLASS_MAP.put("VGroup", VGroup.class);
        CLASS_MAP.put("HGroup", HGroup.class);
        CLASS_MAP.put("VWheel", VWheel.class);
        CLASS_MAP.put("HWheel", HWheel.class);
        CLASS_MAP.put("VScroll", VScroll.class);
        CLASS_MAP.put("HScroll", HScroll.class);
        CLASS_MAP.put("Rect", Rectangular.class);
        CLASS_MAP.put("Sprite", Sprite.class);
        CLASS_MAP.put("Sprite9", Sprite9.class);
        CLASS_MAP.put("Clip", Clip.class);
        CLASS_MAP.put("Button", Button.class);
        CLASS_MAP.put("Text", BmfTextObject.class);
        CLASS_MAP.put("NovaGroup", NovaGroup.class);
    }

    @SuppressWarnings("unchecked")
    public static Class<? extends DisplayObject> getClassByName(final String name) {
        // Log.v(TAG, "getClassByName(): " + name);

        if (CLASS_MAP.containsKey(name)) {
            return CLASS_MAP.get(name);
        } else {

            try {
                Class<?> theClass = Class.forName(name);
                if (theClass == null) {
                    Log.e(TAG, "Class not found: " + name, new Exception());
                } else if (DisplayObject.class.isAssignableFrom(theClass)) {
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

    public static int getAlignment(final String align) {
        if ("top".equalsIgnoreCase(align)) {
            return Alignment.TOP;
        } else if ("bottom".equalsIgnoreCase(align)) {
            return Alignment.BOTTOM;
        } else if ("left".equalsIgnoreCase(align)) {
            return Alignment.LEFT;
        } else if ("right".equalsIgnoreCase(align)) {
            return Alignment.RIGHT;
        } else if ("hcenter".equalsIgnoreCase(align)) {
            return Alignment.HORIZONTAL_CENTER;
        } else if ("vcenter".equalsIgnoreCase(align)) {
            return Alignment.VERTICAL_CENTER;
        } else if ("center".equalsIgnoreCase(align)) {
            return Alignment.HORIZONTAL_CENTER | Alignment.VERTICAL_CENTER;
        }

        return Alignment.NONE;
    }

    public static boolean isUnknownUri(final String uri) {
        return !uri.startsWith(Pure2DURI.HTTP) //
                && !uri.startsWith(Pure2DURI.DRAWABLE) //
                && !uri.startsWith(Pure2DURI.ASSET) //
                && !uri.startsWith(Pure2DURI.FILE) //
                && !uri.startsWith(Pure2DURI.STRING) //
                && !uri.startsWith(Pure2DURI.CACHE) //
                && !uri.startsWith(Pure2DURI.XML);
    }
}

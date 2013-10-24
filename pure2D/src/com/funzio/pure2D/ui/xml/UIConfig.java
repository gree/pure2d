/**
 * 
 */
package com.funzio.pure2D.ui.xml;

import java.util.HashMap;

import android.util.Log;

import com.funzio.pure2D.DisplayObject;
import com.funzio.pure2D.containers.DisplayGroup;
import com.funzio.pure2D.containers.HGroup;
import com.funzio.pure2D.containers.HList;
import com.funzio.pure2D.containers.HWheel;
import com.funzio.pure2D.containers.VGroup;
import com.funzio.pure2D.containers.VList;
import com.funzio.pure2D.containers.VWheel;
import com.funzio.pure2D.shapes.Clip;
import com.funzio.pure2D.shapes.Sprite;
import com.funzio.pure2D.shapes.Sprite9;
import com.funzio.pure2D.ui.Button;

/**
 * @author long.ngo
 */
public class UIConfig {
    private static final String TAG = UIConfig.class.getSimpleName();

    private static final HashMap<String, Class<? extends DisplayObject>> CLASS_MAP = new HashMap<String, Class<? extends DisplayObject>>();
    static {
        CLASS_MAP.put("Group", DisplayGroup.class);
        CLASS_MAP.put("VGroup", VGroup.class);
        CLASS_MAP.put("HGroup", HGroup.class);
        CLASS_MAP.put("VWheel", VWheel.class);
        CLASS_MAP.put("HWheel", HWheel.class);
        CLASS_MAP.put("VList", VList.class);
        CLASS_MAP.put("HList", HList.class);
        CLASS_MAP.put("Sprite", Sprite.class);
        CLASS_MAP.put("Sprite9", Sprite9.class);
        CLASS_MAP.put("Clip", Clip.class);
        CLASS_MAP.put("Button", Button.class);
    }

    @SuppressWarnings("unchecked")
    public static Class<? extends DisplayObject> getClassByName(final String name) {
        Log.v(TAG, "getClassByName(): " + name);

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
}

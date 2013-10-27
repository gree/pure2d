/**
 * 
 */
package com.funzio.pure2D.ui;

import android.content.Context;
import android.content.res.Resources;
import android.os.Environment;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;

import com.funzio.pure2D.ui.xml.UIConfig;
import com.funzio.pure2D.ui.xml.UILoader;

/**
 * @author long.ngo
 */
public class UIManager {
    protected static final String TAG = UIManager.class.getSimpleName();

    // singleton
    private static UIManager sInstance;

    private Context mContext;
    private Resources mResources;
    private String mPackageName;
    private String mCacheDir;

    private UITextureManager mTextureManager;
    private UIConfig mConfig;
    private UILoader mLoader;

    private UIManager() {
        mConfig = new UIConfig(this);
        mLoader = new UILoader(this);
    }

    public static UIManager getInstance() {
        if (sInstance == null) {
            sInstance = new UIManager();
        }

        return sInstance;
    }

    public void setContext(final Context context) {
        Log.w(TAG, "setContext(): " + context);
        mContext = context;

        if (context != null) {
            mResources = context.getResources();
            mPackageName = context.getApplicationContext().getPackageName();
            mCacheDir = Environment.getExternalStorageDirectory() + "/Android/data/" + mPackageName + "/";
            mConfig.reset(mResources);
        } else {
            mConfig.reset(null);
            mTextureManager = null;
        }
    }

    public Context getContext() {
        return mContext;
    }

    public String getCacheDir() {
        return mCacheDir;
    }

    public boolean loadConfig(final XmlPullParser parser) {
        Log.v(TAG, "loadConfig()");

        final boolean success = mConfig.load(parser);

        // apply the config
        if (mTextureManager != null) {
            mTextureManager.loadBitmapFonts();
        }

        return success;
    }

    public UILoader getLoader() {
        return mLoader;
    }

    public UIConfig getConfig() {
        return mConfig;
    }

    public void setTextureManager(final UITextureManager textureManager) {
        Log.v(TAG, "setTextureManager(): " + textureManager);

        mTextureManager = textureManager;

        // apply the config
        if (mTextureManager != null) {
            mTextureManager.loadBitmapFonts();
        }
    }

    public UITextureManager getTextureManager() {
        return mTextureManager;
    }

    public String getStringValue(final XmlPullParser parser, final String name) {
        String value = parser.getAttributeValue(null, name);

        if (value != null) {
            if (value.startsWith(UIConfig.URI_STRING)) {
                String id = value.substring(UIConfig.URI_STRING.length());
                value = mResources.getString(mResources.getIdentifier(id, UIConfig.TYPE_STRING, mPackageName));
            }
        }

        return value;
    }

}

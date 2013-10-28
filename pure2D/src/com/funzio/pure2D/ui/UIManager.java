/**
 * 
 */
package com.funzio.pure2D.ui;

import android.content.Context;
import android.content.res.Resources;
import android.os.Environment;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;

import com.funzio.pure2D.loaders.tasks.ReadTextFileTask;
import com.funzio.pure2D.ui.vo.UIConfigVO;

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
    private UILoader mLoader;

    private UIConfigVO mConfigVO;

    private UIManager() {
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
        } else {
            mTextureManager = null;
        }
    }

    public Context getContext() {
        return mContext;
    }

    public String getCacheDir() {
        return mCacheDir;
    }

    /**
     * Load a config file, synchronously
     * 
     * @param assets
     * @param filePath
     */
    public void loadConfig(final String filePath) {
        Log.v(TAG, "load(): " + filePath);

        final ReadTextFileTask readTask = new ReadTextFileTask(mResources.getAssets(), filePath);
        if (readTask.run()) {
            Log.v(TAG, "Load success: " + filePath);

            try {
                mConfigVO = new UIConfigVO(new JSONObject(readTask.getContent()));
            } catch (JSONException e) {
                Log.e(TAG, "Load failed: " + filePath, e);
            }

        } else {
            Log.e(TAG, "Load failed: " + filePath);
        }
    }

    public UILoader getLoader() {
        return mLoader;
    }

    public UIConfigVO getConfig() {
        return mConfigVO;
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

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

import com.funzio.pure2D.loaders.tasks.ReadTextFileTask;
import com.funzio.pure2D.particles.nova.NovaManager;
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

    private UITextureManager mTextureManager;
    private NovaManager mNovaManager;
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
            mNovaManager = new NovaManager(mResources.getAssets());
        } else {
            mTextureManager = null;
        }
    }

    public Context getContext() {
        return mContext;
    }

    public void reset() {
        Log.w(TAG, "reset()");

        // release all textures
        if (mTextureManager != null) {
            mTextureManager.reset();
            mTextureManager = null;
        }

        // reset and clear pools
        mNovaManager.reset();
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
                if (mConfigVO.texture_manager.cache_dir == null || mConfigVO.texture_manager.cache_dir.length() == 0) {
                    // default cache dir
                    mConfigVO.texture_manager.cache_dir = Environment.getExternalStorageDirectory() + "/Android/data/" + mPackageName + "/";
                }
                // Log.e("long", mConfigVO.texture_manager.cache_dir);
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
        Log.i(TAG, "setTextureManager(): " + textureManager);

        mTextureManager = textureManager;

        // apply the config
        if (mTextureManager != null) {
            mTextureManager.setUIManager(this); // 2-way link
            mTextureManager.loadBitmapFonts();
            mNovaManager.setDelegator(mTextureManager.getNovaDelegator());
        }

    }

    public UITextureManager getTextureManager() {
        return mTextureManager;
    }

    public NovaManager getNovaManager() {
        return mNovaManager;
    }

    public String evalString(final String input) {
        String value = input;
        if (value != null) {
            if (value.startsWith(UIConfig.URI_STRING)) {
                // localized string
                String id = value.substring(UIConfig.URI_STRING.length());
                value = mResources.getString(mResources.getIdentifier(id, UIConfig.TYPE_STRING, mPackageName));
            } else {
                // process variables
                value = value.replace(UIConfig.$CACHE_DIR, mConfigVO.texture_manager.cache_dir);
                value = value.replace(UIConfig.$CDN_URL, mConfigVO.texture_manager.cdn_url);
            }
        }

        return value;
    }

    public String getPathFromUri(final String uri) {
        String actualPath = null;

        if (uri.startsWith(UIConfig.URI_DRAWABLE)) {
            actualPath = uri.substring(UIConfig.URI_DRAWABLE.length());
            final int drawable = mResources.getIdentifier(actualPath, UIConfig.TYPE_DRAWABLE, mPackageName);
            actualPath = String.valueOf(drawable);
        } else if (uri.startsWith(UIConfig.URI_ASSET)) {
            actualPath = uri.substring(UIConfig.URI_ASSET.length());
        } else if (uri.startsWith(UIConfig.URI_FILE)) {
            actualPath = uri.substring(UIConfig.URI_FILE.length());
        } else if (uri.startsWith(UIConfig.URI_HTTP)) {
            actualPath = uri; // keep
        } else if (uri.startsWith(UIConfig.URI_CACHE)) {
            final String shortPath = uri.substring(UIConfig.URI_CACHE.length());
            actualPath = mConfigVO.texture_manager.cache_dir + shortPath;
        } else {
            actualPath = uri;
        }

        return actualPath;
    }

}

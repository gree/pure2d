/**
 * 
 */
package com.funzio.pure2D.particles.nova;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.res.AssetManager;
import android.graphics.PointF;
import android.util.Log;

import com.funzio.pure2D.containers.Container;
import com.funzio.pure2D.particles.nova.vo.NovaVO;

/**
 * @author long
 */
public class NovaManager implements NovaLoader.Listener {

    private static final String TAG = NovaManager.class.getSimpleName();

    public static final String NOVA_DIR = "nova";

    private final AssetManager mAssets;
    private NovaLoader mLoader;

    private final Map<String, NovaFactory> mNovaFactories;
    private NovaDelegator mDelegator;
    private float mScale = 1;

    public NovaManager(final AssetManager assets) {
        mAssets = assets;
        mLoader = new NovaLoader(this);
        mNovaFactories = new HashMap<String, NovaFactory>();
    }

    public NovaDelegator getDelegator() {
        return mDelegator;
    }

    public void setDelegator(final NovaDelegator delegator) {
        mDelegator = delegator;
    }

    /**
     * Load a Json Nova file
     * 
     * @param filePath
     */
    private NovaFactory loadNova(final String filePath) {
        synchronized (mNovaFactories) {
            // check cache
            if (!mNovaFactories.containsKey(filePath)) {
                Log.i(TAG, "loadNova(): " + filePath);
                // load nova file synchronously
                mLoader.load(filePath.startsWith("/") ? null : mAssets, filePath);
            } else {
                Log.v(TAG, "loadNova(): " + filePath + " found in cache!");
            }

            return mNovaFactories.get(filePath);
        }
    }

    /**
     * Create the emitters to add to the scene
     * 
     * @param filePath
     * @param position
     * @return
     */
    protected List<NovaEmitter> createEmitters(final String filePath, final PointF position, final Object... params) {

        if (mNovaFactories.containsKey(filePath)) {
            return mNovaFactories.get(filePath).createEmitters(position, params);
        } else {
            // check and load
            final NovaFactory factory = loadNova(filePath);
            if (factory != null) {
                return factory.createEmitters(position, params);
            } else {
                Log.e(TAG, "NovaFactory not found! " + filePath);
                return null;
            }
        }
    }

    /**
     * Create and add the emitters to a specific container
     * 
     * @param container
     * @param filePath
     * @param x
     * @param y
     * @param params
     */
    public List<NovaEmitter> addEmittersTo(final Container container, final String filePath, final PointF position, final Object... params) {
        final List<NovaEmitter> emitters = createEmitters(filePath, position, params);
        // null check first
        if (emitters == null) {
            Log.e(TAG, "Emiters not created!", new Exception());
            return null;
        }

        // queue to add later
        final boolean success = container.queueEvent(new Runnable() {

            @Override
            public void run() {
                final int size = emitters.size();
                for (int i = 0; i < size; i++) {
                    container.addChild(emitters.get(i));
                }
            }
        });

        if (!success) {
            // no scene attached yet? add directly
            final int size = emitters.size();
            for (int i = 0; i < size; i++) {
                container.addChild(emitters.get(i));
            }
        }

        return emitters;
    }

    /**
     * Clear and reset everything for memory saving
     */
    public void reset() {
        Log.w(TAG, "reset()");

        clearPools();

        synchronized (mNovaFactories) {
            mNovaFactories.clear();
        }
    }

    /**
     * Clear the pools when memory needed.
     */
    public void clearPools() {
        Log.d(TAG, "clearPools()");

        synchronized (mNovaFactories) {
            // only clear the pools but keep everything else
            Set<String> keys = mNovaFactories.keySet();
            for (String key : keys) {
                mNovaFactories.get(key).clearPools();
            }
        }

    }

    public float getScale() {
        return mScale;
    }

    public void setScale(final float scale) {
        mScale = scale;
    }

    /**
     * @hide
     */
    @Override
    public void onLoad(final NovaLoader loader, final String filePath, final NovaVO novaVO) {
        synchronized (mNovaFactories) {
            // save some memory
            novaVO.releaseSource();

            // scale it to the right scale
            novaVO.applyScale(mScale);

            // cache it
            mNovaFactories.put(filePath, new NovaFactory(novaVO, mDelegator));
        }
    }

    /**
     * @hide
     */
    @Override
    public void onError(final NovaLoader loader, final String filePath) {
        Log.e(TAG, "Nova Loading Error! " + filePath);
    }

}

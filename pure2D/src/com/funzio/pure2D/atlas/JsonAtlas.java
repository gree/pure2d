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
package com.funzio.pure2D.atlas;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.res.AssetManager;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.funzio.pure2D.Scene;
import com.funzio.pure2D.loaders.AsyncTaskExecuter;
import com.funzio.pure2D.loaders.tasks.ReadTextFileTask;
import com.funzio.pure2D.loaders.tasks.RunnableTask;
import com.funzio.pure2D.loaders.tasks.Task;
import com.funzio.pure2D.loaders.tasks.URLLoadJsonTask;
import com.funzio.pure2D.loaders.tasks.URLLoadTextTask;
import com.funzio.pure2D.loaders.tasks.WriteTextFileTask;
import com.funzio.pure2D.particles.nova.NovaConfig;

/**
 * @author long
 * @category Loads and parses Json file exported by TexturePacker
 */
public class JsonAtlas extends Atlas {
    protected static final String TAG = JsonAtlas.class.getSimpleName();

    protected int mAxisSystem = Scene.AXIS_TOP_LEFT; // TOP-LEFT is TexturePacker's default

    public JsonAtlas() {
        super();
    }

    public JsonAtlas(final int axisSystem) {
        super();

        mAxisSystem = axisSystem;
    }

    protected void load(final InputStream stream, final float scale) throws IOException, JSONException {
        Log.v(TAG, "load()");

        final StringBuilder sb = new StringBuilder();
        while (stream.available() > 0) {
            final byte[] bytes = new byte[stream.available()];
            stream.read(bytes);
            sb.append(new String(bytes));
        }
        stream.close();

        parse(sb.toString(), scale);
    }

    /**
     * Load from assets
     * 
     * @param assets
     * @param filePath
     * @param scale
     * @throws IOException
     * @throws JSONException
     */
    public void load(final AssetManager assets, final String filePath, final float scale) throws IOException, JSONException {
        Log.v(TAG, "load(): " + filePath);

        if (assets == null) {
            load(new FileInputStream(new File(filePath)), scale);
        } else {
            load(assets.open(filePath), scale);
        }

        // listener
        if (mListener != null) {
            mListener.onAtlasLoad(JsonAtlas.this);
        }
    }

    /**
     * Load from file system
     * 
     * @param filePath
     * @param scale
     * @throws IOException
     * @throws JSONException
     */
    public void load(final String filePath, final float scale) throws IOException, JSONException {
        Log.v(TAG, "load(): " + filePath);

        load(new FileInputStream(new File(filePath)), scale);

        // listener
        if (mListener != null) {
            mListener.onAtlasLoad(JsonAtlas.this);
        }
    }

    public void loadAsync(final AssetManager assets, final String filePath, final float scale) {
        Log.v(TAG, "loadAsync(): " + filePath);

        final AsyncTaskExecuter<Task> executer = new AsyncTaskExecuter<Task>();
        // start loading
        executer.executeOnPool(new RunnableTask(new Runnable() {

            @Override
            public void run() {
                try {
                    load(assets, filePath, scale);

                } catch (IOException e) {
                    Log.e(TAG, "Load failed: " + filePath, e);
                } catch (JSONException e) {
                    Log.e(TAG, "Load JSON failed: " + filePath, e);
                }
            }
        }));
    }

    /**
     * Load from a url and cache as an option
     * 
     * @param urlPath
     * @param cachePath
     * @param scale
     * @return
     * @throws JSONException
     */
    public boolean loadURL(final String urlPath, final String cachePath, final float scale) throws JSONException {
        Log.v(TAG, "loadURL(): " + urlPath + ", " + cachePath);

        // read cache first
        if (cachePath != null && cachePath.length() > 0) {
            final ReadTextFileTask readTask = new ReadTextFileTask(cachePath);
            if (readTask.run()) {
                parse(readTask.getContent(), scale);

                // listener
                if (mListener != null) {
                    mListener.onAtlasLoad(JsonAtlas.this);
                }

                return true;
            }
        }

        // load from url
        final URLLoadTextTask urlTask = new URLLoadJsonTask(urlPath);
        if (urlTask.run()) {
            final String json = urlTask.getStringBuilder().toString();
            parse(json, scale);

            // listener
            if (mListener != null) {
                mListener.onAtlasLoad(JsonAtlas.this);
            }

            // cache it
            if (cachePath != null && cachePath.length() > 0) {
                final WriteTextFileTask fileTask = new WriteTextFileTask(json, cachePath, false);
                fileTask.run();
            }

            return true;
        }

        return false;
    }

    public void loadURLAsync(final String urlPath, final String cachePath, final float scale) {
        Log.v(TAG, "loadURLAsync(): " + urlPath + ", " + cachePath);

        final AsyncTaskExecuter<Task> executer = new AsyncTaskExecuter<Task>();
        // start loading
        executer.executeOnPool(new RunnableTask(new Runnable() {

            @Override
            public void run() {
                try {
                    loadURL(urlPath, cachePath, scale);
                } catch (JSONException e) {
                    Log.e(TAG, "Load JSON failed: " + urlPath, e);
                }
            }
        }));
    }

    protected void parse(final String json, final float scale) throws JSONException {

        synchronized (mMasterFrameSet) {
            removeAllFrames();

            final JSONObject jsonObject = new JSONObject(json);
            final JSONObject meta = jsonObject.getJSONObject("meta");
            // final float scale = (float) jsonObject.getDouble("scale");
            final JSONObject size = meta.getJSONObject("size");
            mWidth = size.getInt("w") * scale;
            mHeight = size.getInt("h") * scale;
            mImage = meta.getString("image");

            // Long Ngo added: framerate
            getMasterFrameSet().setFps(meta.optInt("fps"));
            String loopMode = meta.optString("loop_mode", null);
            if (loopMode != null) {
                getMasterFrameSet().setLoopMode(NovaConfig.getLoopMode(loopMode));
            }

            // create the master frame set
            final JSONArray frames = jsonObject.getJSONArray("frames");
            final int totalFrames = frames.length();
            for (int i = 0; i < totalFrames; i++) {
                addFrame(parseFrame(i, frames.getJSONObject(i), scale));
            }

            // Long Ngo added: check for sub frame sets
            final JSONObject frameSets = jsonObject.optJSONObject("frame_sets");
            if (frameSets != null) {
                final JSONArray names = frameSets.names();
                if (names != null) {
                    final int numSets = names.length();
                    for (int i = 0; i < numSets; i++) {
                        final String setName = names.getString(i);
                        final JSONObject set = frameSets.optJSONObject(setName);
                        // Log.e(TAG, "New set: " + setName);
                        if (set != null) {
                            final JSONArray subFrames = set.optJSONArray("frames");
                            if (subFrames != null) {
                                // create new subset
                                final AtlasFrameSet newSet = new AtlasFrameSet(setName);
                                // add the frames
                                final int numFrames = subFrames.length();
                                for (int j = 0; j < numFrames; j++) {
                                    final int frameIndex = subFrames.getInt(j);
                                    if (frameIndex < totalFrames) {
                                        // Log.e(TAG, j + ": " + getFrame(frameIndex));
                                        newSet.addFrame(getFrame(frameIndex));
                                    }
                                }

                                // check optional loop mode
                                loopMode = set.optString("loop_mode", null);
                                if (loopMode != null) {
                                    newSet.setLoopMode(NovaConfig.getLoopMode(loopMode));
                                }

                                // add to the subsets
                                addSubFrameSet(newSet);
                            }
                        }
                    }
                }
            }

            Log.v(TAG, "parse(): frames: " + totalFrames + ", subsets: " + getNumSubFrameSets());
        }
    }

    protected AtlasFrame parseFrame(final int index, final JSONObject frameJson, final float scale) throws JSONException {
        final JSONObject frame = frameJson.getJSONObject("frame");
        final boolean trimmed = frameJson.getBoolean("trimmed");
        final boolean rotated = frameJson.getBoolean("rotated");

        final int left = frame.getInt("x");
        final int top = frame.getInt("y");
        final int w = frame.getInt("w");
        final int h = frame.getInt("h");
        final int right = left + (rotated ? h : w);
        final int bottom = top + (rotated ? w : h);

        final AtlasFrame atlasFrame = new AtlasFrame(this, index, frameJson.getString("filename"), new RectF(left * scale, top * scale, right * scale, bottom * scale));
        if (trimmed) {

            final JSONObject spriteSourceSize = frameJson.getJSONObject("spriteSourceSize");
            final int offsetX = spriteSourceSize.getInt("x");
            int offsetY = spriteSourceSize.getInt("y");
            // check axis
            if (mAxisSystem == Scene.AXIS_BOTTOM_LEFT) {
                final JSONObject sourceSize = frameJson.getJSONObject("sourceSize");
                // flip for axis
                offsetY = sourceSize.getInt("h") - (spriteSourceSize.getInt("y") + spriteSourceSize.getInt("h"));
            }

            if (offsetX != 0 || offsetY != 0) {
                atlasFrame.mOffset = new PointF(offsetX * scale, offsetY * scale);
            }
        }
        if (rotated) {
            atlasFrame.rotateCCW();
        }

        return atlasFrame;
    }

}

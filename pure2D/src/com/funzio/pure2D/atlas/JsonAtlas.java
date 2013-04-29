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

import com.funzio.pure2D.loaders.AsyncTaskExecuter;
import com.funzio.pure2D.loaders.tasks.ReadTextFileTask;
import com.funzio.pure2D.loaders.tasks.Task;

/**
 * @author long
 * @category Loads and parses Json file exported by TexturePacker
 */
public class JsonAtlas extends Atlas {
    protected static final String TAG = JsonAtlas.class.getSimpleName();

    public JsonAtlas() {
        super();
    }

    public JsonAtlas(final InputStream stream, final float scale) throws IOException, JSONException {
        super();

        final StringBuilder sb = new StringBuilder();
        while (stream.available() > 0) {
            final byte[] bytes = new byte[stream.available()];
            stream.read(bytes);
            sb.append(new String(bytes));
        }
        stream.close();

        parse(sb.toString(), scale);
    }

    public JsonAtlas(final AssetManager asset, final String filePath, final float scale) throws IOException, JSONException {
        this(asset.open(filePath), scale);
    }

    public JsonAtlas(final String filePath, final float scale) throws IOException, JSONException {
        this(new FileInputStream(new File(filePath)), scale);
    }

    public void loadAsync(final AssetManager assets, final String filePath, final float scale) {
        Log.v(TAG, "loadAsync(): " + filePath);

        final ReadTextFileTask readTask = new ReadTextFileTask(assets, filePath);
        final AsyncTaskExecuter<Task> executer = new AsyncTaskExecuter<Task>();
        executer.setTaskListener(new Task.TaskListener() {

            @Override
            public void onTaskComplete(final Task task) {

                if (task.isSucceeded()) {
                    Log.v(TAG, "Load success: " + filePath);

                    try {
                        parse(((ReadTextFileTask) task).getContent(), scale);

                        if (mListener != null) {
                            mListener.onAtlasLoad(JsonAtlas.this);
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Load failed: " + filePath, e);
                    }

                } else {
                    Log.e(TAG, "Load failed: " + filePath);
                }

            }
        });

        // start loading
        executer.executeOnPool(readTask);
    }

    public void parse(final String json, final float scale) throws JSONException {
        removeAllFrames();

        final JSONObject jsonObject = new JSONObject(json);
        final JSONObject meta = jsonObject.getJSONObject("meta");
        // final float scale = (float) jsonObject.getDouble("scale");
        final JSONObject size = meta.getJSONObject("size");
        mWidth = size.getInt("w") * scale;
        mHeight = size.getInt("h") * scale;
        mImage = meta.getString("image");
        // framerate
        getMasterFrameSet().setFps(meta.optInt("fps"));

        // create the frames
        final JSONArray frames = jsonObject.getJSONArray("frames");
        final int length = frames.length();
        for (int i = 0; i < length; i++) {
            addFrame(parseFrame(i, frames.getJSONObject(i), scale));
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
        final int right = left + (rotated ? h : w) - 1;
        final int bottom = top + (rotated ? w : h) - 1;

        final AtlasFrame atlasFrame = new AtlasFrame(this, index, frameJson.getString("filename"), new RectF(left * scale, top * scale, right * scale, bottom * scale));
        if (trimmed) {
            final JSONObject spriteSourceSize = frameJson.getJSONObject("spriteSourceSize");
            final int offsetX = spriteSourceSize.getInt("x");
            final int offsetY = spriteSourceSize.getInt("y");
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

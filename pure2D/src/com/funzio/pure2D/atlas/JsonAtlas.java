/**
 * 
 */
package com.funzio.pure2D.atlas;

import java.io.IOException;
import java.io.InputStream;

import android.content.res.AssetManager;
import android.graphics.PointF;
import android.graphics.Rect;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author long
 * @category Loads and parses Json file exported by TexturePacker
 */
public class JsonAtlas extends Atlas {
    public JsonAtlas(final String json) throws JSONException {
        super();

        parseJson(json);
    }

    public JsonAtlas(final InputStream stream) throws IOException, JSONException {
        super();

        final StringBuilder sb = new StringBuilder();
        while (stream.available() > 0) {
            final byte[] bytes = new byte[stream.available()];
            stream.read(bytes);
            sb.append(new String(bytes));
        }
        stream.close();

        parseJson(sb.toString());
    }

    public JsonAtlas(final AssetManager asset, final String filePath) throws IOException, JSONException {
        this(asset.open(filePath));
    }

    protected void parseJson(final String json) throws JSONException {
        final JSONObject jsonObject = new JSONObject(json);
        final JSONObject meta = jsonObject.getJSONObject("meta");
        final JSONObject size = meta.getJSONObject("size");
        mWidth = size.getInt("w");
        mHeight = size.getInt("h");

        final JSONArray frames = jsonObject.getJSONArray("frames");
        final int length = frames.length();
        for (int i = 0; i < length; i++) {
            addFrame(parseFrame(i, frames.getJSONObject(i)));
        }

    }

    protected AtlasFrame parseFrame(final int index, final JSONObject frameJson) throws JSONException {
        final JSONObject frame = frameJson.getJSONObject("frame");
        final boolean trimmed = frameJson.getBoolean("trimmed");
        final boolean rotated = frameJson.getBoolean("rotated");

        int left = frame.getInt("x");
        int top = frame.getInt("y");
        int w = frame.getInt("w");
        int h = frame.getInt("h");
        int right = left + (rotated ? h : w) - 1;
        int bottom = top + (rotated ? w : h) - 1;

        final AtlasFrame atlasFrame = new AtlasFrame(this, index, frameJson.getString("filename"), new Rect(left, top, right, bottom));
        if (trimmed) {
            final JSONObject spriteSourceSize = frameJson.getJSONObject("spriteSourceSize");
            final int offsetX = spriteSourceSize.getInt("x");
            final int offsetY = spriteSourceSize.getInt("y");
            if (offsetX != 0 || offsetY != 0) {
                atlasFrame.mOffset = new PointF(offsetX, offsetY);
            }
        }
        if (rotated) {
            atlasFrame.rotateCCW();
        }

        return atlasFrame;
    }

}

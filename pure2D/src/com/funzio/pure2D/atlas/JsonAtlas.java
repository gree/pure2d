/**
 * 
 */
package com.funzio.pure2D.atlas;

import java.io.IOException;
import java.io.InputStream;

import android.content.res.AssetManager;
import android.graphics.PointF;
import android.graphics.RectF;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author long
 * @category Loads and parses Json file exported by TexturePacker
 */
public class JsonAtlas extends Atlas {
    public JsonAtlas(final String json, final float scale) throws JSONException {
        super();

        parseJson(json, scale);
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

        parseJson(sb.toString(), scale);
    }

    public JsonAtlas(final AssetManager asset, final String filePath, final float scale) throws IOException, JSONException {
        this(asset.open(filePath), scale);
    }

    protected void parseJson(final String json, final float scale) throws JSONException {
        final JSONObject jsonObject = new JSONObject(json);
        final JSONObject meta = jsonObject.getJSONObject("meta");
        // final float scale = (float) jsonObject.getDouble("scale");
        final JSONObject size = meta.getJSONObject("size");
        mWidth = size.getInt("w");
        mHeight = size.getInt("h");
        mImage = meta.getString("image");

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

        final float left = frame.getInt("x") * scale;
        final float top = frame.getInt("y") * scale;
        final float w = frame.getInt("w");
        final float h = frame.getInt("h");
        final float right = left + ((rotated ? h : w) - 1) * scale;
        final float bottom = top + ((rotated ? w : h) - 1) * scale;

        final AtlasFrame atlasFrame = new AtlasFrame(this, index, frameJson.getString("filename"), new RectF(left, top, right, bottom));
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

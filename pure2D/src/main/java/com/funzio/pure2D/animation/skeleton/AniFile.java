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
 * AniFile can be use to load Funzio's .ani file. This can be used by the AniSkeleton to render.
 * @see AniSkeleton
 */
package com.funzio.pure2D.animation.skeleton;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.microedition.khronos.opengles.GL10;

import android.content.res.AssetManager;
import android.content.res.XmlResourceParser;
import android.graphics.RectF;
import android.util.Base64;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.funzio.pure2D.DisplayObject;
import com.funzio.pure2D.gl.gl10.VertexBuffer;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.gl.gl10.textures.TextureCoordBuffer;

/**
 * @author long
 */
public class AniFile {
    private static final String TAG = AniFile.class.getSimpleName();

    public static final int TEXTURE_SIZE = 512;
    public static final int FLOAT = 4;
    public static final int HEADER = 12;

    public static final int PART_POINTS = 4;
    public static final int PART_VERTICES = 3 * PART_POINTS; // xyz
    public static final int PART_VERTICES_SIZE = PART_VERTICES * FLOAT;
    public static final int PART_COORDS = 2 * PART_POINTS; // xy
    public static final int PART_COORDS_SIZE = PART_COORDS * FLOAT;
    public static final int PART_TOTAL_SIZE = PART_VERTICES_SIZE + PART_COORDS_SIZE;

    public byte[] mSkeletonData = null;
    public List<String> mImageIndex = new ArrayList<String>();
    public List<String> mRequiredImages = new ArrayList<String>();

    // header data
    protected float mVersion = 0;
    protected int mNumParts = 0;
    protected int mNumFrames = 0;
    protected int mFrameSize = 0;
    private float[] mVertexFloats;
    private float[] mCoordFloats;

    private Map<String, Texture> mTextures;

    public AniFile() {
        // TODO nothing
    }

    public AniFile(final AssetManager assetManager, final String filePath) {
        try {
            parse(assetManager.open(filePath));
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public AniFile(final XmlResourceParser parser) {
        try {
            parse(parser);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public AniFile(final InputStream stream) {
        try {
            parse(stream);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    protected void parse(final InputStream stream) throws Exception {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setValidating(false);
        XmlPullParser xml = factory.newPullParser();
        xml.setInput(stream, null);

        parse(xml);
    }

    protected void parse(final XmlPullParser xml) throws Exception {
        String key = null;
        int eventType = xml.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {

            if (eventType == XmlPullParser.START_DOCUMENT) {
                // TODO nothing
            } else if (eventType == XmlPullParser.START_TAG) {
                String name = xml.getName();
                if (name.equals("key")) {
                    key = xml.nextText();
                } else if ((key != null) && name.equals("data") && key.equals("floatdata")) {
                    mSkeletonData = Base64.decode(xml.nextText(), Base64.DEFAULT);

                    // header
                    final int versionBits = (mSkeletonData[3] << 24) | ((mSkeletonData[2] & 0xFF) << 16) | ((mSkeletonData[1] & 0xFF) << 8) | (mSkeletonData[0] & 0xFF);
                    mVersion = Float.intBitsToFloat(versionBits);
                    mNumFrames = mSkeletonData[4];
                    mNumParts = mSkeletonData[8];
                    mFrameSize = mNumParts * (mVersion == 1 ? PART_TOTAL_SIZE : PART_VERTICES_SIZE);
                    mVertexFloats = new float[PART_VERTICES];
                    mCoordFloats = new float[PART_COORDS];
                } else if ((key != null) && name.equals("string") && key.equals("imageindex")) {
                    mImageIndex.add(xml.nextText());
                } else if ((key != null) && name.equals("string") && key.equals("requiredimages")) {
                    mRequiredImages.add(xml.nextText());
                }
            } else if (eventType == XmlPullParser.END_TAG) {
                // TODO nothing
            }

            // next item
            eventType = xml.next();
        }
    }

    /**
     * Get the Vertex Buffers of all the parts in a specific frame
     * 
     * @param frame
     * @param buffers
     * @return
     */
    public boolean getFrameVertexBuffers(final int frame, final int flips, final VertexBuffer[] buffers, final RectF bounds) {
        int start = (frame * mFrameSize) + HEADER;
        if (mVersion == 2) {
            // the coordinates are the same across the frames
            // so it's part of the Header
            start += PART_COORDS_SIZE * mNumParts;
        }

        for (int i = 0; i < mNumParts; i++) {
            // find the vertices
            readVertices(mSkeletonData, start, mVertexFloats, flips, bounds);

            // get the buffers
            if (buffers != null) {
                // set into vertex buffers
                if (buffers[i] == null) {
                    buffers[i] = new VertexBuffer(GL10.GL_TRIANGLE_STRIP, PART_POINTS, mVertexFloats);
                    buffers[i].setVertexPointerSize(3); // xyz
                } else {
                    buffers[i].setValues(mVertexFloats);
                }
            }

            start += mVersion == 1 ? PART_TOTAL_SIZE : PART_VERTICES_SIZE;
        }

        return true;
    }

    /**
     * Get the Texture Coordinate Buffers of all the parts in a specific frame
     * 
     * @param frame
     * @param buffers
     * @return
     */
    public int getFrameCoordBuffers(final int frame, final TextureCoordBuffer[] buffers) {

        if (mVersion == 1) {
            int start = (frame * mFrameSize) + HEADER + PART_VERTICES_SIZE;
            for (int i = 0; i < mNumParts; i++) {
                readFloats(mSkeletonData, start, mCoordFloats);
                // set into vertex buffers
                if (buffers[i] == null) {
                    buffers[i] = new TextureCoordBuffer(mCoordFloats);
                } else {
                    buffers[i].setValues(mCoordFloats);
                }

                start += PART_TOTAL_SIZE;
            }

        } else if (mVersion == 2) {
            // the coordinates are the same across the frames
            // so it's next to the Header
            int start = HEADER;
            for (int i = 0; i < mNumParts; i++) {
                readFloats(mSkeletonData, start, mCoordFloats);

                // set into vertex buffers
                if (buffers[i] == null) {
                    buffers[i] = new TextureCoordBuffer(mCoordFloats);
                } else {
                    buffers[i].setValues(mCoordFloats);
                }

                start += PART_COORDS_SIZE;
            }
        }

        return mNumParts;
    }

    public byte[] getSkeletonData() {
        return mSkeletonData;
    }

    public void setSkeletonData(final byte[] floatData) {
        mSkeletonData = floatData;
    }

    public void setTextures(final Map<String, Texture> map) {
        mTextures = map;
    }

    public Texture getTexture(final int index) {
        if (mTextures == null || index >= mImageIndex.size()) {
            return null;
        }

        return mTextures.get(mImageIndex.get(index));
    }

    /**
     * Read the float values from a byte array into a float array
     * 
     * @param data
     * @param start
     * @param output
     */
    public static void readVertices(final byte[] data, int start, final float[] output, final int flips, final RectF bounds) {
        for (int i = 0; i < output.length; i++) {
            // little endian conversion
            final float value = output[i] = Float.intBitsToFloat((data[start + 3] << 24) | ((data[start + 2] & 0xFF) << 16) | ((data[start + 1] & 0xFF) << 8) | (data[start] & 0xFF));
            if (i % 3 == 0) {
                // x vertex

                if (bounds != null) {
                    if (bounds.left > value) {
                        bounds.left = value;
                    }
                    if (bounds.right < value) {
                        bounds.right = value;
                    }
                }

                if ((flips & DisplayObject.FLIP_X) > 0) {
                    output[i] *= -1;
                }
            } else if (i % 3 == 1) {
                // y vertex

                if (bounds != null) {
                    if (bounds.top > value) {
                        bounds.top = value;
                    }
                    if (bounds.bottom < value) {
                        bounds.bottom = value;
                    }
                }

                if ((flips & DisplayObject.FLIP_Y) > 0) {
                    output[i] *= -1;
                }
            }

            // next float
            start += FLOAT;
        }
    }

    /**
     * Read the float values from a byte array into a float array
     * 
     * @param data
     * @param start
     * @param output
     */
    public static void readFloats(final byte[] data, int start, final float[] output) {
        for (int i = 0; i < output.length; i++) {
            // little endian conversion
            output[i] = Float.intBitsToFloat((data[start + 3] << 24) | ((data[start + 2] & 0xFF) << 16) | ((data[start + 1] & 0xFF) << 8) | (data[start] & 0xFF));
            start += 4;
        }
    }
}

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

import java.util.ArrayList;
import java.util.Collections;

import android.graphics.PointF;

import com.funzio.pure2D.gl.gl10.textures.Texture;

/**
 * @author long
 */
public class AtlasFrameSet {
    protected final String mName;
    protected ArrayList<AtlasFrame> mFrames = new ArrayList<AtlasFrame>();
    protected PointF mFrameMaxSize = new PointF();
    protected Texture mTexture;
    protected int mFps = 0;
    protected int mLoopMode = -1; // ignored

    public AtlasFrameSet(final String name) {
        mName = name;
    }

    public AtlasFrameSet(final String name, final Texture... textures) {
        mName = name;

        // null check
        if (textures == null) {
            return;
        }

        final int num = textures.length;
        for (int i = 0; i < num; i++) {
            addFrame(new AtlasFrame(textures[i], i, null));
        }
    }

    public void addFrame(final AtlasFrame frame) {
        mFrames.add(frame);

        // check and auto assign texture
        if (frame.getTexture() == null) {
            frame.setTexture(mTexture);
        }

        if (frame.mSize.x > mFrameMaxSize.x) {
            mFrameMaxSize.x = frame.mSize.x;
        }
        if (frame.mSize.y > mFrameMaxSize.y) {
            mFrameMaxSize.y = frame.mSize.y;
        }
    }

    public boolean removeFrame(final AtlasFrame frame) {
        return mFrames.remove(frame);
    }

    public void removeAllFrames() {
        mFrames.clear();
    }

    public AtlasFrame getFrame(final int index) {
        return index < mFrames.size() ? mFrames.get(index) : null;
    }

    public AtlasFrame getFrame(final String name) {
        int len = mFrames.size();
        for (int i = 0; i < len; i++) {
            AtlasFrame frame = mFrames.get(i);
            if (frame.mName.equals(name)) {
                return frame;
            }
        }

        return null;
    }

    public void setTexture(final Texture texture) {
        mTexture = texture;

        // apply to all frames
        int len = mFrames.size();
        for (int i = 0; i < len; i++) {
            mFrames.get(i).setTexture(texture);
        }
    }

    public Texture getTexture() {
        return mTexture;
    }

    public int getNumFrames() {
        return mFrames.size();
    }

    public void reverseFrames() {
        Collections.reverse(mFrames);
    }

    public void appendFrames(final AtlasFrameSet frames) {
        final int size = frames.getNumFrames();
        for (int i = 0; i < size; i++) {
            addFrame(frames.getFrame(i));
        }
    }

    @Override
    public String toString() {
        StringBuilder st = new StringBuilder();
        int len = mFrames.size();
        for (int i = 0; i < len; i++) {
            AtlasFrame frame = mFrames.get(i);
            st.append(frame.toString());
            if (i < len - 1) {
                st.append("\n");
            }
        }
        return String.format("AtlasFrameSet( %s,\n%s )", mName, st.toString());
    }

    /**
     * Extract the frame set's name from a full frame name
     * 
     * @param full
     * @param delimiter
     * @return
     */
    public static String extractName(final String full, final String delimiter) {
        String[] tokens = full.split(delimiter);
        if (tokens.length <= 1) {
            return full;
        }

        try {
            int newLen = tokens.length - 1;
            Integer.parseInt(tokens[newLen]);
            StringBuffer name = new StringBuffer();
            for (int i = 0; i < newLen; i++) {
                name.append(tokens[i]);
                if (i < newLen - 1) {
                    name.append(delimiter);
                }
            }
            return name.toString();
        } catch (NumberFormatException e) {
            return full;
        }
    }

    public static String extractName(final String full) {
        return extractName(full, "_");
    }

    public PointF getFrameMaxSize() {
        return mFrameMaxSize;
    }

    public void setFrameMaxSize(final PointF frameMaxSize) {
        mFrameMaxSize = frameMaxSize;
    }

    public int getFps() {
        return mFps;
    }

    public void setFps(final int fps) {
        mFps = fps;
    }

    public int getLoopMode() {
        return mLoopMode;
    }

    public void setLoopMode(final int loopMode) {
        mLoopMode = loopMode;
    }

    public static AtlasFrameSet createReversedFrameSet(final AtlasFrameSet frameSet) {
        final AtlasFrameSet reversed = new AtlasFrameSet(frameSet.mName);
        reversed.setTexture(frameSet.getTexture());
        for (int i = frameSet.getNumFrames() - 1; i >= 0; i--) {
            reversed.addFrame(frameSet.getFrame(i));
        }

        return reversed;
    }
}

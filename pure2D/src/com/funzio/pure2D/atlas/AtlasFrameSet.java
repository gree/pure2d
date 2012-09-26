/**
 * 
 */
package com.funzio.pure2D.atlas;

import java.util.ArrayList;
import java.util.List;

/**
 * @author long
 */
public class AtlasFrameSet {
    protected final String mName;
    protected List<AtlasFrame> mFrames = new ArrayList<AtlasFrame>();

    public AtlasFrameSet(final String name) {
        mName = name;
    }

    public void addFrame(final AtlasFrame frame) {
        mFrames.add(frame);
    }

    public boolean removeFrame(final AtlasFrame frame) {
        return mFrames.remove(frame);
    }

    public void removeAllFrames() {
        mFrames.clear();
    }

    public AtlasFrame getFrame(final int index) {
        return mFrames.get(index);
    }

    public AtlasFrame getFrame(final String name) {
        int len = mFrames.size();
        for (int i = 0; i < len; i++) {
            AtlasFrame frame = mFrames.get(i);
            if (frame.getName().equals(name)) {
                return frame;
            }
        }

        return null;
    }

    public int getNumFrames() {
        return mFrames.size();
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
}

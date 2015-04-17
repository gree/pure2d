/**
 * ****************************************************************************
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
 * ****************************************************************************
 */
/**
 *
 */
package com.funzio.pure2D.shapes;

import android.graphics.RectF;

import com.funzio.pure2D.Scene;
import com.funzio.pure2D.atlas.AtlasFrame;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.ui.UIManager;
import com.funzio.pure2D.uni.UniGroup;
import com.funzio.pure2D.uni.UniSprite;

import org.xmlpull.v1.XmlPullParser;

/**
 * @author long
 */
public class Sprite9 extends UniGroup {
    protected static final int NUM_PATCHES = 9;

    private RectF m9Patches;

    private boolean m9PatchEnabled = true;
    private boolean mHasPatches = false;
    private boolean mSizeToTexture = false;

    public Sprite9() {
        super();

        UniSprite sprite;
        for (int i = 0; i < NUM_PATCHES; i++) {
            sprite = new UniSprite();
            sprite.setSizeToTexture(false);
            sprite.setSizeToFrame(false);
            sprite.setAtlasFrame(new AtlasFrame(mTexture, i, null));
            // sprite.setDebugFlags(Pure2D.DEBUG_FLAG_GLOBAL_BOUNDS | Pure2D.DEBUG_FLAG_WIREFRAME);
            addChild(sprite);
        }
    }

    public void set9Patches(final float left, final float right, final float top, final float bottom) {
        if (m9Patches == null) {
            m9Patches = new RectF(left, top, right, bottom);
        } else {
            m9Patches.set(left, top, right, bottom);
        }

        mHasPatches = left > 0 || right > 0 || top > 0 || bottom > 0;

        invalidate(CHILDREN);
    }

    public RectF get9Patches() {
        return m9Patches;
    }

    public boolean is9PatchEnabled() {
        return m9PatchEnabled;
    }

    public void set9PatchEnabled(final boolean value) {
        m9PatchEnabled = value;

        invalidate(CHILDREN);
    }

    public boolean isSizeToTexture() {
        return mSizeToTexture;
    }

    public void setSizeToTexture(final boolean value) {
        mSizeToTexture = value;

        // fit size to texture
        if (mSizeToTexture && mTexture != null) {
            setSize(mTexture.getSize());
        }
    }

    @Override
    public void setTexture(final Texture texture) {
        super.setTexture(texture);

        // auto set size if it's not set
        if ((mSizeToTexture || (mSize.x <= 1 && mSize.y <= 1)) && texture != null) {
            setSize(texture.getSize());
        }
    }

    @Override
    protected void onTextureLoaded(final Texture texture) {
        super.onTextureLoaded(texture);

        // match size
        if (mSizeToTexture || (mSize.x <= 1 && mSize.y <= 1)) {
            setSize(texture.getSize());
        } else {
            // invalidate the 9 patches
            invalidate(CHILDREN);
        }
    }

    @Override
    protected void updateChildren(final int deltaTime) {
        if ((mInvalidateFlags & (CHILDREN | TEXTURE | SIZE)) != 0) {
            if (mTexture == null) {
                super.updateChildren(deltaTime);
                return;
            }

            // some constants
            final float textureW = mTexture.getSize().x;
            final float textureH = mTexture.getSize().y;
            float left = m9PatchEnabled ? m9Patches.left : 0;
            float right = m9PatchEnabled ? m9Patches.right : 0;
            float top = m9PatchEnabled ? m9Patches.top : 0;
            float bottom = m9PatchEnabled ? m9Patches.bottom : 0;
            float middleW = mSize.x - left - right;
            float middleH = mSize.y - top - bottom;
            // if width is too small
            if (middleW < 0) {
                left = right = 0;
                middleW = mSize.x;
            }
            // if height is too small
            if (middleH < 0) {
                top = bottom = 0;
                middleH = mSize.y;
            }

            // vertices
            final float[] widths = {
                    left, middleW, right
            };
            final float[] heights = {
                    bottom, middleH, top
            };
            // swap for AXIS_TOP_LEFT
            if (mScene != null && mScene.getAxisSystem() == Scene.AXIS_TOP_LEFT) {
                heights[0] = top;
                heights[2] = bottom;
            }

            // texture coordinates
            final float[] scaleX = {
                    left, (textureW - left - right), right
            };
            final float[] scaleY = {
                    bottom, (textureH - top - bottom), top
            };
            // swap for AXIS_TOP_LEFT
            if (mScene != null && mScene.getAxisSystem() == Scene.AXIS_TOP_LEFT) {
                final float temp = scaleY[0];
                scaleY[0] = scaleY[2];
                scaleY[2] = temp;
            }

            float vx = 0, vy = 0; // vertex start x,y
            float tx = 0, ty = textureH, tyInverted = 0; // texture coord start x,y
            int index = 0;
            UniSprite sprite;
            AtlasFrame frame;
            for (int row = 0; row < 3; row++) {
                float vh = heights[row];
                float th = scaleY[row];
                vx = 0;
                tx = 0;

                for (int col = 0; col < 3; col++) {
                    float vw = widths[col];
                    float tw = scaleX[col];

                    // set the quad values
                    sprite = (UniSprite) mChildren.get(index);

                    // set the coordinates
                    if (tw != 0 && th != 0) {
                        sprite.setVisible(true);
                        sprite.setPosition(vx, vy);
                        sprite.setSize(vw, vh);
                        frame = sprite.getAtlasFrame();
                        frame.setTexture(mTexture);

                        if (mScene != null && mScene.getAxisSystem() == Scene.AXIS_TOP_LEFT) {
                            frame.setRect(tx, tyInverted + th, tx + tw, tyInverted);
                        } else {
                            frame.setRect(tx, ty - th, tx + tw, ty);
                        }
                        sprite.setAtlasFrame(frame); // apply
                        // Log.e("long", mTexture + " " + sprite.getAtlasFrame().toString());
                    } else {
                        sprite.setVisible(false);
                    }

                    vx += vw;
                    tx += tw;
                    index++;
                }

                vy += vh;
                ty -= th;
                tyInverted += th;
            }
        }

        super.updateChildren(deltaTime);
    }

    @Override
    public void setSize(final float w, final float h) {
        super.setSize(w, h);

        // also invalidate coordinates
        invalidate(CHILDREN);
    }

    /*@Override
    public boolean shouldDraw(final RectF globalViewRect) {
        return super.shouldDraw(globalViewRect) && (mTexture != null && mTexture.isLoaded());
    }*/

    @Override
    public void setXMLAttributes(final XmlPullParser xmlParser, final UIManager manager) {
        super.setXMLAttributes(xmlParser, manager);

        if (xmlParser.getAttributeValue(null, "patches") != null) {
            final String[] patches = manager.evalString(xmlParser.getAttributeValue(null, "patches")).split(",");
            final float configScale = manager.getConfig().screen_scale;
            final float left = patches.length >= 1 ? Float.valueOf(patches[0].trim()) * configScale : 0;
            final float right = patches.length >= 2 ? Float.valueOf(patches[1].trim()) * configScale : 0;
            final float top = patches.length >= 3 ? Float.valueOf(patches[2].trim()) * configScale : 0;
            final float bottom = patches.length >= 4 ? Float.valueOf(patches[3].trim()) * configScale : 0;
            set9Patches(left, right, top, bottom);
        }
    }

    @Override
    public void dispose() {
        super.dispose();

        m9Patches = null;
    }
}

package com.funzio.pure2D.ui;

import com.funzio.pure2D.gl.gl10.textures.Texture;

import org.xmlpull.v1.XmlPullParser;

/**
 * Created by longngo on 6/2/15.
 */
public class ToggleButton extends Button {
    protected static final String ATT_SELECTED = "selected";

    protected boolean mSelected;

    public ToggleButton() {
        super();
    }

    public boolean isSelected() {
        return mSelected;
    }

    public ToggleButton setSelected(final boolean selected) {
        mSelected = selected;

        invalidate(VISUAL);

        return this;
    }

    @Override
    protected Texture getStateTexture(final int state) {
        if (mTextures == null || mTextures.length == 0) return null;

        if (mTextures.length == 2) {
            return mSelected ? mTextures[1] : mTextures[0];
        } else {
            final int index = mSelected ? state + 3 : state;
            return mTextures[Math.min(index, mTextures.length - 1)];
        }
    }

    @Override
    protected void onTouchUp(final boolean hit) {
        super.onTouchUp(hit);

        if (hit) {
            // switch toggle
            setSelected(!mSelected);
        }
    }

    @Override
    public void setXMLAttributes(final XmlPullParser xmlParser, final UIManager manager) {
        super.setXMLAttributes(xmlParser, manager);

        if (xmlParser.getAttributeValue(null, ATT_SELECTED) != null) {
            final String selected = xmlParser.getAttributeValue(null, ATT_SELECTED);
            setSelected(Boolean.valueOf(selected));
        }
    }
}

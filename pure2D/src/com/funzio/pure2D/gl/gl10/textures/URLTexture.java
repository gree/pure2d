/**
 * 
 */
package com.funzio.pure2D.gl.gl10.textures;

import android.graphics.Bitmap;

import com.funzio.pure2D.gl.gl10.GLState;

/**
 * @author long
 */
public class URLTexture extends Texture {
    private String mURL;
    private Bitmap.Config mConfig;

    public URLTexture(final GLState glState, final String URL, final Bitmap.Config config) {
        super(glState);

        load(URL, config);
    }

    public void load(final String URL, final Bitmap.Config config) {
        mURL = URL;
        mConfig = config;

        // TODO add loading logic here
    }

    @Override
    public void reload() {
        load(mURL, mConfig);
    }
}

/**
 * 
 */
package com.funzio.crimecity.particles;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import android.content.res.Resources;
import android.util.Log;

import com.funzio.crimecity.game.model.CCMapDirection;
import com.funzio.pure2D.demo.R;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.gl.gl10.textures.DrawableTexture;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.gl.gl10.textures.TextureManager;
import com.funzio.pure2D.gl.gl10.textures.TextureOptions;

/**
 * @author long
 */
public class ParticleTextureManager extends TextureManager {
    private static final String TAG = ParticleTextureManager.class.getSimpleName();

    private final Map<String, Texture> mUnitTextures = new HashMap<String, Texture>();

    public final DrawableTexture mFireTexture;
    public final DrawableTexture mFireGreyTexture;
    public final DrawableTexture mSmokeTexture;

    public ParticleTextureManager(final GLState glState, final Resources res) {
        super(null, res);

        mGLState = glState;
        mGL = glState.mGL;

        TextureOptions options = TextureOptions.getDefault();
        options.inMipmaps = 1; // enable mipmapping for particles
        mFireTexture = createDrawableTexture(R.drawable.fireball_small, options);
        mFireGreyTexture = createDrawableTexture(R.drawable.fireball_grey, options);
        mSmokeTexture = createDrawableTexture(R.drawable.smoke_small, options);
    }

    public Texture getUnitTexture(final String textureKey, final CCMapDirection direction) {
        CCMapDirection east = direction;
        if (east.equals(CCMapDirection.NORTHWEST)) {
            east = CCMapDirection.NORTHEAST;
        } else if (east.equals(CCMapDirection.SOUTHWEST)) {
            east = CCMapDirection.SOUTHEAST;
        }

        // find the drawble
        String key = textureKey + "_" + east.abbrev;
        Log.v(TAG, "getUnitTexture(): " + key);
        if (mUnitTextures.containsKey(key)) {
            return mUnitTextures.get(key);
        }

        int drawable = 0;
        try {
            Field field = R.drawable.class.getField(key.toLowerCase());
            drawable = field.getInt(null);
        } catch (Exception e) {
            // TODO nothing
        }

        if (drawable > 0) {
            // create and cache
            Texture texture = createDrawableTexture(drawable, null);
            mUnitTextures.put(key, texture);

            return texture;
        } else {
            Log.e(TAG, "Bitmap not found: " + key);
            return null;
        }
    }
}

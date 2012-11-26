/**
 * 
 */
package com.funzio.pure2D.sounds;

import java.io.File;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

/**
 * @author sajjadtabib
 */
public class UriMedia extends AbstractMedia {
    protected static final String TAG = UriMedia.class.getSimpleName();

    protected Uri mUri;

    /**
     * @param key
     * @param filePath
     */
    public UriMedia(final int key, final String filePath) {
        super(key);

        mUri = Uri.fromFile(new File(filePath));

    }

    public UriMedia(final int key, final Uri uri) {
        super(key);
        mUri = uri;

    }

    public Uri getMediaUri() {
        return mUri;
    }

    public int load(final MediaPlayer player, final Context context) {
        try {
            player.setDataSource(context, mUri);
        } catch (Exception e) {
            String message = e.getMessage();
            Log.e(TAG, message == null ? "Failed to load AssetMedia" : message);
            return 0;
        }

        return 1;
    }
}

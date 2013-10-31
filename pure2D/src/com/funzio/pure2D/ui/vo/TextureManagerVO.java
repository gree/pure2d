/**
 * 
 */
package com.funzio.pure2D.ui.vo;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author long.ngo
 */
public class TextureManagerVO {

    public String cdn_url;
    public String cache_dir;
    public final int texture_expiration_time;
    public final int expiration_check_interval;

    public final TextureOptionsVO texture_options;

    public TextureManagerVO(final JSONObject json) throws JSONException {
        cdn_url = json.optString("cdn_url");
        texture_expiration_time = json.optInt("texture_expiration_time", 0);

        // expiration_check_interval = json.optInt("expiration_check_interval", 0);
        expiration_check_interval = texture_expiration_time / 2; // default value, has to be < texture_expiration_time

        texture_options = new TextureOptionsVO(json.getJSONObject("texture_options"));
    }

}

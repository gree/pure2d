/**
 * 
 */
package com.funzio.pure2D.ui.vo;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.funzio.pure2D.gl.gl10.textures.TextureOptions;

/**
 * @author long.ngo
 */
public class UIConfigVO {

    public String cdn_url;
    public String cache_dir;
    public boolean texture_async = true;
    public ArrayList<FontVO> fonts;

    public UIConfigVO(final JSONObject json) throws JSONException {
        cdn_url = json.optString("cdn_url");
        fonts = getFonts(json.getJSONArray("fonts"));
    }

    protected static ArrayList<FontVO> getFonts(final JSONArray array) throws JSONException {
        if (array == null) {
            return null;
        }

        final ArrayList<FontVO> result = new ArrayList<FontVO>();
        final int size = array.length();
        for (int i = 0; i < size; i++) {
            FontVO vo = new FontVO(array.getJSONObject(i));
            result.add(vo);
        }

        return result;
    }

    public TextureOptions getTextureOptions() {
        // TODO Auto-generated method stub
        return null;
    }
}

package com.funzio.pure2D.demo.textures;

import com.funzio.pure2D.demo.R;
import com.funzio.pure2D.demo.activities.MenuActivity;
import com.funzio.pure2D.demo.animations.HelloAtlasActivity;
import com.funzio.pure2D.demo.buffers.FrameBufferActivity;
import com.funzio.pure2D.demo.buffers.StencilBufferActivity;

public class TextureMenuActivity extends MenuActivity {

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.demo.activities.MenuActivity#getLayout()
     */
    @Override
    protected int getLayout() {
        return R.layout.texture_menu;
    }

    @Override
    protected void createMenus() {
        addMenu(R.id.btn_hello_texture, HelloTextureActivity.class);
        addMenu(R.id.btn_sprite_9, Sprite9Activity.class);
        addMenu(R.id.btn_repeating_texture, RepeatingTextureActivity.class);
        addMenu(R.id.btn_multiple_texture, MultipleTextureActivity.class);

        addMenu(R.id.btn_blending, TextureBlendingActivity.class);
        addMenu(R.id.btn_masking, StencilBufferActivity.class);
        addMenu(R.id.btn_frame_buffer, FrameBufferActivity.class);
        addMenu(R.id.btn_hello_atlas, HelloAtlasActivity.class);
        addMenu(R.id.btn_image_sequence, ImageSequenceActivity.class);
    }
}

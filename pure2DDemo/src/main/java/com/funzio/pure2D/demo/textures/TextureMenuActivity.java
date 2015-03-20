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
package com.funzio.pure2D.demo.textures;

import com.funzio.pure2D.demo.activities.MenuActivity;
import com.funzio.pure2D.demo.animations.HelloAtlasActivity;
import com.funzio.pure2D.demo.buffers.FrameBufferActivity;
import com.funzio.pure2D.demo.buffers.StencilBufferActivity;
import com.longo.pure2D.demo.R;

public class TextureMenuActivity extends MenuActivity {

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
        addMenu(R.id.btn_texture_packer, TexturePackerActivity.class);
        addMenu(R.id.btn_url_texture, URLTextureActivity.class);

        addMenu(R.id.btn_blending, TextureBlendingActivity.class);
        addMenu(R.id.btn_masking, StencilBufferActivity.class);
        addMenu(R.id.btn_frame_buffer, FrameBufferActivity.class);
        addMenu(R.id.btn_hello_atlas, HelloAtlasActivity.class);
        addMenu(R.id.btn_image_sequence, ImageSequenceActivity.class);
    }
}

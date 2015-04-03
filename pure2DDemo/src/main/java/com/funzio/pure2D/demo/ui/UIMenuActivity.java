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
package com.funzio.pure2D.demo.ui;

import com.funzio.pure2D.demo.activities.MenuActivity;
import com.funzio.pure2D.demo.containers.HListActivity;
import com.funzio.pure2D.demo.containers.HWheelActivity;
import com.funzio.pure2D.demo.containers.VListActivity;
import com.funzio.pure2D.demo.containers.VWheelActivity;
import com.funzio.pure2D.demo.textures.Sprite9Activity;
import com.longo.pure2D.demo.R;

public class UIMenuActivity extends MenuActivity {

    @Override
    protected int getLayout() {
        return R.layout.ui_menu;
    }

    @Override
    protected void createMenus() {
        addMenu(R.id.btn_hello_text, HelloTextActivity.class);
        addMenu(R.id.btn_sprite_9, Sprite9Activity.class);
        addMenu(R.id.btn_button, ButtonActivity.class);
        addMenu(R.id.btn_vwheel, VWheelActivity.class);
        addMenu(R.id.btn_hwheel, HWheelActivity.class);
        addMenu(R.id.btn_scrolls, ScrollActivity.class);
        addMenu(R.id.btn_bitmap_font, BitmapFontActivity.class);
        addMenu(R.id.btn_korean_bitmap_font, KoreanCharsetActivity.class);
        addMenu(R.id.btn_vlist, VListActivity.class);
        addMenu(R.id.btn_hlist, HListActivity.class);
    }
}

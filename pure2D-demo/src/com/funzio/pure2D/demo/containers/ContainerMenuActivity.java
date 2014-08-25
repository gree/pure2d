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
package com.funzio.pure2D.demo.containers;

import com.funzio.pure2D.demo.activities.MenuActivity;
import com.longo.pure2D.demo.R;

public class ContainerMenuActivity extends MenuActivity {

    @Override
    protected int getLayout() {
        return R.layout.container_menu;
    }

    @Override
    protected void createMenus() {
        addMenu(R.id.btn_display_groups, DisplayGroupActivity.class);
        addMenu(R.id.btn_vgroup, VGroupActivity.class);
        addMenu(R.id.btn_hgroup, HGroupActivity.class);
        addMenu(R.id.btn_vwheel, VWheelActivity.class);
        addMenu(R.id.btn_hwheel, HWheelActivity.class);
        addMenu(R.id.btn_vwheel_3d, VWheel3DActivity.class);
        addMenu(R.id.btn_hwheel_3d, HWheel3DActivity.class);
        addMenu(R.id.btn_grid_group, GridGroupActivity.class);
        addMenu(R.id.btn_uni_groups, UniGroupActivity.class);
        addMenu(R.id.btn_uni_clips, UniGroupClipActivity.class);
    }

}

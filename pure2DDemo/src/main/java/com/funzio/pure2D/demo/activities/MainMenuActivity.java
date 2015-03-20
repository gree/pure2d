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
package com.funzio.pure2D.demo.activities;

import com.funzio.pure2D.demo.animations.AnimationMenuActivity;
import com.funzio.pure2D.demo.astar.AstarMenuActivity;
import com.funzio.pure2D.demo.buffers.BufferMenuActivity;
import com.funzio.pure2D.demo.camera.CameraMenuActivity;
import com.funzio.pure2D.demo.containers.ContainerMenuActivity;
import com.funzio.pure2D.demo.effects.EffectsMenuActivity;
import com.funzio.pure2D.demo.loaders.LoaderMenuActivity;
import com.funzio.pure2D.demo.objects.ObjectMenuActivity;
import com.funzio.pure2D.demo.particles.NovaMenuActivity;
import com.funzio.pure2D.demo.particles.ParticleMenuActivity;
import com.funzio.pure2D.demo.physics.PhysicsMenuActivity;
import com.funzio.pure2D.demo.pui.PUIMenuActivity;
import com.funzio.pure2D.demo.simple3D.Simple3DMenuActivity;
import com.funzio.pure2D.demo.textures.TextureMenuActivity;
import com.funzio.pure2D.demo.ui.UIMenuActivity;
import com.longo.pure2D.demo.R;

public class MainMenuActivity extends MenuActivity {

    @Override
    protected int getLayout() {
        return R.layout.main_menu;
    }

    @Override
    protected void createMenus() {

        addMenu(R.id.btn_display_objects, ObjectMenuActivity.class);
        addMenu(R.id.btn_containers, ContainerMenuActivity.class);
        addMenu(R.id.btn_camera, CameraMenuActivity.class);
        addMenu(R.id.btn_textures, TextureMenuActivity.class);
        addMenu(R.id.btn_buffers, BufferMenuActivity.class);
        addMenu(R.id.btn_3d, Simple3DMenuActivity.class);
        addMenu(R.id.btn_nova_system, NovaMenuActivity.class);
        addMenu(R.id.btn_particles, ParticleMenuActivity.class);
        addMenu(R.id.btn_effects, EffectsMenuActivity.class);
        addMenu(R.id.btn_animations, AnimationMenuActivity.class);
        addMenu(R.id.btn_physics, PhysicsMenuActivity.class);
        addMenu(R.id.btn_astar, AstarMenuActivity.class);
        addMenu(R.id.btn_loaders, LoaderMenuActivity.class);
        addMenu(R.id.btn_ui, UIMenuActivity.class);
        addMenu(R.id.btn_pui_layouts, PUIMenuActivity.class);
    }

}

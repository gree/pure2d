package com.funzio.pure2D.demo.activities;

import com.funzio.pure2D.demo.R;
import com.funzio.pure2D.demo.animations.AnimationMenuActivity;
import com.funzio.pure2D.demo.buffers.BufferMenuActivity;
import com.funzio.pure2D.demo.camera.CameraMenuActivity;
import com.funzio.pure2D.demo.casino.CasinoMenuActivity;
import com.funzio.pure2D.demo.containers.ContainerMenuActivity;
import com.funzio.pure2D.demo.effects.EffectsMenuActivity;
import com.funzio.pure2D.demo.loaders.LoaderMenuActivity;
import com.funzio.pure2D.demo.mw.MWMenuActivity;
import com.funzio.pure2D.demo.objects.ObjectMenuActivity;
import com.funzio.pure2D.demo.particles.ParticleMenuActivity;
import com.funzio.pure2D.demo.physics.PhysicsMenuActivity;
import com.funzio.pure2D.demo.simple3D.Simple3DMenuActivity;
import com.funzio.pure2D.demo.text.TextMenuActivity;
import com.funzio.pure2D.demo.textures.TextureMenuActivity;

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
        addMenu(R.id.btn_particles, ParticleMenuActivity.class);
        addMenu(R.id.btn_effects, EffectsMenuActivity.class);
        addMenu(R.id.btn_animations, AnimationMenuActivity.class);
        addMenu(R.id.btn_physics, PhysicsMenuActivity.class);
        addMenu(R.id.btn_loaders, LoaderMenuActivity.class);
        addMenu(R.id.btn_mw, MWMenuActivity.class);
        addMenu(R.id.btn_text, TextMenuActivity.class);
        addMenu(R.id.btn_casino, CasinoMenuActivity.class);
    }

}

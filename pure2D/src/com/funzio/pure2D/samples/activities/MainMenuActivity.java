package com.funzio.pure2D.samples.activities;

import com.funzio.pure2D.R;
import com.funzio.pure2D.samples.activities.animations.AnimationMenuActivity;
import com.funzio.pure2D.samples.activities.buffers.BufferMenuActivity;
import com.funzio.pure2D.samples.activities.camera.CameraMenuActivity;
import com.funzio.pure2D.samples.activities.casino.CasinoMenuActivity;
import com.funzio.pure2D.samples.activities.containers.ContainerMenuActivity;
import com.funzio.pure2D.samples.activities.effects.EffectsMenuActivity;
import com.funzio.pure2D.samples.activities.loaders.LoaderMenuActivity;
import com.funzio.pure2D.samples.activities.mw.MWMenuActivity;
import com.funzio.pure2D.samples.activities.objects.ObjectMenuActivity;
import com.funzio.pure2D.samples.activities.particles.ParticleMenuActivity;
import com.funzio.pure2D.samples.activities.physics.PhysicsMenuActivity;
import com.funzio.pure2D.samples.activities.simple3D.Simple3DMenuActivity;
import com.funzio.pure2D.samples.activities.text.TextMenuActivity;
import com.funzio.pure2D.samples.activities.textures.TextureMenuActivity;

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

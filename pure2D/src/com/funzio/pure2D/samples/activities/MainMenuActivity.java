package com.funzio.pure2D.samples.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager.LayoutParams;

import com.funzio.pure2D.R;
import com.funzio.pure2D.samples.activities.animations.AnimationMenuActivity;
import com.funzio.pure2D.samples.activities.camera.CameraMenuActivity;
import com.funzio.pure2D.samples.activities.casino.CasinoMenuActivity;
import com.funzio.pure2D.samples.activities.containers.ContainerMenuActivity;
import com.funzio.pure2D.samples.activities.effects.EffectsMenuActivity;
import com.funzio.pure2D.samples.activities.loaders.LoaderMenuActivity;
import com.funzio.pure2D.samples.activities.mw.MWMenuActivity;
import com.funzio.pure2D.samples.activities.objects.ObjectMenuActivity;
import com.funzio.pure2D.samples.activities.particles.ParticleMenuActivity;
import com.funzio.pure2D.samples.activities.physics.PhysicsMenuActivity;
import com.funzio.pure2D.samples.activities.text.TextMenuActivity;
import com.funzio.pure2D.samples.activities.textures.TextureMenuActivity;

public class MainMenuActivity extends Activity {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);

        getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN, LayoutParams.FLAG_FULLSCREEN);
    }

    public void onClickButton(final View view) {
        if (view.getId() == R.id.btn_display_objects) {
            startActivity(new Intent(this, ObjectMenuActivity.class));
        } else if (view.getId() == R.id.btn_containers) {
            startActivity(new Intent(this, ContainerMenuActivity.class));
        } else if (view.getId() == R.id.btn_camera) {
            startActivity(new Intent(this, CameraMenuActivity.class));
        } else if (view.getId() == R.id.btn_textures) {
            startActivity(new Intent(this, TextureMenuActivity.class));
        } else if (view.getId() == R.id.btn_particles) {
            startActivity(new Intent(this, ParticleMenuActivity.class));
        } else if (view.getId() == R.id.btn_effects) {
            startActivity(new Intent(this, EffectsMenuActivity.class));
        } else if (view.getId() == R.id.btn_animations) {
            startActivity(new Intent(this, AnimationMenuActivity.class));
        } else if (view.getId() == R.id.btn_physics) {
            startActivity(new Intent(this, PhysicsMenuActivity.class));
        } else if (view.getId() == R.id.btn_loaders) {
            startActivity(new Intent(this, LoaderMenuActivity.class));
        } else if (view.getId() == R.id.btn_mw) {
            startActivity(new Intent(this, MWMenuActivity.class));
        } else if (view.getId() == R.id.btn_text) {
            startActivity(new Intent(this, TextMenuActivity.class));
        } else if (view.getId() == R.id.btn_casino) {
            startActivity(new Intent(this, CasinoMenuActivity.class));
        }
    }
}

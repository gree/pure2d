package com.funzio.pure2D.samples.activities.animations;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager.LayoutParams;

import com.funzio.pure2D.R;
import com.funzio.pure2D.samples.activities.textures.ImageSequenceActivity;

public class AnimationMenuActivity extends Activity {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.animation_menu);

        getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN, LayoutParams.FLAG_FULLSCREEN);
    }

    public void onClickButton(final View view) {
        final int id = view.getId();
        if (id == R.id.btn_hello_atlas) {
            startActivity(new Intent(this, HelloAtlasActivity.class));
        } else if (id == R.id.btn_image_sequence) {
            startActivity(new Intent(this, ImageSequenceActivity.class));
        } else if (id == R.id.btn_tween_animations) {
            startActivity(new Intent(this, TweenAnimationsActivity.class));
        } else if (id == R.id.btn_skeleton_animation) {
            startActivity(new Intent(this, SkeletonActivity.class));
        } else if (id == R.id.btn_skeleton_animation_cache) {
            startActivity(new Intent(this, SkeletonCacheActivity.class));
        }
    }
}

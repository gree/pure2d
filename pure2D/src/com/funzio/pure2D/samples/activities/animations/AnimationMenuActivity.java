package com.funzio.pure2D.samples.activities.animations;

import com.funzio.pure2D.R;
import com.funzio.pure2D.samples.activities.MenuActivity;
import com.funzio.pure2D.samples.activities.particles.CoinExplosionActivity;
import com.funzio.pure2D.samples.activities.textures.ImageSequenceActivity;

public class AnimationMenuActivity extends MenuActivity {

    @Override
    protected int getLayout() {
        return R.layout.animation_menu;
    }

    @Override
    protected void createMenus() {
        addMenu(R.id.btn_hello_atlas, HelloAtlasActivity.class);
        addMenu(R.id.btn_json_atlas, JsonAtlasActivity.class);
        addMenu(R.id.btn_image_sequence, ImageSequenceActivity.class);

        addMenu(R.id.btn_tween_animations, TweenAnimationsActivity.class);
        addMenu(R.id.btn_path_animation, PathAnimationActivity.class);
        addMenu(R.id.btn_bezier_animation, BezierAnimationActivity.class);
        addMenu(R.id.btn_wave_animation, WaveAnimationActivity.class);

        addMenu(R.id.btn_skeleton_animation, SkeletonActivity.class);
        addMenu(R.id.btn_skeleton_animation_cache, SkeletonCacheActivity.class);
        addMenu(R.id.btn_coin_explosion, CoinExplosionActivity.class);
    }
}

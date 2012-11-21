package com.funzio.pure2D.samples.activities.animations;

import com.funzio.pure2D.R;
import com.funzio.pure2D.samples.activities.MenuActivity;
import com.funzio.pure2D.samples.activities.textures.ImageSequenceActivity;

public class AnimationMenuActivity extends MenuActivity {

    @Override
    protected int getLayout() {
        return R.layout.animation_menu;
    }

    @Override
    protected void createMenus() {
        addMenu(R.id.btn_hello_atlas, HelloAtlasActivity.class);
        addMenu(R.id.btn_image_sequence, ImageSequenceActivity.class);
        addMenu(R.id.btn_tween_animations, TweenAnimationsActivity.class);
        addMenu(R.id.btn_skeleton_animation, SkeletonActivity.class);
        addMenu(R.id.btn_skeleton_animation_cache, SkeletonCacheActivity.class);
    }
}

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
package com.funzio.pure2D.demo.animations;

import com.funzio.pure2D.demo.activities.MenuActivity;
import com.funzio.pure2D.demo.particles.CoinExplosionActivity;
import com.funzio.pure2D.demo.textures.ImageSequenceActivity;
import com.longo.pure2D.demo.R;

public class AnimationMenuActivity extends MenuActivity {

    @Override
    protected int getLayout() {
        return R.layout.animation_menu;
    }

    @Override
    protected void createMenus() {
        addMenu(R.id.btn_hello_atlas, HelloAtlasActivity.class);
        addMenu(R.id.btn_image_sequence, ImageSequenceActivity.class);
        addMenu(R.id.btn_json_atlas, JsonAtlasActivity.class);
        addMenu(R.id.btn_multi_atlas, MultiAtlasActivity.class);

        addMenu(R.id.btn_tween_animations, TweenAnimationsActivity.class);
        addMenu(R.id.btn_path_animation, PathAnimationActivity.class);
        addMenu(R.id.btn_bezier_animation, BezierAnimationActivity.class);
        addMenu(R.id.btn_wave_animation, WaveAnimationActivity.class);
        addMenu(R.id.btn_tornado_animation, TornadoAnimationActivity.class);
        addMenu(R.id.btn_whirl_animation, WhirlAnimationActivity.class);
        addMenu(R.id.btn_parallel_animation, ParallelAnimationActivity.class);

        addMenu(R.id.btn_skeleton_animation, SkeletonActivity.class);
        addMenu(R.id.btn_skeleton_animation_cache, SkeletonCacheActivity.class);
        addMenu(R.id.btn_coin_explosion, CoinExplosionActivity.class);
    }
}

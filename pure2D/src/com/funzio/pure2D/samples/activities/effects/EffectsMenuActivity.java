package com.funzio.pure2D.samples.activities.effects;

import com.funzio.pure2D.R;
import com.funzio.pure2D.samples.activities.MenuActivity;

public class EffectsMenuActivity extends MenuActivity {

    @Override
    protected int getLayout() {
        return R.layout.effect_menu;
    }

    @Override
    protected void createMenus() {
        addMenu(R.id.btn_sparks, SparksActivity.class);
        addMenu(R.id.btn_motion_trail_shape, MotionTrailShapeActivity.class);
    }
}

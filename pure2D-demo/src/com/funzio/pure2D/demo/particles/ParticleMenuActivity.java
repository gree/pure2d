package com.funzio.pure2D.demo.particles;

import com.funzio.pure2D.demo.R;
import com.funzio.pure2D.demo.activities.MenuActivity;
import com.funzio.pure2D.demo.mw.ExplosionActivity;

public class ParticleMenuActivity extends MenuActivity {

    @Override
    protected int getLayout() {
        return R.layout.particle_menu;
    }

    @Override
    protected void createMenus() {
        addMenu(R.id.btn_simple_smoke, SimpleSmokeActivity.class);
        addMenu(R.id.btn_mw_explosion, ExplosionActivity.class);
        addMenu(R.id.btn_motion_trails, MotionTrailActivity.class);
        addMenu(R.id.btn_dynamic_emitters, DynamicEmitterActivity.class);
        addMenu(R.id.btn_coin_explosion, CoinExplosionActivity.class);
        addMenu(R.id.btn_nova_system, NovaMenuActivity.class);
    }

}

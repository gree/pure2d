package com.funzio.pure2D.samples.activities.particles;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager.LayoutParams;

import com.funzio.pure2D.R;
import com.funzio.pure2D.samples.activities.mw.ExplosionActivity;

public class ParticleMenuActivity extends Activity {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.particle_menu);

        getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN, LayoutParams.FLAG_FULLSCREEN);
    }

    public void onClickButton(final View view) {
        if (view.getId() == R.id.btn_simple_smoke) {
            startActivity(new Intent(this, SimpleSmokeActivity.class));
        } else if (view.getId() == R.id.btn_mw_explosion) {
            startActivity(new Intent(this, ExplosionActivity.class));
        } else if (view.getId() == R.id.btn_motion_trails) {
            startActivity(new Intent(this, MotionTrailActivity.class));
        } else if (view.getId() == R.id.btn_dynamic_emitters) {
            startActivity(new Intent(this, DynamicEmitterActivity.class));
        }
    }
}

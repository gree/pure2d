package com.funzio.pure2D.samples.activities.mw;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager.LayoutParams;

import com.funzio.pure2D.R;

public class MWMenuActivity extends Activity {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mw_menu);

        getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN, LayoutParams.FLAG_FULLSCREEN);
    }

    public void onClickButton(final View view) {
        if (view.getId() == R.id.btn_mw_explosion) {
            startActivity(new Intent(this, ExplosionActivity.class));
        } else if (view.getId() == R.id.btn_ground_attackers) {
            startActivity(new Intent(this, GroundUnitsActivity.class));
        } else if (view.getId() == R.id.btn_air_attackers) {
            startActivity(new Intent(this, AirUnitsActivity.class));
        } else if (view.getId() == R.id.btn_sea_attackers) {
            startActivity(new Intent(this, SeaUnitsActivity.class));
        }
    }
}

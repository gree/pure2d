package com.funzio.pure2D.samples.activities.effects;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager.LayoutParams;

import com.funzio.pure2D.R;

public class EffectsMenuActivity extends Activity {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.effect_menu);

        getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN, LayoutParams.FLAG_FULLSCREEN);
    }

    public void onClickButton(final View view) {
        if (view.getId() == R.id.btn_sparks) {
            startActivity(new Intent(this, SparksActivity.class));
        }
    }
}

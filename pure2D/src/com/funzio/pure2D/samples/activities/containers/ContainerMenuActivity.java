package com.funzio.pure2D.samples.activities.containers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager.LayoutParams;

import com.funzio.pure2D.R;

public class ContainerMenuActivity extends Activity {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.container_menu);

        getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN, LayoutParams.FLAG_FULLSCREEN);
    }

    public void onClickButton(final View view) {
        if (view.getId() == R.id.btn_display_groups) {
            startActivity(new Intent(this, DisplayGroupActivity.class));
        } else if (view.getId() == R.id.btn_vgroup) {
            startActivity(new Intent(this, VGroupActivity.class));
        } else if (view.getId() == R.id.btn_hgroup) {
            startActivity(new Intent(this, HGroupActivity.class));
        } else if (view.getId() == R.id.btn_vwheel) {
            startActivity(new Intent(this, VWheelActivity.class));
        } else if (view.getId() == R.id.btn_hwheel) {
            startActivity(new Intent(this, HWheelActivity.class));
        }
    }
}

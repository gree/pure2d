package com.funzio.pure2D.demo.physics;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager.LayoutParams;

import com.funzio.pure2D.demo.R;

public class PhysicsMenuActivity extends Activity {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.physics_menu);

        getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN, LayoutParams.FLAG_FULLSCREEN);
    }

    public void onClickButton(final View view) {
        if (view.getId() == R.id.btn_hello_physic) {
            startActivity(new Intent(this, HelloPhysicsActivity.class));
        }
    }
}

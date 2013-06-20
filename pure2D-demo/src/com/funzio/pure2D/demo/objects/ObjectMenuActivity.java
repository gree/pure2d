package com.funzio.pure2D.demo.objects;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager.LayoutParams;

import com.funzio.pure2D.demo.R;

public class ObjectMenuActivity extends Activity {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.object_menu);

        getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN, LayoutParams.FLAG_FULLSCREEN);
    }

    public void onClickButton(final View view) {
        if (view.getId() == R.id.btn_hello_display_object) {
            startActivity(new Intent(this, HelloObjectActivity.class));
        } else if (view.getId() == R.id.btn_multiple_objects) {
            startActivity(new Intent(this, MultipleObjectActivity.class));
        } else if (view.getId() == R.id.btn_jumping_objects) {
            startActivity(new Intent(this, JumperActivity.class));
        } else if (view.getId() == R.id.btn_bouncing_objects) {
            startActivity(new Intent(this, BouncerActivity.class));
        } else if (view.getId() == R.id.btn_lwf_character) {
            startActivity(new Intent(this, LWFCharacterActivity.class));
        } else if (view.getId() == R.id.btn_lwf_cinematic) {
            startActivity(new Intent(this, LWFCinematicActivity.class));
        }
    }
}

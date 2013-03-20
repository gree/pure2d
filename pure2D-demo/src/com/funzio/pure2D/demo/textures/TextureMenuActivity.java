package com.funzio.pure2D.demo.textures;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager.LayoutParams;

import com.funzio.pure2D.demo.R;
import com.funzio.pure2D.demo.animations.HelloAtlasActivity;
import com.funzio.pure2D.demo.buffers.FrameBufferActivity;

public class TextureMenuActivity extends Activity {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.texture_menu);

        getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN, LayoutParams.FLAG_FULLSCREEN);
    }

    public void onClickButton(final View view) {
        if (view.getId() == R.id.btn_hello_texture) {
            startActivity(new Intent(this, HelloTextureActivity.class));
        } else if (view.getId() == R.id.btn_repeating_texture) {
            startActivity(new Intent(this, RepeatingTextureActivity.class));
        } else if (view.getId() == R.id.btn_multiple_texture) {
            startActivity(new Intent(this, MultipleTextureActivity.class));
        } else if (view.getId() == R.id.btn_masking) {
            startActivity(new Intent(this, TextureMaskingActivity.class));
        } else if (view.getId() == R.id.btn_frame_buffer) {
            startActivity(new Intent(this, FrameBufferActivity.class));
        } else if (view.getId() == R.id.btn_hello_atlas) {
            startActivity(new Intent(this, HelloAtlasActivity.class));
        } else if (view.getId() == R.id.btn_image_sequence) {
            startActivity(new Intent(this, ImageSequenceActivity.class));
        }
    }
}

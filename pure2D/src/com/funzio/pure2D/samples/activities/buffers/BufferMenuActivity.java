package com.funzio.pure2D.samples.activities.buffers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager.LayoutParams;

import com.funzio.pure2D.R;

public class BufferMenuActivity extends Activity {

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.buffer_menu);

        getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN, LayoutParams.FLAG_FULLSCREEN);
    }

    public void onClickButton(final View view) {
        if (view.getId() == R.id.btn_frame_buffer) {
            startActivity(new Intent(this, FrameBufferActivity.class));
        } else if (view.getId() == R.id.btn_stencil_buffer) {
            startActivity(new Intent(this, StencilBufferActivity.class));
        }
    }
}

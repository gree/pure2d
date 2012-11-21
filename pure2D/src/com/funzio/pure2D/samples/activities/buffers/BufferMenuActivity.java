package com.funzio.pure2D.samples.activities.buffers;

import com.funzio.pure2D.R;
import com.funzio.pure2D.samples.activities.MenuActivity;

public class BufferMenuActivity extends MenuActivity {

    @Override
    protected int getLayout() {
        return R.layout.buffer_menu;
    }

    @Override
    protected void createMenus() {
        addMenu(R.id.btn_frame_buffer, FrameBufferActivity.class);
        addMenu(R.id.btn_stencil_buffer, StencilBufferActivity.class);
    }
}

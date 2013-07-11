package com.funzio.pure2D.demo.buffers;

import com.funzio.pure2D.demo.R;
import com.funzio.pure2D.demo.activities.MenuActivity;

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

package com.funzio.pure2D.demo.camera;

import com.funzio.pure2D.demo.R;
import com.funzio.pure2D.demo.activities.MenuActivity;

public class CameraMenuActivity extends MenuActivity {
    @Override
    protected int getLayout() {
        return R.layout.camera_menu;
    }

    @Override
    protected void createMenus() {
        addMenu(R.id.btn_hello_camera, HelloCameraActivity.class);
        addMenu(R.id.btn_perspective_camera, PerspectiveCameraActivity.class);
    }
}

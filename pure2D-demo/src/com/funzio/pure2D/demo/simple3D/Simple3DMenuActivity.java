package com.funzio.pure2D.demo.simple3D;

import com.funzio.pure2D.demo.R;
import com.funzio.pure2D.demo.activities.MenuActivity;
import com.funzio.pure2D.demo.camera.PerspectiveCameraActivity;

public class Simple3DMenuActivity extends MenuActivity {
    @Override
    protected int getLayout() {
        return R.layout.simple_3d_menu;
    }

    @Override
    protected void createMenus() {
        addMenu(R.id.btn_perspective_camera, PerspectiveCameraActivity.class);
        // addMenu(R.id.btn_3d_rotation, Rotation3DActivity.class);
    }
}

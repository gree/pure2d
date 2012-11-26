package com.funzio.pure2D.samples.activities.simple3D;

import com.funzio.pure2D.R;
import com.funzio.pure2D.samples.activities.MenuActivity;
import com.funzio.pure2D.samples.activities.camera.PerspectiveCameraActivity;

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

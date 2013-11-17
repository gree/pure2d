package com.funzio.pure2D.demo.containers;

import com.funzio.pure2D.demo.R;
import com.funzio.pure2D.demo.activities.MenuActivity;

public class ContainerMenuActivity extends MenuActivity {

    @Override
    protected int getLayout() {
        return R.layout.container_menu;
    }

    @Override
    protected void createMenus() {
        addMenu(R.id.btn_display_groups, DisplayGroupActivity.class);
        addMenu(R.id.btn_vgroup, VGroupActivity.class);
        addMenu(R.id.btn_hgroup, HGroupActivity.class);
        addMenu(R.id.btn_vwheel, VWheelActivity.class);
        addMenu(R.id.btn_hwheel, HWheelActivity.class);
        addMenu(R.id.btn_vwheel_3d, VWheel3DActivity.class);
        addMenu(R.id.btn_hwheel_3d, HWheel3DActivity.class);
        addMenu(R.id.btn_grid_group, GridGroupActivity.class);
        addMenu(R.id.btn_uni_groups, UniGroupActivity.class);
        addMenu(R.id.btn_uni_clips, UniGroupClipActivity.class);
    }

}

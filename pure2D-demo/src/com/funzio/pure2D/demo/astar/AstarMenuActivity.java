package com.funzio.pure2D.demo.astar;

import com.funzio.pure2D.demo.R;
import com.funzio.pure2D.demo.activities.MenuActivity;
import com.funzio.pure2D.demo.containers.GridGroupActivity;

public class AstarMenuActivity extends MenuActivity {

    @Override
    protected int getLayout() {
        return R.layout.astar_menu;
    }

    @Override
    protected void createMenus() {
        addMenu(R.id.btn_grid_group, GridGroupActivity.class);
        addMenu(R.id.btn_astar_rect_grid, AstarRectGridActivity.class);
        addMenu(R.id.btn_astar_hex_grid, AstarHexGridActivity.class);
    }
}

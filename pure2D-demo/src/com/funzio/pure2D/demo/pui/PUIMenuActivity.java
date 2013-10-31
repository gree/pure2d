/**
 * 
 */
package com.funzio.pure2D.demo.pui;

import android.widget.Button;
import android.widget.LinearLayout;

import com.funzio.pure2D.demo.R;
import com.funzio.pure2D.demo.activities.MenuActivity;

/**
 * @author long
 */
public class PUIMenuActivity extends MenuActivity {

    private static final String[] PUI_FILES = new String[] {
            "pui_constraints", //
            "pui_fonts", //
            "pui_buttons", //
            "pui_include", //
            "pui_vgroups", //
            "pui_hgroups", //
            "pui_animators", //
            "pui_clips", //
            "pui_nova", //
            "pui_my_dialog", //
    };

    private LinearLayout mCol1;
    private LinearLayout mCol2;

    @Override
    protected int getLayout() {
        return R.layout.pui_menu;
    }

    @Override
    protected void createMenus() {
        mCol1 = (LinearLayout) mLayout.findViewById(R.id.col1);
        mCol2 = (LinearLayout) mLayout.findViewById(R.id.col2);

        int index = 0;
        for (String file : PUI_FILES) {
            final Button button = new Button(this);
            button.setId(button.hashCode());
            button.setText(file.replace("pui_", ""));
            button.setTag(file);

            addMenu((index % 2 == 0) ? mCol1 : mCol2, button, PUIActivity.class);
            index++;
        }

    }
}

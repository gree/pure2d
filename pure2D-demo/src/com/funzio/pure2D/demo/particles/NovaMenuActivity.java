/**
 * 
 */
package com.funzio.pure2D.demo.particles;

import java.io.IOException;

import android.widget.Button;

import com.funzio.pure2D.demo.R;
import com.funzio.pure2D.demo.activities.MenuActivity;

/**
 * @author long
 */
public class NovaMenuActivity extends MenuActivity {

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.demo.activities.MenuActivity#getLayout()
     */
    @Override
    protected int getLayout() {
        return R.layout.nova_menu;
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.demo.activities.MenuActivity#createMenus()
     */
    @Override
    protected void createMenus() {
        try {
            String[] files = getAssets().list("nova");
            for (String file : files) {
                if (file.contains(".json") && !file.contains("_template")) {
                    Button button = new Button(this);
                    button.setId(button.hashCode());
                    button.setText(file);
                    addMenu(button, NovaActivity.class);
                }
            }
        } catch (IOException e) {

        }

    }

}

/**
 * 
 */
package com.funzio.pure2D.demo.particles;

import java.io.IOException;

import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;

import com.funzio.pure2D.demo.R;
import com.funzio.pure2D.demo.activities.MenuActivity;

/**
 * @author long
 */
public class NovaMenuActivity extends MenuActivity {

    private LinearLayout mCol1;
    private LinearLayout mCol2;

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
        mCol1 = (LinearLayout) mLayout.findViewById(R.id.col1);
        mCol2 = (LinearLayout) mLayout.findViewById(R.id.col2);

        try {
            String[] files = getAssets().list("nova");
            int index = 0;
            for (String file : files) {
                if (file.contains(".json") && file.indexOf("_") != 0) {
                    Button button = new Button(this);
                    button.setId(button.hashCode());
                    button.setText(file.split("\\.")[0]);
                    button.setTag(file);

                    addMenu((index % 2 == 0) ? mCol1 : mCol2, button, NovaActivity.class);
                    index++;
                }
            }
        } catch (IOException e) {
            Log.e("NovaMenuActivity", "Loading Error!", e);
        }

    }
}

/*******************************************************************************
 * Copyright (C) 2012-2014 GREE, Inc.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/
/**
 * 
 */
package com.funzio.pure2D.demo.particles;

import java.io.IOException;

import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;

import com.funzio.pure2D.demo.activities.MenuActivity;
import com.longo.pure2D.demo.R;

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

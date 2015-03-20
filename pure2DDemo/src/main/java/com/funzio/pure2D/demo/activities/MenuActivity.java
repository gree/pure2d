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
package com.funzio.pure2D.demo.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;

import com.longo.pure2D.demo.R;

public abstract class MenuActivity extends Activity implements View.OnClickListener {
    public static final String EXTRA_TAG = "tag";

    protected SparseArray<Class<? extends Activity>> mMenuMap;
    protected LinearLayout mLayout;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getLayout());
        mLayout = (LinearLayout) findViewById(R.id.menu_layout);

        getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN, LayoutParams.FLAG_FULLSCREEN);

        mMenuMap = new SparseArray<Class<? extends Activity>>();

        createMenus();
    }

    public void onClick(final View view) {
        onClickButton(view);
    }

    public void onClickButton(final View view) {
        Class<? extends Activity> activityClass = mMenuMap.get(view.getId());
        if (activityClass != null) {
            Intent intent = new Intent(this, activityClass);
            intent.putExtra(EXTRA_TAG, (String) ((Button) view).getTag());
            startActivity(intent);
        }
    }

    protected void addMenu(final int view, final Class<? extends Activity> activityClass) {
        mMenuMap.put(view, activityClass);
    }

    protected void addMenu(final View button, final Class<? extends Activity> activityClass) {
        button.setOnClickListener(this);
        mLayout.addView(button);

        mMenuMap.put(button.getId(), activityClass);
    }

    protected void addMenu(final LinearLayout container, final View button, final Class<? extends Activity> activityClass) {
        button.setOnClickListener(this);
        container.addView(button);

        mMenuMap.put(button.getId(), activityClass);
    }

    abstract protected int getLayout();

    abstract protected void createMenus();
}

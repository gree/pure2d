package com.funzio.pure2D.demo.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.view.WindowManager.LayoutParams;

public abstract class MenuActivity extends Activity {

    protected SparseArray<Class<? extends Activity>> mMenuMap;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getLayout());

        getWindow().setFlags(LayoutParams.FLAG_FULLSCREEN, LayoutParams.FLAG_FULLSCREEN);

        mMenuMap = new SparseArray<Class<? extends Activity>>();

        createMenus();
    }

    public void onClickButton(final View view) {
        Class<? extends Activity> activityClass = mMenuMap.get(view.getId());
        if (activityClass != null) {
            startActivity(new Intent(this, activityClass));
        }
    }

    protected void addMenu(final int view, final Class<? extends Activity> activityClass) {
        mMenuMap.put(view, activityClass);
    }

    abstract protected int getLayout();

    abstract protected void createMenus();
}

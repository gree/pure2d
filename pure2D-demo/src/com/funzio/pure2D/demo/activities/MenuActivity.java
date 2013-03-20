package com.funzio.pure2D.demo.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;

import com.funzio.pure2D.demo.R;

public abstract class MenuActivity extends Activity implements View.OnClickListener {

    protected SparseArray<Class<? extends Activity>> mMenuMap;
    protected LinearLayout mLayout;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getLayout());
        mLayout = (LinearLayout) findViewById(R.id.nova_menu_layout);

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
            intent.putExtra("text", ((Button) view).getText());
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

    abstract protected int getLayout();

    abstract protected void createMenus();
}

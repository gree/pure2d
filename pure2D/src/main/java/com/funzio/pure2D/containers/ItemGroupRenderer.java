package com.funzio.pure2D.containers;

import android.view.MotionEvent;

import com.funzio.pure2D.Touchable;

/**
 * Created by longngo on 3/25/15.
 */
public class ItemGroupRenderer extends DisplayGroup implements ItemRenderer, Touchable {
    protected List mList;
    protected int mDataIndex = -1;
    protected Object mData;

    public ItemGroupRenderer() {
        super();
    }

    @Override
    public boolean setData(int index, Object data) {
        // diff check
        if (mDataIndex != index || mData != data) {
            mDataIndex = index;
            mData = data;

            return true;
        }

        return false;
    }

    @Override
    public int getDataIndex() {
        return mDataIndex;
    }

    @Override
    public Object getData() {
        return mData;
    }

    @Override
    public void onAdded(Container container) {
        super.onAdded(container);

        if (container instanceof List) {
            mList = (List) container;
        }
    }

    @Override
    public void onRemoved() {
        super.onRemoved();

        mList = null;
        mDataIndex = -1;
        mData = null;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (super.onTouchEvent(event)) {
            if (mList != null) {
                // event
                mList.onItemTouch(event, this);
            }
            return true;
        }

        return false;
    }
}

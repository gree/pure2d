package com.funzio.pure2D.containers.renderers;

import android.view.MotionEvent;

import com.funzio.pure2D.Touchable;
import com.funzio.pure2D.containers.Container;
import com.funzio.pure2D.containers.List;
import com.funzio.pure2D.uni.UniGroup;

/**
 * Created by longngo on 3/25/15.
 */
public class UniGroupItemRenderer<T> extends UniGroup implements ItemRenderer<T>, Touchable {
    protected List mList;
    protected int mDataIndex = -1;
    protected T mData;

    public UniGroupItemRenderer() {
        super();
    }

    @Override
    public boolean setData(int index, T data) {
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
    public T getData() {
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

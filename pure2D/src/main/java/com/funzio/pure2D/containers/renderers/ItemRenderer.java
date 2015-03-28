package com.funzio.pure2D.containers.renderers;

import com.funzio.pure2D.Displayable;

/**
 * Created by longngo on 3/24/15.
 */
public interface ItemRenderer<T> extends Displayable {
    public boolean setData(int index, T data);

    public int getDataIndex();

    public T getData();
}

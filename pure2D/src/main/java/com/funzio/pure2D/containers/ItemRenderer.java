package com.funzio.pure2D.containers;

import com.funzio.pure2D.Displayable;

/**
 * Created by longngo on 3/24/15.
 */
public interface ItemRenderer extends Displayable {
    public boolean setData(int index, Object data);

    public int getDataIndex();

    public Object getData();
}

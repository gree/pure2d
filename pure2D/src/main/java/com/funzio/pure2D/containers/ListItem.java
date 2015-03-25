package com.funzio.pure2D.containers;

import com.funzio.pure2D.Displayable;

/**
 * Created by longngo on 3/24/15.
 */
public interface ListItem extends Displayable {

    public void setData(Object data);

    public Object getData();
}

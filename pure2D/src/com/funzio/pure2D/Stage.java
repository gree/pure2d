/**
 * 
 */
package com.funzio.pure2D;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Handler;

/**
 * @author long
 */
public interface Stage {
    public Rect getRect();

    public Resources getResources();

    public AssetManager getAssets();

    public Handler getHandler();

    public void queueEvent(Runnable r);

    public void setFixedSize(final int width, final int height);

    public Point getFixedSize();

    public PointF getFixedScale();
}

/**
 * 
 */
package com.funzio.pure2D;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Rect;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

/**
 * @author long
 */
public class BaseStage extends GLSurfaceView implements Stage {
    public final static String TAG = BaseStage.class.getSimpleName();

    private Scene mScene;
    private Rect mRect;

    public BaseStage(final Context context) {
        super(context);
    }

    public BaseStage(final Context context, final AttributeSet attributes) {
        super(context, attributes);
    }

    public void setScene(final Scene scene) {
        mScene = scene;
        mScene.setStage(this);

        // set the renderer which is the scene
        setRenderer(scene);
    }

    public Scene getScene() {
        return mScene;
    }

    public Rect getRect() {
        if (mRect == null) {
            mRect = new Rect();
        }
        if (mRect.width() == 0) {
            getGlobalVisibleRect(mRect);
        }

        return mRect;
    }

    public AssetManager getAssets() {
        return getContext().getAssets();
    }
}

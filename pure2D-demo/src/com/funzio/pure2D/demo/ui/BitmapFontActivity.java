package com.funzio.pure2D.demo.ui;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;

import com.funzio.pure2D.Scene;
import com.funzio.pure2D.demo.R;
import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.shapes.Sprite;
import com.funzio.pure2D.text.BitmapFont;
import com.funzio.pure2D.text.Characters;
import com.funzio.pure2D.text.TextOptions;

public class BitmapFontActivity extends StageActivity {
    private Sprite mAtlasSprite;
    private BitmapFont mBitmapFont;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // need to get the GL reference first
        mScene.setListener(new Scene.Listener() {

            @Override
            public void onSurfaceCreated(final GL10 gl) {

                // load the textures
                loadTexture();

                mAtlasSprite = new Sprite();
                mAtlasSprite.setTexture(mBitmapFont.getTexture());
                mScene.addChild(mAtlasSprite);
            }
        });
    }

    /*
     * (non-Javadoc)
     * @see com.funzio.pure2D.samples.activities.StageActivity#getLayout()
     */
    @Override
    protected int getLayout() {
        return R.layout.stage_atlas;
    }

    private void loadTexture() {
        final TextOptions options = TextOptions.getDefault();
        options.inTextPaint.setColor(Color.DKGRAY);
        options.inTextPaint.setTextSize(40);
        // options.inTextPaint.setTypeface(FontManager.getInstance().getFont("HelveticaRoundedLTStd-Bd.otf"));
        options.inPaddingX = options.inPaddingY = 2;
        options.inStrokePaint = new Paint(options.inTextPaint);
        options.inStrokePaint.setStrokeWidth(options.inTextPaint.getTextSize() / 7);
        options.inStrokePaint.setStyle(Style.STROKE);
        options.inStrokePaint.setColor(Color.CYAN);

        mBitmapFont = new BitmapFont(Characters.BASIC_SET, options);
        mBitmapFont.load(mScene.getGLState());
    }

    // private Clip addObject(final float x, final float y) {
    // // create object
    // final Clip obj = new Clip();
    // obj.setTexture(mCacheAtlas.getTexture());
    // obj.setAtlasFrameSet(mCacheAtlas.getMasterFrameSet());
    // // obj.setFps(30);
    //
    // // random positions
    // obj.setPosition(x, y);
    //
    // // add to scene
    // mScene.addChild(obj);
    //
    // return obj;
    // }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        final int action = event.getAction();

        if (action == MotionEvent.ACTION_DOWN) {
            mStage.queueEvent(new Runnable() {
                @Override
                public void run() {
                    int len = event.getPointerCount();
                    for (int i = 0; i < len; i++) {
                        // addObject(event.getX(i), mDisplaySize.y - event.getY(i));
                    }
                }
            });
        }

        return true;
    }

    public void onClickAtlas(final View view) {
        if (view.getId() == R.id.cb_show_atlas) {
            if (mAtlasSprite != null) {
                mAtlasSprite.setVisible(((CheckBox) findViewById(R.id.cb_show_atlas)).isChecked());
            }
        }
    }
}

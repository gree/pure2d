package com.funzio.pure2D.demo.textures;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;

import com.funzio.pure2D.Scene;
import com.funzio.pure2D.atlas.Atlas;
import com.funzio.pure2D.atlas.ImageSequenceBufferAtlas;
import com.funzio.pure2D.demo.R;
import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.shapes.Clip;
import com.funzio.pure2D.shapes.Sprite;

public class ImageSequenceActivity extends StageActivity {
    private static final String IMAGE_DIR = "mayan/symbols/majors/priest";
    // private static final String SDCARD_IMAGE_DIR = Environment.getExternalStorageDirectory() + "/funzio/casino/FarmRiches/images/symbols/majors/bonus";

    private Texture mTexture;
    private ImageSequenceBufferAtlas mAtlas;

    private Sprite mAtlasSprite;
    private Atlas.Listener mAtlasListener = new Atlas.Listener() {

        @Override
        public void onAtlasLoad(final Atlas atlas) {
            // get the generated texture
            mTexture = mAtlas.getTexture();

            // create the big sprite for fun
            mAtlasSprite = new Sprite();
            mAtlasSprite.setTexture(mTexture);
            float scale = mDisplaySize.x / mTexture.getSize().x;
            mAtlasSprite.setScale(scale);
            mScene.addChild(mAtlasSprite);
        }
    };

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // need to get the GL reference first
        mScene.setListener(new Scene.Listener() {

            @Override
            public void onSurfaceCreated(final GLState glState, final boolean firstTime) {
                if (firstTime) {
                    // turn on debug
                    // GLDebugHelper.wrap(mGL, GLDebugHelper.CONFIG_CHECK_GL_ERROR | GLDebugHelper.CONFIG_LOG_ARGUMENT_NAMES | GLDebugHelper.ERROR_WRONG_THREAD, new PrintWriter(System.out));

                    createAtlas();
                    addObject(mDisplaySizeDiv2.x, mDisplaySizeDiv2.y);
                }
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

    private void createAtlas() {
        mAtlas = new ImageSequenceBufferAtlas(mScene.getGLState());
        mAtlas.setListener(mAtlasListener);
        mAtlas.loadDir(getAssets(), IMAGE_DIR, null); // load of assets
        // mAtlas.loadDirAsync(SDCARD_IMAGE_DIR, null); // load of sdcard
    }

    private void addObject(final float x, final float y) {
        if (mTexture == null) {
            return;
        }

        // Rectangular temp = new Rectangular();
        // temp.setSize(30, 30);
        // temp.setColor(new GLColor(1f, 1f, 1f, 1f));
        // mScene.addChild(temp);

        // create object
        // Sprite obj = new Sprite();
        // obj.setTexture(mTexture);
        // obj.setScale(0.5f, 0.5f);
        Clip obj = new Clip(mAtlas.getMasterFrameSet());

        // center origin
        obj.setOriginAtCenter();

        // random position
        obj.setPosition(x, y);

        // add to scene
        mScene.addChild(obj);
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        final int action = event.getAction();

        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_MOVE) {
            mStage.queueEvent(new Runnable() {

                @Override
                public void run() {
                    int len = event.getPointerCount();
                    for (int i = 0; i < len; i++) {
                        addObject(event.getX(i), mDisplaySize.y - event.getY(i));
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

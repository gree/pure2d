package com.funzio.pure2D.demo.animations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.microedition.khronos.opengles.GL10;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;

import com.funzio.pure2D.Scene;
import com.funzio.pure2D.animation.skeleton.AniFile;
import com.funzio.pure2D.animation.skeleton.AniSkeleton;
import com.funzio.pure2D.atlas.CacheAtlas;
import com.funzio.pure2D.demo.R;
import com.funzio.pure2D.demo.activities.StageActivity;
import com.funzio.pure2D.gl.gl10.textures.Texture;
import com.funzio.pure2D.shapes.Clip;
import com.funzio.pure2D.shapes.Sprite;

public class SkeletonCacheActivity extends StageActivity {
    private static final String ANI_FILE = "ani/Avatar_Walk_SE_Male_Caucasian_ArmoredGreenCamo_Top_FARC_Green_Bottom_GrayCamo.ani";
    private AniFile mAniFile;
    private Map<String, Texture> mTextures = new HashMap<String, Texture>();
    private CacheAtlas mCacheAtlas;
    private Sprite mAtlasSprite;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // need to get the GL reference first
        mScene.setListener(new Scene.Listener() {

            @Override
            public void onSurfaceCreated(final GL10 gl) {

                // load the textures
                loadTexture();

                // generate a lot of squares
                // addObject(mDisplaySizeDiv2.x, mDisplaySizeDiv2.y);

                final AniSkeleton skeleton = new AniSkeleton(mAniFile);
                // skeleton.setDebugging(true);
                mCacheAtlas = new CacheAtlas(mScene.getGLState(), skeleton, 512);

                mAtlasSprite = new Sprite();
                mAtlasSprite.setTexture(mCacheAtlas.getTexture());
                mScene.addChild(mAtlasSprite);
            }
        });

        mAniFile = new AniFile(getAssets(), ANI_FILE);
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
        final List<String> images = mAniFile.mRequiredImages;
        for (String image : images) {
            mTextures.put(image, mScene.getTextureManager().createAssetTexture(image, null));
            break; // XXX just display naked body
        }

        // send to the file
        mAniFile.setTextures(mTextures);

    }

    private Clip addObject(final float x, final float y) {
        // create object
        final Clip obj = new Clip();
        obj.setTexture(mCacheAtlas.getTexture());
        obj.setAtlasFrameSet(mCacheAtlas.getMasterFrameSet());
        // obj.setFps(30);

        // random positions
        obj.setPosition(x, y);

        // add to scene
        mScene.addChild(obj);

        return obj;
    }

    @Override
    public boolean onTouch(final View v, final MotionEvent event) {
        final int action = event.getAction();

        if (action == MotionEvent.ACTION_DOWN) {
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

/**
 * 
 */
package com.funzio.pure2D.ui;

import java.util.List;

import android.graphics.PointF;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;

import com.funzio.pure2D.containers.DisplayGroup;
import com.funzio.pure2D.particles.nova.NovaEmitter;
import com.funzio.pure2D.particles.nova.NovaFactory;

/**
 * @author long.ngo
 */
public class NovaGroup extends DisplayGroup {
    protected static final String TAG = NovaGroup.class.getSimpleName();

    protected static final String ATT_SOURCE = "source";
    protected static final String ATT_ASYNC = "async";
    protected static final String ATT_AUTO_PLAY = "autoStart";

    protected NovaFactory mNovaFactory;
    protected boolean mNovaLoaded = false;
    protected boolean mAutoStart = true;

    public NovaGroup() {
        super();
    }

    @Override
    public boolean update(final int deltaTime) {
        // async support and diff check
        if (!mNovaLoaded && mNovaFactory != null && mNovaFactory.getNovaVO() != null) {
            mNovaLoaded = true; // flag now

            if (mAutoStart) {
                addEmiters(null);
            }
        }

        return super.update(deltaTime);
    }

    @Override
    public void setXMLAttributes(final XmlPullParser xmlParser, final UIManager manager) {
        super.setXMLAttributes(xmlParser, manager);

        final String autoPlay = xmlParser.getAttributeValue(null, ATT_AUTO_PLAY);
        if (autoPlay != null) {
            mAutoStart = Boolean.valueOf(autoPlay);
        }

        final String source = xmlParser.getAttributeValue(null, ATT_SOURCE);
        if (source != null) {
            final String async = xmlParser.getAttributeValue(null, ATT_ASYNC);
            mNovaFactory = manager.getTextureManager().getUriNova(source, async != null ? Boolean.valueOf(async) : UIConfig.DEFAULT_ASYNC); // async by default
            if (mNovaFactory.getNovaVO() != null) {
                mNovaLoaded = true; // flag

                if (mAutoStart) {
                    addEmiters(null);
                }
            } else {
                mNovaLoaded = false;
            }
        }
    }

    /**
     * Create and add the emitters to a specific container
     * 
     * @param x
     * @param y
     * @param params
     */
    protected List<NovaEmitter> addEmiters(final PointF position, final Object... params) {
        Log.v(TAG, "addEmitters(): " + params);

        final List<NovaEmitter> emitters = mNovaFactory.createEmitters(position, params);
        // null check first
        if (emitters == null) {
            Log.e(TAG, "Emiters not created!", new Exception());
            return null;
        }

        // queue to add later
        final Runnable runnable = new Runnable() {

            @Override
            public void run() {
                final int size = emitters.size();
                for (int i = 0; i < size; i++) {
                    addChild(emitters.get(i));
                }
            }
        };
        if (!queueEvent(runnable)) {
            runnable.run();
        }

        return emitters;
    }

    public boolean isAutoStart() {
        return mAutoStart;
    }

    public void setAutoStart(final boolean autoPlay) {
        mAutoStart = autoPlay;
    }

    public boolean start() {
        if (mNovaLoaded) {
            addEmiters(null);

            return true;
        }

        return false;
    }
}

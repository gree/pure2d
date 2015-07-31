package com.funzio.pure2D.uni;

import java.util.ArrayList;

//import jp.gree.hclib.activities.map.BaseMapViewRenderer;

import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;

import org.xmlpull.v1.XmlPullParser;

import com.funzio.pure2D.Maskable;
import com.funzio.pure2D.Parentable;
import com.funzio.pure2D.Scene;
import com.funzio.pure2D.StackableObject;
import com.funzio.pure2D.animators.Manipulator;
import com.funzio.pure2D.containers.Container;
import com.funzio.pure2D.gl.GLColor;
import com.funzio.pure2D.gl.gl10.BlendFunc;
import com.funzio.pure2D.gl.gl10.GLState;
import com.funzio.pure2D.ui.UIConstraint;
import com.funzio.pure2D.ui.UIManager;

public abstract class FastUniGroupDebugger extends FastUniGroup {
    private final static String TAG = FastUniGroupDebugger.class.getSimpleName();

    public static long glThreadId;

    @Override
    public boolean addChild(final StackableObject child) {
        checkIsGlThread("addChild1");
        return super.addChild(child);
    }

    @Override
    public boolean addChild(final StackableObject child, final int index) {
        checkIsGlThread("addChild2");
        return super.addChild(child, index);
    }

    @Override
    public boolean addManipulator(final Manipulator manipulator) {
        checkIsGlThread("addManipulator");
        return super.addManipulator(manipulator);
    }

    @Override
    public void clearCache() {
        checkIsGlThread("clearCache");
        super.clearCache();
    }

    @Override
    public void dispose() {
        checkIsGlThread("dispose");
        super.dispose();
    }

    @Override
    public boolean draw(final GLState glState) {
        checkIsGlThread("draw");
        return super.draw(glState);
    }

    @Override
    public BlendFunc getBlendFunc() {
        checkIsGlThread("getBlendFunc");
        return super.getBlendFunc();
    }

    @Override
    public int getCachePolicy() {
        checkIsGlThread("getCachePolicy");
        return super.getCachePolicy();
    }

    @Override
    public int getCacheProjection() {
        checkIsGlThread("getCacheProjection");
        return super.getCacheProjection();
    }

    @Override
    public StackableObject getChildAt(final int index) {
        checkIsGlThread("getChildAt");
        return super.getChildAt(index);
    }

    @Override
    public StackableObject getChildById(final String id) {
        checkIsGlThread("getChildById");
        return super.getChildById(id);
    }

    @Override
    public int getChildIndex(final StackableObject child) {
        checkIsGlThread("getChildIndex");
        return super.getChildIndex(child);
    }

    @Override
    public PointF getGlobalPosition() {
        checkIsGlThread("getGlobalPosition");
        return super.getGlobalPosition();
    }

    @Override
    public GLColor getInheritedColor() {
        // checkIsGlThread("getInheritedColor");
        return super.getInheritedColor();
    }

    @Override
    public Manipulator getManipulator(final int index) {
        checkIsGlThread("getManipulator");
        return super.getManipulator(index);
    }

    //    @Override
    //    public int getNumChildren() {
    //        checkIsGlThread("getNumChildren");
    //        return super.getNumChildren();
    //    }

    @Override
    public int getNumGrandChildren() {
        checkIsGlThread("getNumGrandChildren");
        return super.getNumGrandChildren();
    }

    @Override
    public int getNumManipulators() {
        checkIsGlThread("getNumManipulators");
        return super.getNumManipulators();
    }

    @Override
    public String getObjectTree(final String prefix) {
        checkIsGlThread("getObjectTree");
        return super.getObjectTree(prefix);
    }

    /*
    @Override
    public Parentable getParent() {
        checkIsGlThread("getParent");
        return super.getParent();
    }
    */

    @Override
    public PointF getPivot() {
        checkIsGlThread("getPivot");
        return super.getPivot();
    }

    @Override
    public PointF getSkew() {
        // checkIsGlThread("getSkew");
        return super.getSkew();
    }

    @Override
    public UIConstraint getUIConstraint() {
        checkIsGlThread("getUIConstraint");
        return super.getUIConstraint();
    }

    /*
    @Override
    public void invalidate(final int flags) {
        // checkIsGlThread("invalidate");
        super.invalidate(flags);
    }
    */

    @Override
    public boolean isCacheEnabled() {
        checkIsGlThread("isCacheEnabled");
        return super.isCacheEnabled();
    }

    @Override
    public boolean isClippingEnabled() {
        checkIsGlThread("isClippingEnabled");
        return super.isClippingEnabled();
    }

    @Override
    public boolean isModal() {
        checkIsGlThread("isModal");
        return super.isModal();
    }

    @Override
    public boolean isOriginAtCenter() {
        checkIsGlThread("isOriginAtCenter");
        return super.isOriginAtCenter();
    }

    @Override
    public boolean isTouchable() {
        checkIsGlThread("isTouchable");
        return super.isTouchable();
    }

    @Override
    public boolean isWrapContenHeight() {
        checkIsGlThread("isWrapContenHeight");
        return super.isWrapContenHeight();
    }

    @Override
    public boolean isWrapContentWidth() {
        checkIsGlThread("isWrapContentWidth");
        return super.isWrapContentWidth();
    }

    @Override
    public void move(final float dx, final float dy) {
        checkIsGlThread("move");
        super.move(dx, dy);
    }

    @Override
    public void moveTo(final float x, final float y) {
        checkIsGlThread("moveTo");
        super.moveTo(x, y);
    }

    @Override
    public void onAdded(final Container container) {
        checkIsGlThread("onAdded");
        super.onAdded(container);
    }

    @Override
    public void onAddedToScene(final Scene scene) {
        checkIsGlThread("onAddedToScene");
        super.onAddedToScene(scene);
    }

    @Override
    public void onCreateChildren(final UIManager manager) {
        checkIsGlThread("onCreateChildren");
        super.onCreateChildren(manager);
    }

    @Override
    public void onRemoved() {
        checkIsGlThread("onRemoved");
        super.onRemoved();
    }

    @Override
    public void onRemovedFromScene() {
        checkIsGlThread("onRemovedFromScene");
        super.onRemovedFromScene();
    }

    @Override
    public boolean onTouchEvent(final MotionEvent event) {
        checkIsGlThread("onTouchEvent");
        return super.onTouchEvent(event);
    }

    @Override
    public void removeAllChildren() {
        checkIsGlThread("removeAllChildren");
        super.removeAllChildren();
    }

    @Override
    public int removeAllManipulators() {
        checkIsGlThread("removeAllManipulators");
        return super.removeAllManipulators();
    }

    @Override
    public boolean removeChild(final StackableObject child) {
        checkIsGlThread("removeChild1");
        return super.removeChild(child);
    }

    @Override
    public boolean removeChild(final int index) {
        checkIsGlThread("removeChild2");
        return super.removeChild(index);
    }

    @Override
    public boolean removeFromParent() {
        checkIsGlThread("removeFromParent");
        return super.removeFromParent();
    }

    @Override
    public boolean removeManipulator(final Manipulator manipulator) {
        checkIsGlThread("removeManipulator");
        return super.removeManipulator(manipulator);
    }

    @Override
    public void rotate(final float degreeDelta) {
        checkIsGlThread("rotate");
        super.rotate(degreeDelta);
    }

    @Override
    public boolean sendChildToBottom(final StackableObject child) {
        checkIsGlThread("sendChildToBottom");
        return super.sendChildToBottom(child);
    }

    @Override
    public boolean sendChildToTop(final StackableObject child) {
        checkIsGlThread("sendChildToTop");
        return super.sendChildToTop(child);
    }

    @Override
    public void setAlive(final boolean value) {
        checkIsGlThread("setAlive");
        super.setAlive(value);
    }

    @Override
    public void setAlpha(final float alpha) {
        // checkIsGlThread("setAlpha");
        super.setAlpha(alpha);
    }

    @Override
    public void setAlphaTestEnabled(final boolean alphaTestEnabled) {
        // checkIsGlThread("setAlphaTestEnabled");
        super.setAlphaTestEnabled(alphaTestEnabled);
    }

    @Override
    public void setBlendFunc(final BlendFunc blendFunc) {
        checkIsGlThread("setBlendFunc");
        super.setBlendFunc(blendFunc);
    }

    @Override
    public void setCacheEnabled(final boolean cacheEnabled) {
        checkIsGlThread("setCacheEnabled");
        super.setCacheEnabled(cacheEnabled);
    }

    @Override
    public void setCachePolicy(final int cachePolicy) {
        checkIsGlThread("setCachePolicy");
        super.setCachePolicy(cachePolicy);
    }

    @Override
    public void setCacheProjection(final int cacheProjection) {
        checkIsGlThread("setCacheProjection");
        super.setCacheProjection(cacheProjection);
    }

    @Override
    public void setClippingEnabled(final boolean clippingEnabled) {
        checkIsGlThread("setClippingEnabled");
        super.setClippingEnabled(clippingEnabled);
    }

    @Override
    public void setColor(final GLColor color) {
        // checkIsGlThread("setColor");
        super.setColor(color);
    }

    @Override
    public void setFps(final int fps) {
        // checkIsGlThread("setFps");
        super.setFps(fps);
    }

    @Override
    public void setMask(final Maskable mask) {
        checkIsGlThread("setMask");
        super.setMask(mask);
    }

    @Override
    public void setModal(final boolean modal) {
        checkIsGlThread("setModal");
        super.setModal(modal);
    }

    @Override
    public void setOrigin(final float x, final float y) {
        checkIsGlThread("setOrigin");
        super.setOrigin(x, y);
    }

    @Override
    public void setOriginAtCenter() {
        checkIsGlThread("setOriginAtCenter");
        super.setOriginAtCenter();
    }

    @Override
    public void setPivot(final float x, final float y) {
        checkIsGlThread("setPivot");
        super.setPivot(x, y);
    }

    @Override
    public void setPivot(final PointF pivot) {
        checkIsGlThread("setPivot");
        super.setPivot(pivot);
    }

    @Override
    public void setPivotAtCenter() {
        checkIsGlThread("setPivotAtCenter");
        super.setPivotAtCenter();
    }

    @Override
    public void setPosition(final float x, final float y) {
        // checkIsGlThread("setPosition");
        super.setPosition(x, y);
    }

    @Override
    public void setRotation(final float degree) {
        checkIsGlThread("setRotation");
        super.setRotation(degree);
    }

    @Override
    public void setRotationVector(final float x, final float y, final float z) {
        checkIsGlThread("setRotationVector");
        super.setRotationVector(x, y, z);
    }

    @Override
    public void setScale(final float scale) {
        // checkIsGlThread("setScale");
        super.setScale(scale);
    }

    @Override
    public void setScale(final float sx, final float sy) {
        // checkIsGlThread("setScale");
        super.setScale(sx, sy);
    }

    @Override
    public void setSize(final float w, final float h) {
        checkIsGlThread("setSize");
        super.setSize(w, h);
    }

    @Override
    public void setSkew(final float kx, final float ky) {
        // checkIsGlThread("setSkew");
        super.setSkew(kx, ky);
    }

    @Override
    public void setTouchable(final boolean touchable) {
        checkIsGlThread("setTouchable");
        super.setTouchable(touchable);
    }

    @Override
    public void setUIConstraint(final UIConstraint uiConstraint) {
        checkIsGlThread("setUIConstraint");
        super.setUIConstraint(uiConstraint);
    }

    @Override
    public void setVisible(final boolean value) {
        checkIsGlThread("setVisible");
        super.setVisible(value);
    }

    @Override
    public void setWrapContentHeight(final boolean wrapHeight) {
        checkIsGlThread("setWrapContentHeight");
        super.setWrapContentHeight(wrapHeight);
    }

    @Override
    public void setWrapContentWidth(final boolean wrapWidth) {
        checkIsGlThread("setWrapContentWidth");
        super.setWrapContentWidth(wrapWidth);
    }

    @Override
    public void setX(final float x) {
        checkIsGlThread("setX");
        super.setX(x);
    }

    @Override
    public void setXMLAttributes(final XmlPullParser xmlParser, final UIManager manager) {
        checkIsGlThread("setXMLAttributes");
        super.setXMLAttributes(xmlParser, manager);
    }

    @Override
    public void setY(final float y) {
        checkIsGlThread("setY");
        super.setY(y);
    }

    @Override
    public void setZ(final float z) {
        checkIsGlThread("setZ");
        super.setZ(z);
    }

    @Override
    public boolean shouldDraw(final RectF globalViewRect) {
        checkIsGlThread("shouldDraw");
        return super.shouldDraw(globalViewRect);
    }

    @Override
    public boolean swapChildren(final StackableObject child1, final StackableObject child2) {
        checkIsGlThread("swapChildren1");
        return super.swapChildren(child1, child2);
    }

    @Override
    public boolean swapChildren(final int index1, final int index2) {
        checkIsGlThread("swapChildren2");
        return super.swapChildren(index1, index2);
    }

    @Override
    public boolean update(final int deltaTime) {
        checkIsGlThread("update");
        return super.update(deltaTime);
    }

    @Override
    public RectF updateBounds() {
        checkIsGlThread("updateBounds");
        return super.updateBounds();
    }

    @Override
    protected void drawBounds(final GLState glState) {
        checkIsGlThread("drawBounds");
        super.drawBounds(glState);
    }

    @Override
    protected boolean drawChildren(final GLState glState) {
        checkIsGlThread("drawChildren");
        return super.drawChildren(glState);
    }

    @Override
    protected void drawEnd(final GLState glState) {
        checkIsGlThread("drawEnd");
        super.drawEnd(glState);
    }

    @Override
    protected void drawStart(final GLState glState) {
        checkIsGlThread("drawStart");
        super.drawStart(glState);
    }

    @Override
    protected void drawWireframe(final GLState glState) {
        checkIsGlThread("drawWireframe");
        super.drawWireframe(glState);
    }

    @Override
    protected PointF getSceneSize() {
        checkIsGlThread("getSceneSize");
        return super.getSceneSize();
    }

    @Override
    protected void initCache(final GLState glState, final PointF size) {
        checkIsGlThread("initCache");
        super.initCache(glState, size);
    }

    @Override
    protected boolean isChildInBounds(final StackableObject child) {
        checkIsGlThread("isChildInBounds");
        return super.isChildInBounds(child);
    }

    @Override
    protected void onAddedChild(final StackableObject child) {
        checkIsGlThread("onAddedChild");
        super.onAddedChild(child);
    }

    @Override
    protected void onPreConcatParentMatrix() {
        checkIsGlThread("onPreConcatParentMatrix");
        super.onPreConcatParentMatrix();
    }

    @Override
    protected void onRemovedChild(final StackableObject child) {
        checkIsGlThread("onRemovedChild");
        super.onRemovedChild(child);
    }

    @Override
    protected void updateChildren(final int deltaTime) {
        checkIsGlThread("updateChildren");
        super.updateChildren(deltaTime);
    }

    private void checkIsGlThread(final String log) {
        if (glThreadId != Thread.currentThread().getId()) {
            final String error = "Error : This is not the GlThread at " + log + " in " + getClass().getCanonicalName();
            Log.e(FastUniGroupDebugger.TAG, error, new Exception());
        }
    }
}

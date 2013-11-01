/**
 * 
 */
package com.funzio.pure2D.demo.pui;

import com.funzio.pure2D.DisplayObject;
import com.funzio.pure2D.containers.DisplayGroup;
import com.funzio.pure2D.ui.Button;
import com.funzio.pure2D.ui.TouchListener;
import com.funzio.pure2D.ui.UIManager;

/**
 * @author long.ngo
 */
public class MyDialog extends DisplayGroup {

    private Button mButtonCancel;
    private Button mButtonOkay;

    public MyDialog() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public void onCreateChildren(final UIManager manager) {
        super.onCreateChildren(manager);

        mButtonCancel = (Button) getChildById("btn_cancel");
        mButtonCancel.setTouchListener(new TouchListener() {

            @Override
            public void onTouchUp(final DisplayObject obj, final boolean hit) {
                if (hit) {
                    queueEvent(new Runnable() {

                        @Override
                        public void run() {
                            removeFromParent();
                        }
                    });
                }
            }

            @Override
            public void onTouchDown(final DisplayObject obj) {
                // TODO Auto-generated method stub

            }
        });

        mButtonOkay = (Button) getChildById("btn_okay");
        mButtonOkay.setTouchListener(new TouchListener() {

            @Override
            public void onTouchUp(final DisplayObject obj, final boolean hit) {
                if (hit) {
                    queueEvent(new Runnable() {

                        @Override
                        public void run() {
                            removeFromParent();
                        }
                    });
                }
            }

            @Override
            public void onTouchDown(final DisplayObject obj) {
                // TODO Auto-generated method stub

            }
        });
    }

}

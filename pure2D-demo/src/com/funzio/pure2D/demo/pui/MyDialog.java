/*******************************************************************************
 * Copyright (C) 2012-2014 GREE, Inc.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/
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

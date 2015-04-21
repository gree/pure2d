/**
 * ****************************************************************************
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
 * ****************************************************************************
 */
/**
 *
 */
package com.funzio.pure2D.ui;

import android.util.Log;

import com.funzio.pure2D.containers.Container;
import com.funzio.pure2D.gl.GLColor;

import java.util.ArrayList;

/**
 * @author long.ngo
 */
public class PageStacker implements Pageable.TransitionListener {
    protected static final String TAG = PageStacker.class.getSimpleName();
    protected static final GLColor DIMMED_COLOR = new GLColor(0xFF666666);

    protected Container mContainer;
    protected ArrayList<Pageable> mPages = new ArrayList<Pageable>();
    protected int mNumPages = 0;

    protected Pageable mCurrentPage;
    protected GLColor mDimmedColor = DIMMED_COLOR;
    protected Pageable.TransitionListener mTransitionListener;

    private ArrayList<GLColor> mDimmedStack = new ArrayList<GLColor>();

    public PageStacker(final Container container) {
        mContainer = container;
    }

    public Pageable getCurrentPage() {
        return mCurrentPage;
    }

    public boolean pushPage(final Pageable page) {
        if (mPages.add(page)) {
            // event
            page.setTransitionListener(this);

            final Pageable previousPage = mCurrentPage;
            mCurrentPage = page;

            // slide in the new page, on GL Thread
            mContainer.queueEvent(new Runnable() {

                @Override
                public void run() {
                    if (!page.isPageFloating()) {
                        // slide out the previous page
                        if (previousPage != null) {
                            previousPage.transitionOut(true);
                        }
                    } else {
                        if (previousPage != null && page.isModal()) {
                            // dim
                            mDimmedStack.add(previousPage.getColor());
                            previousPage.setColor(mDimmedColor);
                        }
                    }

                    // slide in the current page
                    mContainer.addChild(page);
                    page.transitionIn(true);
                }
            });

            mNumPages++;

            return true;
        }

        return false;
    }

    protected Pageable dismissPage() {
        final Pageable currentPage = popPage();
        if (currentPage != null) {
            currentPage.onDismiss();
        }

        return currentPage;
    }

    /**
     * Pop the last page
     * @return
     */
    public Pageable popPage() {
        if (mNumPages == 0) {
            return null;
        }

        // pop the current page
        final Pageable currentPage = mPages.remove(mNumPages - 1);
        if (--mNumPages > 0) {
            // slide in
            mCurrentPage = mPages.get(mNumPages - 1);
        }
        final Pageable previousPage = mCurrentPage;

        // slide in the new page, on GL Thread
        mContainer.queueEvent(new Runnable() {

            @Override
            public void run() {
                // slide out the current page
                if (currentPage != null) {
                    currentPage.transitionOut(false);
                }

                // slide in the previous page
                if (!currentPage.isPageFloating()) {
                    if (previousPage != null) {
                        mContainer.addChild(previousPage);
                        previousPage.transitionIn(false);
                    }
                } else {
                    if (previousPage != null && currentPage.isModal()) {
                        // undim
                        if (mDimmedStack.size() > 0) {
                            previousPage.setColor(mDimmedStack.remove(mDimmedStack.size() - 1));
                        }
                    }
                }
            }
        });

        return currentPage;
    }

    /**
     * Pop all the way to the specified page
     * @param page
     * @return
     */
    public int popPage(final Pageable page) {
        final int index = mPages.indexOf(page);
        if (index >= 0 && index < mNumPages) {
            final int diff = mNumPages - index;
            for (int i = 0; i < diff; i++) {
                popPage();
            }

            return diff;
        }

        return 0;
    }

    public int popToPage(final Pageable page) {
        final int index = mPages.indexOf(page);
        if (index >= 0 && index < mNumPages) {
            final int diff = mNumPages - index - 1;
            for (int i = 0; i < diff; i++) {
                popPage();
            }

            return diff;
        }

        return 0;
    }

    public int getNumPages() {
        return mNumPages;
    }

    public Container getContainer() {
        return mContainer;
    }

    // public void setContainer(final Container container) {
    // mContainer = container;
    // }


    public GLColor getDimmedColor() {
        return mDimmedColor;
    }

    public void setDimmedColor(final GLColor dimmedColor) {
        mDimmedColor = dimmedColor;
    }

    @Override
    public void onTransitionInComplete(final Pageable page) {
        Log.v(TAG, "onTransitionInComplete(): " + page);

        // forward callback
        if (mTransitionListener != null) {
            mTransitionListener.onTransitionInComplete(page);
        }
    }

    @Override
    public void onTransitionOutComplete(final Pageable page) {
        Log.v(TAG, "onTransitionOutComplete(): " + page);

        mContainer.queueEvent(new Runnable() {

            @Override
            public void run() {
                // now remove the page
                mContainer.removeChild(page);
            }
        });

        // forward callback
        if (mTransitionListener != null) {
            mTransitionListener.onTransitionOutComplete(page);
        }
    }

    public boolean onBackPressed() {
        if (mNumPages > 1) {

            if (mCurrentPage.onBackPressed()) {
                // let page completely take control
                return true;
            } else if (mCurrentPage.isDismissible()) {
                // sync on GL Thread
                mContainer.queueEvent(new Runnable() {

                    @Override
                    public void run() {
                        dismissPage();
                    }
                });

                return true;
            }
        }

        return false;
    }

    public Pageable.TransitionListener getTransitionListener() {
        return mTransitionListener;
    }

    public void setTransitionListener(final Pageable.TransitionListener transitionListener) {
        mTransitionListener = transitionListener;
    }
}

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
package com.funzio.pure2D.ui;

import java.util.ArrayList;

import android.util.Log;

import com.funzio.pure2D.containers.Container;

/**
 * @author long.ngo
 */
public class PageStacker implements Pageable.TransitionListener {

    protected static final String TAG = PageStacker.class.getSimpleName();

    protected Container mContainer;
    protected ArrayList<Pageable> mPages = new ArrayList<Pageable>();
    protected int mNumPages = 0;
    protected Pageable mCurrentPage;

    public PageStacker(final Container container) {
        mContainer = container;
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
                    }

                    // slind in the current page
                    mContainer.addChild(page);
                    page.transitionIn(true);
                }
            });

            mNumPages++;

            return true;
        }

        return false;
    }

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

                    // slide in the previous page
                    if (!currentPage.isPageFloating()) {
                        if (previousPage != null) {
                            mContainer.addChild(previousPage);
                            previousPage.transitionIn(false);
                        }
                    }
                }
            }
        });

        return currentPage;
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

    @Override
    public void onTransitionInComplete(final Pageable page) {
        Log.v(TAG, "onTransitionInComplete(): " + page);

        // TODO Auto-generated method stub
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
    }

}

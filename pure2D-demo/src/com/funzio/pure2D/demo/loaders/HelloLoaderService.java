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
package com.funzio.pure2D.demo.loaders;

import android.content.Intent;
import android.os.Environment;

import com.funzio.pure2D.loaders.LoaderService;
import com.funzio.pure2D.loaders.tasks.DownloadTask;

/**
 * @author long
 */
public class HelloLoaderService extends LoaderService {
    public static final String CLASS_NAME = HelloLoaderService.class.getName();
    public static final String DST_DIR = Environment.getExternalStorageDirectory() + "/Android/data/com.funzio.pure2D/";

    private static final boolean OVERRIDING = false;

    public HelloLoaderService() {
        super(CLASS_NAME);

        addTask(new DownloadTask("http://a4.mzstatic.com/us/r1000/106/Purple/v4/04/df/1d/04df1d83-ca73-1bc9-86ed-a8bfe54a54f2/mzl.bfeuaicq.320x480-75.jpg", DST_DIR + "ka_1.jpg", OVERRIDING));
        addTask(new DownloadTask("http://a1.mzstatic.com/us/r1000/102/Purple/v4/4a/83/ae/4a83aebf-d211-06df-bdac-25ee0445fc37/mzl.tvdbyxif.320x480-75.jpg", DST_DIR + "ka_2.jpg", OVERRIDING));
        addTask(new DownloadTask("http://a5.mzstatic.com/us/r1000/070/Purple/v4/33/ec/68/33ec68d7-eadc-67e1-1707-0b817d18c198/mzl.khtwjfxs.320x480-75.jpg", DST_DIR + "ka_3.jpg", OVERRIDING));
        addTask(new DownloadTask("http://a5.mzstatic.com/us/r1000/119/Purple/v4/17/97/5f/17975f9b-b111-2c50-6958-aa3c2cf179ef/mzl.bcrjwems.320x480-75.jpg", DST_DIR + "ka_4.jpg", OVERRIDING));
    }

    public static String getOnFinishAction() {
        return CLASS_NAME + "." + INTENT_ON_FINISHED;
    }

    public static Intent getStartIntent() {
        final Intent intent = new Intent(CLASS_NAME);
        intent.setAction(CLASS_NAME + "." + INTENT_START);
        return intent;
    }
}

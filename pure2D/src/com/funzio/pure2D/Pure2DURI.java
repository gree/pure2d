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
package com.funzio.pure2D;

/**
 * @author long.ngo
 */
public class Pure2DURI {

    public static final String STRING = "@string/";
    public static final String DRAWABLE = "@drawable/";
    public static final String XML = "@xml/";
    public static final String ASSET = "asset://";
    public static final String FILE = "file://";
    public static final String HTTP = "http://";
    public static final String CACHE = "cache://";

    public static String getPathFromUri(final String uri) {
        if (uri == null) {
            return null;
        }

        String actualPath = null;
        if (uri.startsWith(Pure2DURI.DRAWABLE)) {
            actualPath = uri.substring(Pure2DURI.DRAWABLE.length());
        } else if (uri.startsWith(Pure2DURI.ASSET)) {
            actualPath = uri.substring(Pure2DURI.ASSET.length());
        } else if (uri.startsWith(Pure2DURI.FILE)) {
            actualPath = uri.substring(Pure2DURI.FILE.length());
        } else if (uri.startsWith(Pure2DURI.CACHE)) {
            actualPath = uri.substring(Pure2DURI.CACHE.length());
        } else {
            actualPath = uri; // keep
        }

        return actualPath;
    }

    public static final String string(final String path) {
        return STRING + path;
    }

    public static final String drawable(final String path) {
        return DRAWABLE + path;
    }

    public static final String xml(final String path) {
        return XML + path;
    }

    public static final String asset(final String path) {
        return ASSET + path;
    }

    public static final String file(final String path) {
        return FILE + path;
    }

    public static final String http(final String path) {
        return HTTP + path;
    }

    public static final String cache(final String path) {
        return CACHE + path;
    }

}

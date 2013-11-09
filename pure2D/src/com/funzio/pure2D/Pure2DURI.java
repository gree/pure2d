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

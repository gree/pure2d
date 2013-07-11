/**
 * 
 */
package com.funzio.pure2D.utils;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author long
 */
public class PathUtils {

    public static String getRelativePath(final String shortPath, final String longPath) {
        try {
            final URI a = new URI(shortPath);
            final URI b = new URI(longPath);
            return a.relativize(b).toString();
        } catch (URISyntaxException e) {
            return longPath;
        }
    }
}

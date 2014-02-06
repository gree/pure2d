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
 * @author long
 */
public interface InvalidateFlags {
    // transformation related
    public static final int ORIGIN = 1 << 0;
    public static final int POSITION = 1 << 1;
    public static final int SCALE = 1 << 2;
    public static final int ROTATION = 1 << 3;
    public static final int SKEW = 1 << 4;
    public static final int SIZE = 1 << 5;
    public static final int PIVOT = 1 << 6;

    // visual related
    public static final int VISIBILITY = 1 << 10;
    public static final int COLOR = 1 << 11;
    public static final int ALPHA = 1 << 12;
    public static final int BLEND = 1 << 13;
    public static final int TEXTURE = 1 << 14;
    public static final int TEXTURE_COORDS = 1 << 15;
    public static final int PERSPECTIVE = 1 << 16;
    public static final int DEPTH = 1 << 17;
    public static final int FRAME = 1 << 18;
    public static final int VERTICES = 1 << 19;

    // container related
    public static final int CHILDREN = 1 << 20;
    public static final int PARENT = 1 << 21;
    public static final int CACHE = 1 << 22;
    public static final int PARENT_BOUNDS = 1 << 23;

    // glsurface related
    // public static final int SURFACE = 1 << 30;

    // combined flags
    public static final int TRANSFORM_MATRIX = SKEW;
    public static final int BOUNDS = ORIGIN | POSITION | ROTATION | SCALE | SIZE | PIVOT | TRANSFORM_MATRIX | PARENT | PARENT_BOUNDS;
    public static final int VISUAL = VISIBILITY | COLOR | ALPHA | BLEND | TEXTURE | TEXTURE_COORDS | PERSPECTIVE | DEPTH | FRAME | VERTICES;
    public static final int ALL = BOUNDS | VISUAL | CHILDREN;
}

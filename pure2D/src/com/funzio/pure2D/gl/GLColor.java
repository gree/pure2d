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
package com.funzio.pure2D.gl;

import android.graphics.Color;

/**
 * @author long
 */
public class GLColor {
    public static final GLColor BLACK = new GLColor(0, 0, 0, 1f);
    public static final GLColor WHITE = new GLColor(1f, 1f, 1f, 1f);

    public float r = 0;
    public float g = 0;
    public float b = 0;
    public float a = 0;

    public GLColor(final float r, final float g, final float b, final float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public GLColor(final float n) {
        this.r = n;
        this.g = n;
        this.b = n;
        this.a = n;
    }

    /**
     * @param color. E.g 0xFFFF0000
     */
    public GLColor(final int color) {
        this((color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF, color >>> 24);
    }

    public GLColor(final int r, final int g, final int b, final int a) {
        this.r = (float) r / 255;
        this.g = (float) g / 255;
        this.b = (float) b / 255;
        this.a = (float) a / 255;
    }

    public GLColor(final GLColor color) {
        this.r = color.r;
        this.g = color.g;
        this.b = color.b;
        this.a = color.a;
    }

    public void setValues(final GLColor src) {
        this.r = src.r;
        this.g = src.g;
        this.b = src.b;
        this.a = src.a;
    }

    public void setValues(final int color) {
        r = (color >> 16) & 0xFF;
        g = (color >> 8) & 0xFF;
        b = color & 0xFF;
        a = color >>> 24;
    }

    public void setValues(final float r, final float g, final float b, final float a) {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;
    }

    public void setValues(final int r, final int g, final int b, final int a) {
        this.r = (float) r / 255;
        this.g = (float) g / 255;
        this.b = (float) b / 255;
        this.a = (float) a / 255;
    }

    public void multiply(final float n) {
        this.r *= n;
        this.g *= n;
        this.b *= n;
        this.a *= n;
    }

    public void multiply(final GLColor color) {
        this.r *= color.r;
        this.g *= color.g;
        this.b *= color.b;
        this.a *= color.a;
    }

    public int toInt() {
        return Color.argb(Math.round(a * 255), Math.round(r * 255), Math.round(g * 255), Math.round(b * 255));
    }

    public boolean equals(final GLColor color) {
        return color != null ? r == color.r && g == color.g && b == color.b && a == color.a : false;
    }

    public boolean equals(final float r, final float g, final float b, final float a) {
        return r == this.r && g == this.g && b == this.b && a == this.a;
    }

    @Override
    public String toString() {
        return String.format("#%02x%02x%02x%02x", Math.round(r * 255), Math.round(g * 255), Math.round(b * 255), Math.round(a * 255));
    }

    public static int getInterpolatedValue(final int color1, final int color2, final float interValue) {
        final int a1 = color1 >>> 24;
        final int r1 = (color1 >> 16) & 0xFF;
        final int g1 = (color1 >> 8) & 0xFF;
        final int b1 = color1 & 0xFF;

        final int a2 = color2 >>> 24;
        final int r2 = (color2 >> 16) & 0xFF;
        final int g2 = (color2 >> 8) & 0xFF;
        final int b2 = color2 & 0xFF;

        return Color.argb(a1 + Math.round((a2 - a1) * interValue), r1 + Math.round((r2 - r1) * interValue), g1 + Math.round((g2 - g1) * interValue), b1 + Math.round((b2 - b1) * interValue));
    }

    public static GLColor createInterpolatedColor(final int color1, final int color2, final float interValue) {
        return new GLColor(getInterpolatedValue(color1, color2, interValue));
    }

    public static GLColor createInterpolatedColor(final GLColor color1, final GLColor color2, final float interValue) {
        return createInterpolatedColor(color1.toInt(), color2.toInt(), interValue);
    }
}

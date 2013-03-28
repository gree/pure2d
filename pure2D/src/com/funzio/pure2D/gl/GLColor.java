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
}

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

import android.graphics.PointF;

import org.xmlpull.v1.XmlPullParser;

import com.funzio.pure2D.DisplayObject;
import com.funzio.pure2D.containers.Container;
import com.funzio.pure2D.containers.DisplayGroup;
import com.funzio.pure2D.containers.LinearGroup;
import com.funzio.pure2D.containers.VGroup;
import com.funzio.pure2D.ui.vo.UIConfigVO;

/**
 * @author long.ngo
 */
public class UIConstraint {
    protected static final String ATT_X = "x";
    protected static final String ATT_Y = "y";
    protected static final String ATT_HEIGHT = "height";
    protected static final String ATT_WIDTH = "width";
    protected static final String ATT_LEFT = "left";
    protected static final String ATT_RIGHT = "right";
    protected static final String ATT_TOP = "top";
    protected static final String ATT_BOTTOM = "bottom";
    protected static final String ATT_CENTER_X = "centerX";
    protected static final String ATT_CENTER_Y = "centerY";
    protected static final String ATT_CHILDREN_GAP = "childrenGap";

    protected static final String VAL_WRAP_CONTENT = "wrap_content";
    protected static final String VAL_PERCENT = "%";

    public static enum UNIT {
        UNSET, PIXEL, PERCENT, WRAP
    }

    public float width;
    public UNIT widthUnit = UNIT.UNSET;
    public float height;
    public UNIT heightUnit = UNIT.UNSET;

    public float x;
    public UNIT xUnit = UNIT.UNSET;
    public float y;
    public UNIT yUnit = UNIT.UNSET;

    public float left;
    public UNIT leftUnit = UNIT.UNSET;
    public float right;
    public UNIT rightUnit = UNIT.UNSET;
    public float top;
    public UNIT topUnit = UNIT.UNSET;
    public float bottom;
    public UNIT bottomUnit = UNIT.UNSET;

    public float centerX;
    public UNIT centerXUnit = UNIT.UNSET;
    public float centerY;
    public UNIT centerYUnit = UNIT.UNSET;

    public float childrenGap;
    public UNIT childrenGapUnit = UNIT.UNSET;

    private UIConfigVO mUIConfigVO;
    // private int mAxisSystem = Scene.AXIS_BOTTOM_LEFT;
    private boolean mHasAttributes = false;

    public UIConstraint() {
        // TODO Auto-generated constructor stub
    }

    public UIConstraint(final XmlPullParser parser, final UIManager manager) {
        setAttributes(parser, manager);
    }

    public void setAttributes(final XmlPullParser parser, final UIManager manager) {
        mUIConfigVO = manager.getConfig();
        // mAxisSystem = manager.getTextureManager().getGLState().getAxisSystem();
        mHasAttributes = false;

        widthUnit = getAttributeUnit(parser, ATT_WIDTH);
        if (widthUnit != UNIT.UNSET) {
            if (widthUnit != UNIT.WRAP) {
                width = getAttributeValue(parser, ATT_WIDTH);
            }
            mHasAttributes = true;
        }

        heightUnit = getAttributeUnit(parser, ATT_HEIGHT);
        if (heightUnit != UNIT.UNSET) {
            if (heightUnit != UNIT.WRAP) {
                height = getAttributeValue(parser, ATT_HEIGHT);
            }
            mHasAttributes = true;
        }

        xUnit = getAttributeUnit(parser, ATT_X);
        if (xUnit != UNIT.UNSET) {
            x = getAttributeValue(parser, ATT_X);
            mHasAttributes = true;
        }

        yUnit = getAttributeUnit(parser, ATT_Y);
        if (yUnit != UNIT.UNSET) {
            y = getAttributeValue(parser, ATT_Y);
            mHasAttributes = true;
        }

        leftUnit = getAttributeUnit(parser, ATT_LEFT);
        if (leftUnit != UNIT.UNSET) {
            left = getAttributeValue(parser, ATT_LEFT);
            mHasAttributes = true;
        }

        rightUnit = getAttributeUnit(parser, ATT_RIGHT);
        if (rightUnit != UNIT.UNSET) {
            right = getAttributeValue(parser, ATT_RIGHT);
            mHasAttributes = true;
        }

        topUnit = getAttributeUnit(parser, ATT_TOP);
        if (topUnit != UNIT.UNSET) {
            top = getAttributeValue(parser, ATT_TOP);
            mHasAttributes = true;
        }

        bottomUnit = getAttributeUnit(parser, ATT_BOTTOM);
        if (bottomUnit != UNIT.UNSET) {
            bottom = getAttributeValue(parser, ATT_BOTTOM);
            mHasAttributes = true;
        }

        centerXUnit = getAttributeUnit(parser, ATT_CENTER_X);
        if (centerXUnit != UNIT.UNSET) {
            centerX = getAttributeValue(parser, ATT_CENTER_X);
            mHasAttributes = true;
        }

        centerYUnit = getAttributeUnit(parser, ATT_CENTER_Y);
        if (centerYUnit != UNIT.UNSET) {
            centerY = getAttributeValue(parser, ATT_CENTER_Y);
            mHasAttributes = true;
        }

        childrenGapUnit = getAttributeUnit(parser, ATT_CHILDREN_GAP);
        if (childrenGapUnit != UNIT.UNSET) {
            childrenGap = getAttributeValue(parser, ATT_CHILDREN_GAP);
            mHasAttributes = true;
        }
    }

    public void mergeAttributes(final XmlPullParser parser) {

        if (parser.getAttributeValue(null, ATT_WIDTH) != null) {
            widthUnit = getAttributeUnit(parser, ATT_WIDTH);
            if (widthUnit != UNIT.UNSET) {
                if (widthUnit != UNIT.WRAP) {
                    width = getAttributeValue(parser, ATT_WIDTH);
                }
                mHasAttributes = true;
            }
        }

        if (parser.getAttributeValue(null, ATT_HEIGHT) != null) {
            heightUnit = getAttributeUnit(parser, ATT_HEIGHT);
            if (heightUnit != UNIT.UNSET) {
                if (heightUnit != UNIT.WRAP) {
                    height = getAttributeValue(parser, ATT_HEIGHT);
                }
                mHasAttributes = true;
            }
        }

        if (parser.getAttributeValue(null, ATT_X) != null) {
            xUnit = getAttributeUnit(parser, ATT_X);
            if (xUnit != UNIT.UNSET) {
                x = getAttributeValue(parser, ATT_X);
                mHasAttributes = true;
            }
        }

        if (parser.getAttributeValue(null, ATT_Y) != null) {
            yUnit = getAttributeUnit(parser, ATT_Y);
            if (yUnit != UNIT.UNSET) {
                y = getAttributeValue(parser, ATT_Y);
                mHasAttributes = true;
            }
        }

        if (parser.getAttributeValue(null, ATT_LEFT) != null) {
            leftUnit = getAttributeUnit(parser, ATT_LEFT);
            if (leftUnit != UNIT.UNSET) {
                left = getAttributeValue(parser, ATT_LEFT);
                mHasAttributes = true;
            }
        }

        if (parser.getAttributeValue(null, ATT_RIGHT) != null) {
            rightUnit = getAttributeUnit(parser, ATT_RIGHT);
            if (rightUnit != UNIT.UNSET) {
                right = getAttributeValue(parser, ATT_RIGHT);
                mHasAttributes = true;
            }
        }

        if (parser.getAttributeValue(null, ATT_TOP) != null) {
            topUnit = getAttributeUnit(parser, ATT_TOP);
            if (topUnit != UNIT.UNSET) {
                top = getAttributeValue(parser, ATT_TOP);
                mHasAttributes = true;
            }
        }

        if (parser.getAttributeValue(null, ATT_BOTTOM) != null) {
            bottomUnit = getAttributeUnit(parser, ATT_BOTTOM);
            if (bottomUnit != UNIT.UNSET) {
                bottom = getAttributeValue(parser, ATT_BOTTOM);
                mHasAttributes = true;
            }
        }

        if (parser.getAttributeValue(null, ATT_CENTER_X) != null) {
            centerXUnit = getAttributeUnit(parser, ATT_CENTER_X);
            if (centerXUnit != UNIT.UNSET) {
                centerX = getAttributeValue(parser, ATT_CENTER_X);
                mHasAttributes = true;
            }
        }

        if (parser.getAttributeValue(null, ATT_CENTER_Y) != null) {
            centerYUnit = getAttributeUnit(parser, ATT_CENTER_Y);
            if (centerYUnit != UNIT.UNSET) {
                centerY = getAttributeValue(parser, ATT_CENTER_Y);
                mHasAttributes = true;
            }
        }

        if (parser.getAttributeValue(null, ATT_CHILDREN_GAP) != null) {
            childrenGapUnit = getAttributeUnit(parser, ATT_CHILDREN_GAP);
            if (childrenGapUnit != UNIT.UNSET) {
                childrenGap = getAttributeValue(parser, ATT_CHILDREN_GAP);
                mHasAttributes = true;
            }
        }
    }

    protected float getAttributeValue(final XmlPullParser parser, final String att) {
        final String valueSt = parser.getAttributeValue(null, att);
        if (valueSt != null && valueSt.length() > 0) {
            if (valueSt.endsWith(VAL_PERCENT)) {
                return Float.valueOf(valueSt.substring(0, valueSt.length() - 1).trim()) / 100; // pre-cal
            } else {
                return Float.valueOf(valueSt.trim()) * mUIConfigVO.screen_scale;
            }
        }

        return 0;
    }

    protected UNIT getAttributeUnit(final XmlPullParser parser, final String att) {
        final String valueSt = parser.getAttributeValue(null, att);
        if (valueSt != null && valueSt.length() > 0) {
            if (valueSt.equals(VAL_WRAP_CONTENT)) {
                return UNIT.WRAP;
            } else if (valueSt.endsWith(VAL_PERCENT)) {
                return UNIT.PERCENT;
            } else {
                return UNIT.PIXEL;
            }
        } else {
            return UNIT.UNSET;
        }
    }

    public boolean hasAttributes() {
        return mHasAttributes;
    }

    public void apply(final DisplayObject target, final Container container) {
        if (container == null || !mHasAttributes) {
            return;
        }

        final PointF targetPos = target.getPosition();
        final PointF targetSize = target.getSize();
        final PointF parentSize = container.getSize();
        // if (container instanceof Scene) {
        // final Scene scene = (Scene) container;
        // final PointF fixedScale = scene.getStage().getFixedScale();
        // // apply the fixed scale when using Hardware Scaler
        // parentSize.x *= fixedScale.x;
        // parentSize.y *= fixedScale.y;
        // }
        final float parentW = parentSize.x, parentH = parentSize.y;
        final float cx = targetPos.x, cy = targetPos.y;
        final float cw = targetSize.x, ch = targetSize.y;
        float l = cx, r = 0, t = 0, b = cy, w = cw, h = ch;

        // left and right
        if (leftUnit == UNIT.PIXEL) {
            l = left;
        } else if (leftUnit == UNIT.PERCENT) {
            l = parentW * left;
        }
        if (rightUnit == UNIT.PIXEL) {
            r = right;
        } else if (rightUnit == UNIT.PERCENT) {
            r = parentW * right;
        }
        // implicit width
        if (leftUnit != UNIT.UNSET && rightUnit != UNIT.UNSET) {
            w = parentW - (l + r);
        }

        // top and bottom
        if (topUnit == UNIT.PIXEL) {
            t = top;
        } else if (topUnit == UNIT.PERCENT) {
            t = parentH * top;
        }
        if (bottomUnit == UNIT.PIXEL) {
            b = bottom;
        } else if (bottomUnit == UNIT.PERCENT) {
            b = parentH * bottom;
        }
        // implicit height
        if (topUnit != UNIT.UNSET && bottomUnit != UNIT.UNSET) {
            h = parentH - (t + b);
        }

        // gap
        if (childrenGapUnit != UNIT.UNSET && target instanceof LinearGroup) {
            float g = 0;
            if (childrenGapUnit == UNIT.PIXEL) {
                g = childrenGap;
            } else if (childrenGapUnit == UNIT.PERCENT) {
                g = childrenGap * (target instanceof VGroup ? target.getHeight() : target.getWidth());
            }
            ((LinearGroup) target).setGap(g);
        }

        // explicit width
        if (widthUnit == UNIT.PIXEL) {
            w = width;
        } else if (widthUnit == UNIT.PERCENT) {
            w = parentW * width;
        } else if (widthUnit == UNIT.WRAP && target instanceof DisplayGroup) {
            ((DisplayGroup) target).setWrapContentWidth(true);
        }

        // explicit height
        if (heightUnit == UNIT.PIXEL) {
            h = height;
        } else if (heightUnit == UNIT.PERCENT) {
            h = parentH * height;
        } else if (heightUnit == UNIT.WRAP && target instanceof DisplayGroup) {
            ((DisplayGroup) target).setWrapContentHeight(true);
        }

        // x center
        if (centerXUnit != UNIT.UNSET) {
            if (centerXUnit == UNIT.PIXEL) {
                l = centerX;
            } else if (xUnit == UNIT.PERCENT) {
                l = parentW * centerX;
            }
            l += (parentW - w) * 0.5f + target.getOrigin().x;
        }

        // y center
        if (centerYUnit != UNIT.UNSET) {
            if (centerYUnit == UNIT.PIXEL) {
                b = centerY;
            } else if (yUnit == UNIT.PERCENT) {
                b = parentH * centerY;
            }
            b += (parentH - h) * 0.5f + target.getOrigin().y;
        }

        // implicit left base on right and width
        if (leftUnit == UNIT.UNSET && rightUnit != UNIT.UNSET) {
            l = parentW - r - w + target.getOrigin().x * 2;
        }

        // implicit bottom base on top and height
        if (bottomUnit == UNIT.UNSET && topUnit != UNIT.UNSET) {
            b = parentH - t - h + target.getOrigin().y * 2;
        }

        // explicit x
        if (xUnit == UNIT.PIXEL) {
            l = x;
        } else if (xUnit == UNIT.PERCENT) {
            l = parentW * x;
        }

        // explicit y
        if (yUnit == UNIT.PIXEL) {
            b = y;
        } else if (yUnit == UNIT.PERCENT) {
            b = parentH * y;
        }

        // apply with diff check for perf
        if (cx != l || cy != b) {
            target.setPosition(l, b);
        }
        if (cw != w || ch != h) {
            target.setSize(w, h);
        }

    }
}

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
package com.funzio.crimecity.game.model;

import java.util.HashMap;
import java.util.Map;

/**
 * @author marks
 */
public enum CCMapDirection {
    INVALID(0, "INVALID"), NORTHEAST(45, "NE"), EAST(90, "E"), SOUTHEAST(135, "SE"), SOUTH(180, "S"), SOUTHWEST(225, "SW"), WEST(270, "W"), NORTHWEST(315, "NW"), NORTH(360, "N"),

    ;

    public final int degrees;
    public final String abbrev;
    private static volatile Map<String, CCMapDirection> lookup;
    private static volatile Map<Integer, CCMapDirection> headingLookup;

    private static Map<String, CCMapDirection> getLookup() {
        if (lookup == null) {
            lookup = new HashMap<String, CCMapDirection>();
        }
        return lookup;
    }

    private static Map<Integer, CCMapDirection> getHeadingLookup() {
        if (headingLookup == null) {
            headingLookup = new HashMap<Integer, CCMapDirection>();
        }
        return headingLookup;
    }

    private CCMapDirection(final int degrees, final String abbrev) {
        this.degrees = degrees;
        this.abbrev = abbrev;
        getLookup().put(abbrev, this);
        getHeadingLookup().put(degrees, this);
    }

    public CCMapDirection rotateClockwise90() {
        return this == INVALID ? INVALID : values()[Math.max(1, (ordinal() + 2) % 8)];
    }

    public CCMapDirection rotate45(final Boolean ccw) {
        CCMapDirection value;
        if (!ccw) {
            value = values()[(ordinal() + 1) % 8];
        } else {
            value = values()[(ordinal() + 7) % 8];
        }
        value = value == INVALID ? NORTH : value;
        return value;
    }

    public static CCMapDirection forAbbreviation(final String isoDirection) {
        CCMapDirection dir = getLookup().get(isoDirection);
        return dir == null ? INVALID : dir;
    }

    public static CCMapDirection forHeading(final int heading) {
        CCMapDirection dir = getHeadingLookup().get(heading);
        return dir == null ? INVALID : dir;
    }

    public static int headingWithDirection(final String isoDirection) {
        return forAbbreviation(isoDirection).degrees;
    }

    public boolean equals(final CCMapDirection direction) {
        return degrees == direction.degrees;
    }
}

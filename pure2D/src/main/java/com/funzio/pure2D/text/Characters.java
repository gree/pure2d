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
package com.funzio.pure2D.text;

import java.util.LinkedHashSet;

/**
 * @author long
 */
public class Characters {
    public static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
    public static final String ALPHABET_UPPERCASE = ALPHABET.toUpperCase();
    public static final String DIGITS = "0123456789";
    public static final String SYMBOLS = "!\"#$%&'()*+,-./[\\]^_{|}~";
    public static final String BASIC_SET = ALPHABET_UPPERCASE + ALPHABET + DIGITS + SYMBOLS;

    public static final char SPACE = ' ';
    public static final char NEW_LINE = '\n';

    public static String getUniqueCharacters(final String... strings) {

        final LinkedHashSet<Character> charset = new LinkedHashSet<Character>();
        final StringBuffer buffer = new StringBuffer(charset.size());
        for (String s : strings) {
            final int len = s.length();
            for (int i = 0; i < len; i++) {
                final Character ch = s.charAt(i);
                if (!(ch == Characters.SPACE || ch == Characters.NEW_LINE)) {
                    if (charset.add(ch)) {
                        buffer.append(ch);
                    }
                }
            }
        }

        return buffer.toString();
    }
}

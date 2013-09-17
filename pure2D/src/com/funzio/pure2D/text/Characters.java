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

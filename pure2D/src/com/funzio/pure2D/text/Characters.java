/**
 * 
 */
package com.funzio.pure2D.text;

/**
 * @author long
 */
public interface Characters {
    public static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
    public static final String ALPHABET_UPPERCASE = ALPHABET.toUpperCase();
    public static final String DIGITS = "0123456789";
    public static final String SYMBOLS = "!\"#$%&'()*+,-./[\\]^_{|}~";
    public static final String BASIC_SET = ALPHABET_UPPERCASE + ALPHABET + DIGITS + SYMBOLS;

    public static final char SPACE = ' ';
    public static final char NEW_LINE = '\n';
}

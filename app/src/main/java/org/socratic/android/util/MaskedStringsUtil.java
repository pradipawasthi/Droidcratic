package org.socratic.android.util;

/**
 * Created by pcnofelt on 3/1/17.
 */

public class MaskedStringsUtil {

    /** "AES" */
    private static final char[] AES = new char[] { 65, 69, 83 };

    /** "UTF-8" */
    private static final char[] UTF8 = new char[] { 85, 84, 70, 45, 56 };

    /** "SHA-256" */
    private static final char[] SHA_256 = new char[] { 83, 72, 65, 45, 50, 53, 54 };

    /** "AES/CBC/PKCS7Padding" */
    private static final char[] AES_CBC_PKCS7 = new char[] { 65, 69, 83, 47, 67, 66, 67, 47, 80, 75, 67, 83, 55, 80, 97, 100, 100, 105, 110, 103 };



    public static final String getStringAES() {
        return new String(AES);
    }

    public static final String getStringUTF8() {
        return new String(UTF8);
    }

    public static final String getStringSHA256() {
        return new String(SHA_256);
    }

    public static final String getStringAES_CBC_PKCS7() {
        return new String(AES_CBC_PKCS7);
    }
}

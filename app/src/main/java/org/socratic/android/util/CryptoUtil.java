package org.socratic.android.util;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Perk Mobile on 3/1/17.
 */

public class CryptoUtil {
    private CryptoUtil() {
    }

    private static SecretKeySpec makeKey(String password, String salt)
            throws Exception {
        if (TextUtils.isEmpty(password) || TextUtils.isEmpty(salt)) {
            throw new IllegalArgumentException();
        }

        MessageDigest digester = MessageDigest.getInstance(MaskedStringsUtil.getStringSHA256());
        digester.update(password.getBytes(MaskedStringsUtil.getStringUTF8()));
        digester.update(salt.getBytes(MaskedStringsUtil.getStringUTF8()));
        byte[] hash = digester.digest();

        return new SecretKeySpec(hash, MaskedStringsUtil.getStringAES());
    }

    @SuppressLint("TrulyRandom")
    public static byte[] encrypt(byte[] cleartext, String password, String salt)
            throws Exception {
        SecretKeySpec keyspec = makeKey(password, salt);

        Cipher cipher = Cipher.getInstance(MaskedStringsUtil.getStringAES());
        cipher.init(Cipher.ENCRYPT_MODE, keyspec);
        byte[] encrypted = cipher.doFinal(cleartext);

        return encrypted;
    }

    public static byte[] decrypt(byte[] encrypted, String password, String salt)
            throws Exception {
        SecretKeySpec keyspec = makeKey(password, salt);

        Cipher cipher = Cipher.getInstance(MaskedStringsUtil.getStringAES());
        cipher.init(Cipher.DECRYPT_MODE, keyspec);
        byte[] decrypted = cipher.doFinal(encrypted);

        return decrypted;
    }
}

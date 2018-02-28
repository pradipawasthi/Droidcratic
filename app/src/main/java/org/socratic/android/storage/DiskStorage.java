package org.socratic.android.storage;

import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Base64;

import org.socratic.android.dagger.qualifiers.Storage;
import org.socratic.android.util.CryptoUtil;
import org.socratic.android.util.MaskedStringsUtil;

import java.util.UUID;

import javax.inject.Inject;

/**
 * Acts as a wrapper around shared preferences. Make sure to add
 * any files you declare to the 'files' array. These items will
 * be deleted when the user logs out.
 * <p/>
 * Each file will be encrypted using the keyInstallTimestamp and
 * keyInstallUUID parameters so file contents aren't easily viewable.
 * <p/>
 * Created by Perk Mobile on 3/1/17.
 */
public class DiskStorage {

    private static final int BIT_LENGTH = 128;

    private static final String keyInstallTimestamp = "5ae91d532d9248e4bd5416e636d98856"; // stored as cleartext.
    private static final String keySearchCounter = "DeGCz44HP4m8vVc59JK2QE4Le2VfBcQK"; // stored as cleartext.
    private static final String keyLoggedFirstOpen = "tdVkqP6gNrffe8Yxwmu3d2kmua5uUDN9"; // stored as cleartext.
    private static final String keyUserId = "3ddc9f4b9d9e464c9c3a7e2a45ba838d";
    private static final String keyRatingRequestShowAttempts = "c8c5b9d64f1f4cd59dc5e28090bee7a9";
    private static final String keyHasCompletedRatingRequest = "edcbfa9e480a40c784f27c7b9c87f3f5";

    private static final String keyInstallUUID1 = "a1dacaa862dd4da594cd03b3d2a24e1e"; // stored as cleartext.
    private static final String keyInstallUUID2 = "eb790e8ef1934b798af4c0bdfd76ad40"; // stored as cleartext.
    private static final String keyInstallUUID3 = "252e7921e86e428e8384868e16241d0d"; // stored as cleartext.
    private static final String keyInstallUUID4 = "835ffd5b46544751a7f4bb7b1ca86dd3"; // stored as cleartext.
    private static final String keyInstallUUID5 = "a8123b2aa0cc4c208153ea1efbb544db"; // stored as cleartext.

    private static final String filenameInstallationPref = "aa5d76fb27da4f03a36e3c740559c8c6";
    private static final String keyInstallationPref = "9b44221c09f545d39a60d5d98aaeb3b3";

    private static final String filenameUserPref = "6092284214304b7b8a9cfaada20756fb";
    private static final String keyUserPref = "9a6a86031da5466cb510bb216082b15a";

    private static final String filenameGcmPref = "324bef70565849628ea56bdce7fefa3c";
    private static final String keyGcmPref = "096f8f6b75694169a44941acdb9d1f56";

    private static final String keySessionCounter = "0589efdd7fd4d3ee29e9be8006db8b7f";

    private static final String filenameContactsList = "699bbf0a514258f56b67bd94dd34e01f";
    private static final String keyContactsList = "ba0621c4c5904daa16879419cd109dfa";

    private SharedPreferences diskPreferences;

    private static final String[] files = new String[]{
            filenameInstallationPref,
            filenameUserPref,
            filenameGcmPref
    };

    @Inject
    public DiskStorage(@Storage SharedPreferences diskPreferences) {
        this.diskPreferences = diskPreferences;
    }

    /**
     * Your chance to insert some values at startup time.
     */
    public final void setupDefaults() {
        boolean dirty = false;

        SharedPreferences.Editor edit = diskPreferences.edit();

        if (!diskPreferences.contains(keyInstallTimestamp)) {
            edit.putLong(keyInstallTimestamp, System.currentTimeMillis() / 1000);
            dirty = true;
        }

        ///////////////////
        // create random values
        if (!diskPreferences.contains(keyInstallUUID1)) {
            edit.putString(keyInstallUUID1, UUID.randomUUID().toString());
            dirty = true;
        }
        if (!diskPreferences.contains(keyInstallUUID2)) {
            edit.putString(keyInstallUUID2, UUID.randomUUID().toString());

            dirty = true;
        }
        if (!diskPreferences.contains(keyInstallUUID3)) {
            edit.putString(keyInstallUUID3, UUID.randomUUID().toString());
            dirty = true;
        }
        if (!diskPreferences.contains(keyInstallUUID4)) {
            edit.putString(keyInstallUUID4, UUID.randomUUID().toString());
            dirty = true;
        }
        if (!diskPreferences.contains(keyInstallUUID5)) {
            edit.putString(keyInstallUUID5, UUID.randomUUID().toString());
            dirty = true;
        }
        if (dirty) {
            edit.apply();
        }
    }

    public final long getInstallTimestamp() {
        return diskPreferences.getLong(keyInstallTimestamp, 0L);
    }

    public final int getSearchCount() {
        return diskPreferences.getInt(keySearchCounter, 0);
    }

    public final void incrementSearchCount() {
        int val = diskPreferences.getInt(keySearchCounter, 0) + 1;
        diskPreferences.edit().putInt(keySearchCounter, val).apply();
    }

    public final int getSessionCount() {
        return diskPreferences.getInt(keySessionCounter, 1);
    }

    public final void incrementSessionCount() {
        int count = diskPreferences.getInt(keySessionCounter, 0) + 1;
        diskPreferences.edit().putInt(keySessionCounter, count).apply();
    }

    public boolean getLoggedFirstOpen() {
        return diskPreferences.getBoolean(keyLoggedFirstOpen, false);
    }

    public final void setLoggedFirstOpen() {
        diskPreferences.edit().putBoolean(keyLoggedFirstOpen, true).apply();
    }

    public final void setCompletedRatingRequest() {
        diskPreferences.edit().putBoolean(keyHasCompletedRatingRequest, true).apply();
    }

    public final Boolean hasCompletedRatingRequest() {
        return diskPreferences.getBoolean(keyHasCompletedRatingRequest, false);
    }

    public final int getRatingRequestShowAttempts() {
        return diskPreferences.getInt(keyRatingRequestShowAttempts, 0);
    }

    public final void incrementRatingRequestShowAttempts() {
        int val = diskPreferences.getInt(keyRatingRequestShowAttempts, 0) + 1;
        diskPreferences.edit().putInt(keyRatingRequestShowAttempts, val).apply();
    }


    public final String loadInstallationPref()
            throws Exception {
        return readData(filenameInstallationPref, keyInstallationPref);
    }

    public final void saveInstallationPref(String unencrypted)
            throws Exception {
        writeData(filenameInstallationPref, keyInstallationPref, unencrypted);
    }

    public final String loadUserPref()
            throws Exception {
        return readData(filenameUserPref, keyUserPref);
    }

    public final void saveUserPref(String unencrypted)
            throws Exception {
        writeData(filenameUserPref, keyUserPref, unencrypted);
    }

    public final String loadGcmPref()
            throws Exception {
        return readData(filenameGcmPref, keyGcmPref);
    }

    public final void saveGcmPref(String unencrypted)
            throws Exception {
        writeData(filenameGcmPref, keyGcmPref, unencrypted);
    }

    public final void saveContactsList(String contactsList)
            throws Exception {
        writeData(filenameContactsList, keyContactsList, contactsList);
    }

    public final String loadContactsList()
        throws Exception {
        return readData(filenameContactsList, keyContactsList);
    }

    /**
     * For the decryption part: base64 decode -> decrypt -> cleartext.
     */
    public String readData(String filename, String keyname)
            throws Exception {

        String encoded = diskPreferences.getString(keyname, null);
        String decoded = "";
        if (!TextUtils.isEmpty(encoded)) {
            String[] passwordAndSalt = getCryptoPasswordAndSalt();

            byte[] encodedBytes = encoded.getBytes(MaskedStringsUtil.getStringUTF8());
            byte[] decodedBytes = Base64.decode(encodedBytes, Base64.NO_WRAP | Base64.NO_PADDING);
            byte[] decryptedBytes = CryptoUtil.decrypt(decodedBytes, passwordAndSalt[0], passwordAndSalt[1]);
            decoded = new String(decryptedBytes, MaskedStringsUtil.getStringUTF8());
        }

        return decoded;
    }

    /**
     * For the encryption part: cleartext -> encrypt -> base64 encode.
     */
    public void writeData(String filename, String keyname, String unencrypted)
            throws Exception {
        String encoded = "";
        if (!TextUtils.isEmpty(unencrypted)) {
            String[] passwordAndSalt = getCryptoPasswordAndSalt();

            byte[] decryptedBytes = unencrypted.getBytes(MaskedStringsUtil.getStringUTF8());
            byte[] encryptedBytes = CryptoUtil.encrypt(decryptedBytes, passwordAndSalt[0], passwordAndSalt[1]);
            byte[] encodedBytes = Base64.encode(encryptedBytes, Base64.NO_WRAP | Base64.NO_PADDING);
            encoded = new String(encodedBytes, MaskedStringsUtil.getStringUTF8());
        }

        // Write it.
        SharedPreferences.Editor edit = diskPreferences.edit();
        edit.putString(keyname, encoded);
        edit.apply();
    }

    /**
     * Just adds some noise to the values stored on disk.
     * <p/>
     * NOTE: Modifying this logic will invalidate all existing client encrypted
     * data. If this logic must be modified it is suggested that either 1) a
     * data export/import utility is created for this change or 2) ensure that
     * all data is reset and the app is returned to a new user state, so that
     * legacy data does not get accidentally unencrypted with new keys.
     */
    private String[] getCryptoPasswordAndSalt()
            throws Exception {
        String installUUID1 = diskPreferences.getString(keyInstallUUID1, "");
        String installUUID2 = diskPreferences.getString(keyInstallUUID2, "");
        String installUUID3 = diskPreferences.getString(keyInstallUUID3, "");
        String installUUID4 = diskPreferences.getString(keyInstallUUID4, "");
        String installUUID5 = diskPreferences.getString(keyInstallUUID5, "");

        // generate string 1
        StringBuilder sb1 = new StringBuilder();
        sb1.append(installUUID3);
        sb1.delete(sb1.length() / 2, sb1.length());
        sb1.reverse();
        sb1.append(installUUID5);
        sb1.append(sb1.length() % 2 == 0 ? installUUID2  :  installUUID1);
        sb1.reverse();


        // generate string 2
        StringBuilder sb2 = new StringBuilder(installUUID2);
        sb2.delete(sb2.length() / 2, sb2.length());
        sb2.append(installUUID4);
        sb2.delete(sb2.length() / 2, sb2.length());
        sb2.reverse();
        sb2.append(installUUID5.length() % 2 == 0 ? installUUID5.substring(7, 18) : installUUID5);

        return new String[]{
                sb1.toString(),
                sb2.toString()
        };
    }

    public void wipeAllData() {
        for (String filename : files) {
            diskPreferences.edit().clear().apply();
        }
    }
}


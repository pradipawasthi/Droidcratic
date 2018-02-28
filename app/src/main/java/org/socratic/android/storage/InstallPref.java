package org.socratic.android.storage;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.UUID;

import javax.inject.Inject;

/**
 * Created by pcnofelt on 3/1/17.
 */

public final class InstallPref {

    private DiskStorage diskStorage;

    private Gson gson;

    private static final String TAG = InstallPref.class.getSimpleName();

    private Data mData;

    @Inject
    public InstallPref(DiskStorage diskStorage, Gson gson) {
        this.diskStorage = diskStorage;
        this.gson = gson;
        mData = new Data();
    }

    public String getUserId() {
        return mData.getUserId();
    }

    public void loadFromDisk() {
        try {
            mData = gson.fromJson(diskStorage.loadUserPref(), Data.class);
        } catch (Exception ex) {
            Log.e(TAG, "Couldn't load install pref from disk.");
        } finally {
            if (mData == null) {
                mData = new Data();
                // generate user id
                mData.setUserId(UUID.randomUUID().toString());
                save();
            }
        }
    }

    public void save() {
        try {
            String raw = gson.toJson(mData);
            diskStorage.saveUserPref(raw);
        } catch (Exception ex) {
            Log.e(TAG, "Error preemptively saving install pref to disk.", ex);
        }
    }

    public void clear() {
        mData = new Data();
        save();
    }

    /**
     * Not to be used outside of this class. We'll store an encrypted
     * representation on disk which will be loaded up on each app start.
     */
    private static final class Data {

        @SerializedName("734105741c9b4daa9f81cbd783401f5b")
        private String mUserId;

        public String getUserId() {
            return mUserId;
        }

        public void setUserId(String userId) {
            this.mUserId = userId;
        }

    }
}

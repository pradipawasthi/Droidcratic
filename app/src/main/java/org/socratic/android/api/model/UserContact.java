package org.socratic.android.api.model;

import android.provider.ContactsContract;
import android.util.Log;
import android.util.LongSparseArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by williamxu on 8/10/17.
 */

public class UserContact {

    private static final String TAG = UserContact.class.getSimpleName();

    private long id;
    private String name;
    private LongSparseArray<String> emails;
    private LongSparseArray<String> phoneNumbers;

    public UserContact(long id, String name) {
        this.id = id;
        this.name = name;
    }

    public void addEmail(int type, String email) {
        if (emails == null) {
            emails = new LongSparseArray<>();
        }

        emails.put(type, email);
    }

    public void addPhoneNumber(int type, String number) {
        if (phoneNumbers == null) {
            phoneNumbers = new LongSparseArray<>();
        }

        phoneNumbers.put(type, number);
    }

    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("identifier", String.valueOf(id));
            jsonObject.put("firstName", name);
            jsonObject.put("phoneNumbers", getPhoneNumberArray());
            jsonObject.put("emailAddresses", getEmailArray());
        } catch (JSONException e) {
            Log.d(TAG, "JSONException: " + e.getMessage());
        }

        return jsonObject;
    }

    public JSONArray getPhoneNumberArray() {
        JSONArray phoneNumberArray = new JSONArray();

        try {
            if (phoneNumbers != null) {
                for (int i = 0; i < phoneNumbers.size(); i++) {
                    JSONObject phoneNumberObject = new JSONObject();
                    phoneNumberObject.put("label", getPhoneType(i));
                    phoneNumberObject.put("phoneNumber", phoneNumbers.get(phoneNumbers.keyAt(i)));

                    phoneNumberArray.put(phoneNumberObject);
                }
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        }

        return phoneNumberArray;
    }

    public JSONArray getEmailArray() {
        JSONArray emailArray = new JSONArray();

        try {
            if (emails != null) {
                for (int i = 0; i < emails.size(); i++) {
                    JSONObject emailObject = new JSONObject();
                    emailObject.put("label", getEmailType(i));
                    emailObject.put("emailAddress", emails.get(emails.keyAt(i)));

                    emailArray.put(emailObject);
                }
            }
        } catch (JSONException e) {
            Log.d(TAG, e.getMessage());
        }

        return emailArray;
    }

    private String getPhoneType(int index) {
        int type = (int) phoneNumbers.keyAt(index);
        String phoneType;

        switch (type) {
            case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                phoneType = "home";
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                phoneType = "mobile";
                break;
            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                phoneType = "work";
                break;
            default:
                phoneType = "other";
        }

        return phoneType;
    }

    private String getEmailType(int index) {
        int type = (int) emails.keyAt(index);
        String emailType;

        switch (type) {
            case ContactsContract.CommonDataKinds.Email.TYPE_HOME:
                emailType = "home";
                break;
            case ContactsContract.CommonDataKinds.Email.TYPE_MOBILE:
                emailType = "mobile";
                break;
            case ContactsContract.CommonDataKinds.Email.TYPE_WORK:
                emailType = "work";
                break;
            default:
                emailType = "other";
        }

        return emailType;
    }

    public String getName() {
        return name;
    }

    public long getId() { return id; }

}

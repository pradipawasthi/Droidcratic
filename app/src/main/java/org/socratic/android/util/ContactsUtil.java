package org.socratic.android.util;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.LongSparseArray;

import org.socratic.android.api.model.UserContact;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by williamxu on 8/10/17.
 */

public class ContactsUtil {

    private static final String TAG = ContactsUtil.class.getSimpleName();

    public static List<UserContact> fetchContacts(Context context) {
        List<UserContact> contactsList = new LinkedList<>();
        LongSparseArray<UserContact> contactsArray = new LongSparseArray<>();

        String[] projection = {
                ContactsContract.Data.MIMETYPE,
                ContactsContract.Data.CONTACT_ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Contactables.DATA,
                ContactsContract.CommonDataKinds.Contactables.TYPE,
        };

        String selection = ContactsContract.Data.MIMETYPE + " in (?, ?)";
        String[] selectionArgs = {
                ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE,
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
        };
        String sortOrder = ContactsContract.Contacts.SORT_KEY_ALTERNATIVE;

        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            uri = ContactsContract.CommonDataKinds.Contactables.CONTENT_URI;
        } else {
            uri = ContactsContract.Data.CONTENT_URI;
        }

        Cursor cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);

        try {
            final int mimeTypeIdx = cursor.getColumnIndex(ContactsContract.Data.MIMETYPE);
            final int idIdx = cursor.getColumnIndex(ContactsContract.Data.CONTACT_ID);
            final int nameIdx = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            final int dataIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Contactables.DATA);
            final int typeIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Contactables.TYPE);

            while (cursor.moveToNext()) {
                long id = cursor.getLong(idIdx);
                UserContact userContact = contactsArray.get(id);
                if (userContact == null) {
                    userContact = new UserContact(id, cursor.getString(nameIdx));
                    contactsArray.put(id, userContact);
                    contactsList.add(userContact);
                }
                int type = cursor.getInt(typeIdx);
                String data = cursor.getString(dataIdx);
                String mimeType = cursor.getString(mimeTypeIdx);
                if (mimeType.equals(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)) {
                    // mimeType == ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE
                    userContact.addEmail(type, data);
                } else {
                    // mimeType == ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
                    userContact.addPhoneNumber(type, data);
                }
            }
        } catch (NullPointerException exception) {
            Log.d(TAG, "null contacts cursor");
        }

        if (cursor != null) {
            cursor.close();
        }

        return contactsList;
    }


    public static String getContactIDs(List<UserContact> contacts) {

        StringBuilder stringBuilder = new StringBuilder();

        for (UserContact userContact : contacts) {
            stringBuilder.append(userContact.getId());
            stringBuilder.append(" ");
        }

        return  stringBuilder.toString().trim();
    }

    public static boolean hasNewContacts(String oldIds, String newIds) {
        boolean hasNewContacts = false;

        List<String> oldIdsList = Arrays.asList(oldIds.split(" "));
        List<String> newIdsList = Arrays.asList(newIds.split(" "));

        if (!oldIdsList.containsAll(newIdsList)) {
            hasNewContacts = true;
        }

        return hasNewContacts;
    }
}

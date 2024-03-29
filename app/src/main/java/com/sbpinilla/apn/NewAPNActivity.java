package com.sbpinilla.apn;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;

public class NewAPNActivity extends Activity {
    /*
     * Information of all APNs Details can be found in
     * com.android.providers.telephony.TelephonyProvider
     */
    public static final Uri APN_TABLE_URI = Uri
            .parse("content://telephony/carriers");
    /*
     * Information of the preferred APN
     */
    public static final Uri PREFERRED_APN_URI = Uri
            .parse("content://telephony/carriers/preferapn");
    private static final String TAG = "CHANGE_APN";
    public static final String NEW_APN = "APN NAME";

    private int getDafaultAPN() {
        Cursor c = this.getContentResolver().query(PREFERRED_APN_URI,
                new String[] { "_id", "name" }, null, null, null);
        int id = -1;
        if (c != null) {
            try {
                if (c.moveToFirst())
                    id = c.getInt(c.getColumnIndex("_id"));
            } catch (SQLException e) {
                Log.d(TAG, e.getMessage());
            }
            c.close();
        }
        return id;

    }

    /*
     * Set an apn to be the default apn for web traffic Require an input of the
     * apn id to be set
     */
    public boolean setDefaultAPN(int id) {
        boolean res = false;
        ContentResolver resolver = this.getContentResolver();
        ContentValues values = new ContentValues();

        // See /etc/apns-conf.xml. The TelephonyProvider uses this file to
        // provide
        // content://telephony/carriers/preferapn URI mapping
        values.put("apn_id", id);
        try {
            resolver.update(PREFERRED_APN_URI, values, null, null);
            Cursor c = resolver.query(PREFERRED_APN_URI, new String[] { "name",
                    "apn" }, "_id=" + id, null, null);
            if (c != null) {
                res = true;
                c.close();
            }
        } catch (SQLException e) {
            Log.d(TAG, e.getMessage());
        }
        return res;
    }

    private int checkNewAPN() {
        int id = -1;
        Cursor c = this.getContentResolver().query(APN_TABLE_URI,
                new String[] { "_id", "name" }, "name=?",
                new String[] { NEW_APN }, null);
        if (c == null) {
            id = -1;
        } else {
            int record_cnt = c.getCount();
            if (record_cnt == 0) {
                id = -1;
            } else if (c.moveToFirst()) {
                if (c.getString(c.getColumnIndex("name")).equalsIgnoreCase(
                        NEW_APN)) {
                    id = c.getInt(c.getColumnIndex("_id"));
                }
            }
            c.close();
        }
        return id;
    }

    public int addNewAPN() {
        int id = -1;
        ContentResolver resolver = this.getContentResolver();
        ContentValues values = new ContentValues();
        values.put("name", NEW_APN);
        values.put("apn", NEW_APN);

        /*
         * The following three field values are for testing in Android emulator
         * only The APN setting page UI will ONLY display APNs whose 'numeric'
         * filed is TelephonyProperties.PROPERTY_SIM_OPERATOR_NUMERIC. On
         * Android emulator, this value is 310260, where 310 is mcc, and 260
         * mnc. With these field values, the newly added apn will appear in
         * system UI.
         */
        values.put("mcc", "310");
        values.put("mnc", "260");
        values.put("numeric", "310260");

        Cursor c = null;
        try {
            Uri newRow = resolver.insert(APN_TABLE_URI, values);
            if (newRow != null) {
                c = resolver.query(newRow, null, null, null, null);

                // Obtain the apn id
                int idindex = c.getColumnIndex("_id");
                c.moveToFirst();
                id = c.getShort(idindex);
                Log.d(TAG, "New ID: " + id + ": Inserting new APN succeeded!");
            }
        } catch (SQLException e) {
            Log.d(TAG, e.getMessage());
        }

        if (c != null)
            c.close();
        return id;
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_apn);
        int id = checkNewAPN();
        int default_id = getDafaultAPN();

        if (id == -1) {
            id = addNewAPN();
        }

        if (setDefaultAPN(id)) {
            Log.i(TAG, NEW_APN
                    + " set new default APN successfully and Default id is "
                    + id);
        }
        if (setDefaultAPN(default_id)) {
            Log.i(TAG,
                    NEW_APN
                            + " set previous default APN successfully and Default id is "
                            + default_id);
        }

    }
}

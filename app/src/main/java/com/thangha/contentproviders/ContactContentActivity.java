package com.thangha.contentproviders;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

import android.app.ListActivity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;

public class ContactContentActivity extends ListActivity {

    private final String contactAddress = "content://contacts/people";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_content);

        Uri allContacts = ContactsContract.Contacts.CONTENT_URI;

        String[] protection = new String[] {
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.HAS_PHONE_NUMBER
        };

        Cursor c = null;
        CursorLoader loader = new CursorLoader(this, allContacts, protection, ContactsContract.Contacts.HAS_PHONE_NUMBER + " = ? " +
                "AND " + ContactsContract.Contacts.DISPLAY_NAME + " LIKE ? ",
                new String[] {"1", "%a%"},
                ContactsContract.Contacts.DISPLAY_NAME + " DESC");
        c = loader.loadInBackground();

        String[] columns = new String[] {
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts._ID
        };

        int[] views = new int[] {R.id.txtContactName, R.id.txtContactID};

        SimpleCursorAdapter adapter = null;
        adapter = new SimpleCursorAdapter(this, R.layout.activity_contact_content, c, columns, views, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        this.setListAdapter(adapter);
    }
}
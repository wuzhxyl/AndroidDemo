package com.ilifesmart.activity;

import android.content.ContentProvider;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ilifesmart.androiddemo.R;
import com.ilifesmart.util.Utils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ContactsActivity extends BaseActivity {

    @BindView(R.id.contacts_cont)
    RecyclerView mContactsCont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);
        ButterKnife.bind(this);

        if (Utils.checkPermissionGranted(Utils.PERMISSIONS_READ_CONTACTS)) {
            initialize();
        } else {
            Utils.requestPermissions(this, Utils.PERMISSIONS_READ_CONTACTS, true, Utils.PERMISSION_CODE_READ_CONTACTS);
        }
    }

    private void initialize() {
        ContactsAdapter adapter = new ContactsAdapter(getContactsList());
        mContactsCont.setLayoutManager(new LinearLayoutManager(this));
        mContactsCont.setAdapter(adapter);
    }

    private List<Contact> mContacts = new ArrayList<>();
    private List<Contact> getContactsList() {
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phone = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                mContacts.add(new Contact(phone, name));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return mContacts;
    }

    private class ContactHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Contact mContact;
        public ContactHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
        }

        public void onBind(Contact contact) {
            mContact = contact;
            this.mContact = contact;

            ((TextView)itemView.findViewById(R.id.name)).setText(contact.name);
            ((TextView)itemView.findViewById(R.id.phone)).setText(contact.phone);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(ContactsActivity.this, PhoneMessageActivity.class);
            intent.putExtra("PHONE", mContact.phone);
            intent.putExtra("NAME", mContact.name);
            ContactsActivity.this.startActivity(intent);
        }
    }

    private class ContactsAdapter extends RecyclerView.Adapter<ContactHolder> {

        private List<Contact> mContacts;

        public ContactsAdapter(List<Contact> contacts) {
            this.mContacts = contacts;
        }

        @Override
        public ContactHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(ContactsActivity.this).inflate(R.layout.layout_recycler_item_contact, parent, false);
            return new ContactHolder(v);
        }

        @Override
        public void onBindViewHolder(ContactHolder holder, int position) {
            holder.onBind(mContacts.get(position));
        }

        @Override
        public int getItemCount() {
            return mContacts.size();
        }
    }

    private class Contact {
        public String phone;
        public String name;

        public Contact(String mobile, String name) {
            this.phone = mobile;
            this.name = name;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Utils.PERMISSION_CODE_READ_CONTACTS) {
            boolean isAllGranted = true;

            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false;
                    break;
                }
            }

            if (isAllGranted) {
                initialize();
            } else {
                alertPermissionRequest(permissions);
            }
        }
    }
}

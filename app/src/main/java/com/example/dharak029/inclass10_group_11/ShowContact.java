/**
 * Assignment - InClass11
 * File Name - ShowContact.java
 * Dharak Shah,Viranchi Deshpande
 */

package com.example.dharak029.inclass10_group_11;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShowContact extends AppCompatActivity {

    String uid = null;
    DatabaseReference userRef;
    DatabaseReference contactsRef;
    TextView txtUserName;
    RecyclerView recyclerView;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_contact);

        txtUserName = (TextView)findViewById(R.id.txtUserName);



        if(getIntent().getExtras()!=null){
            uid = getIntent().getExtras().getString("UID");
            name = getIntent().getExtras().getString("NAME");
        }

        txtUserName.setText(name);

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        userRef = rootRef.child(uid);
        contactsRef = userRef.child("Contacts");

        findViewById(R.id.btnCreateContact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShowContact.this,AddContact.class);
                intent.putExtra("UID",uid);
                startActivity(intent);
            }
        });

        findViewById(R.id.imgEditUser).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(ShowContact.this, EditProfile.class);
                intent.putExtra("UID", uid);
                intent.putExtra("NAME",name);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    if(dataSnapshot1.getKey().equals("fname") && name == null){
                        name = (String) dataSnapshot1.getValue();
                        txtUserName.setText(name);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        contactsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Contact> arrayList = new ArrayList<Contact>();
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    Contact contact = dataSnapshot1.getValue(Contact.class);
                    arrayList.add(contact);
                }
//                Log.d("seize",arrayList.get(0).getImgString());
                recyclerView = (RecyclerView)findViewById(R.id.recyclerThread);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ShowContact.this);
                recyclerView.setLayoutManager(mLayoutManager);
                ShowContactAdapter showContactAdapter = new ShowContactAdapter(arrayList,uid,name,ShowContact.this);
                recyclerView.setAdapter(showContactAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case R.id.action_logout:
                finish();
                Intent intent = new Intent(ShowContact.this,MainActivity.class);
                startActivity(intent);

        }

        return super.onOptionsItemSelected(item);
    }
}

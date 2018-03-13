/**
 * Assignment - InClass11
 * File Name - ShowContactAdapter.java
 * Dharak Shah,Viranchi Deshpande
 */
package com.example.dharak029.inclass10_group_11;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by dharak029 on 11/13/2017.
 */

public class ShowContactAdapter extends RecyclerView.Adapter<ShowContactAdapter.ViewHolder> {

    ArrayList<Contact> mData;
    String uid;
    Activity activity;
    String name;

    public ShowContactAdapter(ArrayList<Contact> mData,String uid,String name,Activity activity) {
        this.mData = mData;
        this.uid = uid;
        this.activity = activity;
        this.name = name;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout, parent, false);
        ShowContactAdapter.ViewHolder viewHolder = new ShowContactAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Contact contact = (Contact) mData.get(position);
        holder.txtName.setText(contact.getName());
        holder.txtEmail.setText(contact.getEmail());
        holder.txtPhone.setText(contact.getPhone());

        Picasso.with(activity)
                .load(contact.getImgString())
                .into(holder.imgViewPersonalAvatar);
        holder.imgDeleteContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                DatabaseReference userRef = rootRef.child(uid);
                DatabaseReference contactsRef = userRef.child("Contacts");
                contactsRef.child(mData.get(position).getName()).removeValue();
                mData.remove(position);
                notifyDataSetChanged();

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference firememesRef = storage.getReference(contact.getPath());
                firememesRef.delete();

            }
        });

        holder.imgEditContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.finish();
                Intent intent = new Intent(activity, EditContact.class);
                intent.putExtra("UID", uid);
                intent.putExtra("NAME",name);
                intent.putExtra("CONTACT",contact);
                activity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName;
        TextView txtPhone;
        TextView txtEmail;
        ImageView imgViewPersonalAvatar;
        ImageView imgEditContact,imgDeleteContact;

        public ViewHolder(final View itemView) {
            super(itemView);

            imgEditContact = (ImageView)itemView.findViewById(R.id.imgEditContact);
            imgDeleteContact = (ImageView)itemView.findViewById(R.id.imgDeleteContact);
            txtName = (TextView) itemView.findViewById(R.id.txtName);
            txtPhone = (TextView) itemView.findViewById(R.id.txtPhone);
            txtEmail = (TextView) itemView.findViewById(R.id.txtEmail);
            imgViewPersonalAvatar = (ImageView) itemView.findViewById(R.id.imgViewPersonalAvatar);
        }
    }
}


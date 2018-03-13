/**
 * Assignment - InClass11
 * File Name - EditProfile.java
 * Dharak Shah,Viranchi Deshpande
 */
package com.example.dharak029.inclass10_group_11;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

public class EditProfile extends AppCompatActivity {

    EditText editFname,editLname;
    ImageView imgView;
    String UID = null;
    String name = null;
    DatabaseReference userRef;
    FirebaseStorage storage = FirebaseStorage.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        editFname = (EditText)findViewById(R.id.editProfileFname);
        editLname = (EditText)findViewById(R.id.editProfileLname);

        imgView = (ImageView)findViewById(R.id.imgProfileUserImage);





        if(getIntent().getExtras()!=null){
            UID = getIntent().getExtras().getString("UID");
            name = getIntent().getExtras().getString("NAME");
        }



        final String path = "users/"+ UID+"/"+ "profile"+ ".png";
        StorageReference firememesRef = storage.getReference(path);

        String url = firememesRef.getDownloadUrl().toString();

        firememesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(EditProfile.this)
                        .load(uri.toString())
                        .into(imgView);
            }
        });

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        userRef = rootRef.child(UID);

        findViewById(R.id.btnUpdateProfile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                imgView.setDrawingCacheEnabled(true);
                imgView.buildDrawingCache();
                Bitmap bitmap = imgView.getDrawingCache();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                imgView.setDrawingCacheEnabled(false);
                byte[] data = baos.toByteArray();

                StorageReference firememesRef = storage.getReference(path);

                UploadTask uploadTask = firememesRef.putBytes(data);
                userRef.child("fname").setValue(editFname.getText().toString());
                userRef.child("lname").setValue(editLname.getText().toString());
                name = editFname.getText().toString();

                uploadTask.addOnSuccessListener(EditProfile.this,new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        String uri = downloadUrl.toString();

                        Intent intent = new Intent(EditProfile.this,ShowContact.class);
                        intent.putExtra("UID",UID);
                        intent.putExtra("NAME",name);
                        finish();
                        startActivity(intent);
                    }
                });

                Toast.makeText(EditProfile.this,"Profile updated successfully",Toast.LENGTH_LONG).show();
            }
        });

        findViewById(R.id.btnProfileCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(EditProfile.this, ShowContact.class);
                intent.putExtra("UID", UID);
                intent.putExtra("NAME",name);
                startActivity(intent);
            }
        });

        imgView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence[] items = {"Camara","Gallery"};
                AlertDialog.Builder dialog = new AlertDialog.Builder(EditProfile.this);
                dialog.setTitle("Choose Option").setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0){
                            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(takePicture, 0);
                        }
                        else{
                            Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                            getIntent.setType("image/*");

                            Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            pickIntent.setType("image/*");

                            Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
                            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

                            startActivityForResult(chooserIntent, 1);
                        }
                    }
                });

                dialog.show();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    if(dataSnapshot1.getKey().equals("fname")){
                        editFname.setText(dataSnapshot1.getValue().toString());
                    }
                    else if(dataSnapshot1.getKey().equals("lname")){
                        editLname.setText(dataSnapshot1.getValue().toString());
                    }
                    else{

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case 0:
                if(resultCode == EditProfile.RESULT_OK){
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    imgView.setImageBitmap(Bitmap.createScaledBitmap(photo, 600, 600, false));
                }

                break;
            case 1:
                if(resultCode == EditProfile.RESULT_OK){
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    imgView.setImageBitmap(Bitmap.createScaledBitmap(photo, 600, 600, false));
                }
                break;
        }
    }
}

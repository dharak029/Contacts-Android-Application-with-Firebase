/**
 * Assignment - InClass11
 * File Name - AddContact.java
 * Dharak Shah,Viranchi Deshpande
 */

package com.example.dharak029.inclass10_group_11;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class AddContact extends AppCompatActivity {


    ImageView imgEditContactImage;
    EditText edtEditName;
    EditText edtEditEmail;
    EditText edtEditPhone;

    String UID ;
    String name;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    String uri;
    final String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);



        imgEditContactImage = (ImageView) findViewById(R.id.imgContactImage);
        edtEditName = (EditText)findViewById(R.id.edtContactName);
        edtEditEmail = (EditText)findViewById(R.id.edtContactEmail);
        edtEditPhone = (EditText)findViewById(R.id.edtContactPhone);

        if(getIntent().getExtras()!=null){
            UID = getIntent().getExtras().getString("UID");
            name = getIntent().getExtras().getString("NAME");
        }

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference userRf = rootRef.child(UID);

        final Bitmap bitmap1 = ((BitmapDrawable)imgEditContactImage.getDrawable()).getBitmap();

        findViewById(R.id.btnContactAdd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bitmap bitmap2 = ((BitmapDrawable)imgEditContactImage.getDrawable()).getBitmap();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap2.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);

                if(edtEditName.getText().toString().equals("") || edtEditEmail.getText().toString().equals("")|| edtEditPhone.equals("") || bitmap2.equals(bitmap1)){
                    Toast.makeText(AddContact.this, "All inputs are mandatory", Toast.LENGTH_SHORT).show();
                }
                else if(!edtEditEmail.getText().toString().matches(EMAIL_PATTERN))
                {
                    Toast.makeText(AddContact.this, "Email should be in correct format", Toast.LENGTH_SHORT).show();
                }
                else if (edtEditPhone.length() < 10)
                {
                    Toast.makeText(AddContact.this, "Phone should be 10 character", Toast.LENGTH_SHORT).show();
                }
                else {


                    String uuid = UUID.randomUUID() + "";


                    imgEditContactImage.setDrawingCacheEnabled(true);
                    imgEditContactImage.buildDrawingCache();
                    Bitmap bitmap = imgEditContactImage.getDrawingCache();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                    imgEditContactImage.setDrawingCacheEnabled(false);
                    byte[] data = baos.toByteArray();

                    final String path = "users/" + UID + "/" + "contacts/" + uuid + ".png";
                    StorageReference firememesRef = storage.getReference(path);


                    UploadTask uploadTask = firememesRef.putBytes(data);
                    uploadTask.addOnSuccessListener(AddContact.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            @SuppressWarnings("VisibleForTests") Uri downloadUrl = taskSnapshot.getDownloadUrl();
                            uri = downloadUrl.toString();
                            Contact contact = new Contact(edtEditName.getText().toString(), edtEditEmail.getText().toString(), edtEditPhone.getText().toString(), uri, path);

                            DatabaseReference contactListRef = userRf.child("Contacts");
                            DatabaseReference contactRef = contactListRef.child(edtEditName.getText().toString());
                            contactRef.setValue(contact);

                            Intent intent = new Intent(AddContact.this, ShowContact.class);
                            intent.putExtra("UID", UID);
                            intent.putExtra("NAME", name);
                            finish();
                            startActivity(intent);
                        }
                    });
                }


            }
        });

        findViewById(R.id.btnContactCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(AddContact.this, ShowContact.class);
                intent.putExtra("UID", UID);
                intent.putExtra("NAME",name);
                startActivity(intent);
            }
        });

        imgEditContactImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CharSequence[] items = {"Camara","Gallery"};
                AlertDialog.Builder dialog = new AlertDialog.Builder(AddContact.this);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case 0:
                if(resultCode == AddContact.RESULT_OK){
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    imgEditContactImage.setImageBitmap(Bitmap.createScaledBitmap(photo, 600, 600, false));
                }

                break;
            case 1:
                if(resultCode == AddContact.RESULT_OK){
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    imgEditContactImage.setImageBitmap(Bitmap.createScaledBitmap(photo, 600, 600, false));
                }
                break;
        }
    }
}

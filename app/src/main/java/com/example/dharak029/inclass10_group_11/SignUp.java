/**
 * Assignment - InClass10
 * File Name - SignUp.java
 * Dharak Shah,Viranchi Deshpande
 */
package com.example.dharak029.inclass10_group_11;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

public class SignUp extends AppCompatActivity {

    EditText txtSignupEmail;
    EditText txtSignupFirstName;
    EditText txtSignupLastName;
    EditText txtSignupPassword;
    EditText txtSignupRepeatPwd;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    final String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ImageView imgSignUp;
    FirebaseStorage storage = FirebaseStorage.getInstance();


   // DatabaseReference rootRef = FirebaseDatabase.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        imgSignUp = (ImageView) findViewById(R.id.imgGetUserImage);


        txtSignupEmail = (EditText)findViewById(R.id.editSignupEmail);
        txtSignupFirstName = (EditText)findViewById(R.id.editSignupFname);
        txtSignupLastName = (EditText)findViewById(R.id.editSignupLname);
        txtSignupPassword = (EditText)findViewById(R.id.editSignupPassword);
        txtSignupRepeatPwd = (EditText)findViewById(R.id.editRepeatPassword);
        mAuth = FirebaseAuth.getInstance();

        imgSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence[] items = {"Camara","Gallery"};
                AlertDialog.Builder dialog = new AlertDialog.Builder(SignUp.this);
                dialog.setTitle("Choose Option").setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which == 0){
                            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(takePicture, 0);
                        }
                        else{
//                            Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                            startActivityForResult(pickPhoto , 1);
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


        final Bitmap bitmap1 = ((BitmapDrawable)imgSignUp.getDrawable()).getBitmap();



        findViewById(R.id.btnSignUpRegister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = txtSignupEmail.getText().toString();
                final String pwd = txtSignupPassword.getText().toString();
                Bitmap bitmap = ((BitmapDrawable)imgSignUp.getDrawable()).getBitmap();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);

                if(txtSignupFirstName.getText().toString().equals("") || txtSignupLastName.getText().toString().equals("")|| email.equals("")|| pwd.equals("") || txtSignupRepeatPwd.getText().toString().equals("") || bitmap.equals(bitmap1)){
                    Toast.makeText(SignUp.this, "All inputs are mandatory", Toast.LENGTH_SHORT).show();
                }
                else if(!email.matches(EMAIL_PATTERN))
                {
                    Toast.makeText(SignUp.this, "Email should be in correct format", Toast.LENGTH_SHORT).show();
                }
                else if (pwd.length() < 8)
                {
                    Toast.makeText(SignUp.this, "Password should be greater than 6 character", Toast.LENGTH_SHORT).show();
                }
                else{

                    if(pwd.equals(txtSignupRepeatPwd.getText().toString())){
                        mAuth.createUserWithEmailAndPassword(email,pwd )
                                .addOnCompleteListener(SignUp.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        Log.d("demo", "createUserWithEmail:onComplete:" + task.isSuccessful());

                                        // If sign in fails, display a message to the user. If sign in succeeds
                                        // the auth state listener will be notified and logic to handle the
                                        // signed in user can be handled in the listener.
                                        if (!task.isSuccessful()) {
                                            Toast.makeText(SignUp.this,"Failed Signup",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                        else{

                                            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                                            User user = new User(txtSignupFirstName.getText().toString(), txtSignupLastName.getText().toString(), email);
                                            DatabaseReference userRef = rootRef.child(mAuth.getCurrentUser().getUid());
                                            userRef.setValue(user);
                                            imgSignUp.setDrawingCacheEnabled(true);
                                            imgSignUp.buildDrawingCache();
                                            Bitmap bitmap = imgSignUp.getDrawingCache();
                                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                                            imgSignUp.setDrawingCacheEnabled(false);
                                            byte[] data = baos.toByteArray();

                                            String path = "users/"+ mAuth.getCurrentUser().getUid()+"/"+ "profile"+ ".png";
                                            StorageReference firememesRef = storage.getReference(path);

                                            UploadTask uploadTask = firememesRef.putBytes(data);
                                            uploadTask.addOnSuccessListener(SignUp.this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                }
                                            });
                                            Toast.makeText(SignUp.this, "Signup Success",
                                                    Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(SignUp.this, ShowContact.class);
                                            intent.putExtra("UID", mAuth.getCurrentUser().getUid());
                                            startActivity(intent);

                                        }

                                        // ...
                                    }
                                });
                    }
                    else{
                        Toast.makeText(SignUp.this,"Password Not Matched",
                                Toast.LENGTH_SHORT).show();
                    }
                }



            }
        });




        findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(SignUp.this,MainActivity.class);
                startActivity(intent);
            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case 0:
                if(resultCode == SignUp.RESULT_OK){
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    ((ImageView)findViewById(R.id.imgGetUserImage)).setImageBitmap(Bitmap.createScaledBitmap(photo, 600, 600, false));
                }

                break;
            case 1:
                if(resultCode == SignUp.RESULT_OK){
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    ((ImageView)findViewById(R.id.imgGetUserImage)).setImageBitmap(Bitmap.createScaledBitmap(photo, 600, 600, false));
                }
                break;
        }
    }
}

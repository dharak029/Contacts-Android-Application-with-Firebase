/**
 * Assignment - InClass11
 * File Name - MainActivity.java
 * Dharak Shah,Viranchi Deshpande
 */
package com.example.dharak029.inclass10_group_11;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,GoogleApiClient.OnConnectionFailedListener{

    EditText txtEmail;
    EditText txtPassword;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    SignInButton gsignInButton;
    GoogleApiClient googleApiClient;
    private final int SIGN_IN = 9001;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail().build();

        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API,googleSignInOptions)
                .build();

        txtEmail = (EditText)findViewById(R.id.editTextEmail);
        txtPassword = (EditText)findViewById(R.id.editTextPass);
        mAuth = FirebaseAuth.getInstance();

        findViewById(R.id.btnGoogleSignIn).setOnClickListener(this);

        findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = txtEmail.getText().toString();
                String password = txtPassword.getText().toString();

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d("demo", "signInWithEmail:onComplete:" + task.isSuccessful());

                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Log.w("demo", "signInWithEmail:failed", task.getException());
                                    Toast.makeText(MainActivity.this,"SignIn Failed",
                                            Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Intent intent = new Intent(MainActivity.this,ShowContact.class);
                                    intent.putExtra("UID",mAuth.getCurrentUser().getUid());
                                    startActivity(intent);
                                }

                                // ...
                            }
                        });

            }
        });

        findViewById(R.id.txtSignup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,SignUp.class);
                startActivity(intent);
            }
        });

    }

    public void signIn(){
        Intent intent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(intent,SIGN_IN);
    }

    @Override
    public void onClick(View view) {
        signIn();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    public void handleSignInResult(GoogleSignInResult result){

        if(result.isSuccess()){
            GoogleSignInAccount account = result.getSignInAccount();
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            User user = new User(account.getGivenName(), account.getFamilyName(), account.getEmail());
            DatabaseReference userRef = rootRef.child(account.getId());
//            if(userRef.getKey() ==null){
                userRef.setValue(user);
//            }
            Intent intent = new Intent(MainActivity.this,ShowContact.class);
            intent.putExtra("UID",account.getId());
            intent.putExtra("NAME",account.getGivenName());
            startActivity(intent);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}

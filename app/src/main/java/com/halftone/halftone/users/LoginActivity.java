package com.halftone.halftone.users;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.halftone.halftone.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.halftone.halftone.layout.MainActivity;

public class LoginActivity extends Activity {

    private EditText emailField, passwordField;
    private Button loginButton, registerButton;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

            firebaseAuth = FirebaseAuth.getInstance();

            emailField = (EditText) findViewById(R.id.emailField);
            passwordField = (EditText) findViewById(R.id.passwordField);
            loginButton = (Button) findViewById(R.id.loginField);
            registerButton = (Button) findViewById(R.id.registerField);

            enableFunctionality();
    }

    public void enableFunctionality(  ){
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if( firebaseAuth.getCurrentUser() != null ) {
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }
            }
        };

        loginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                if(checkValidation()) startSignIn();
            }
        });
        registerButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

    }

    public boolean checkValidation(){
        boolean validFields = true;
        String email =  emailField.getText().toString();
        String password = passwordField.getText().toString();
        if( TextUtils.isEmpty( email ) ){
            validFields = false;
            emailField.setError("You must provide an email.");
        }
        if( TextUtils.isEmpty( password ) ){
            validFields = false;
            passwordField.setError("Please enter your email.");
        }
        return validFields;
    }

    @Override
    protected void onStart(){
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseAuthStateListener);
    }

    private void startSignIn() {
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(LoginActivity.this, "You must supply a valid email and password.", Toast.LENGTH_LONG).show();
        } else {
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Incorrect details suppplied.", Toast.LENGTH_LONG).show();
                    }else {
                        System.out.println(
                                FirebaseAuth.getInstance().getCurrentUser().getUid()
                        );
                    }
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        // https://stackoverflow.com/questions/3141996/android-how-to-override-the-back-button-so-it-doesnt-finish-my-activity
        if( firebaseAuth.getCurrentUser() != null ){
            firebaseAuth.signOut();
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}
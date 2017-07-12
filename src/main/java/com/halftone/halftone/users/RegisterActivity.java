package com.halftone.halftone.users;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.halftone.halftone.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.halftone.halftone.content.Post;
import com.halftone.halftone.layout.MainActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends Activity {

    private EditText regEmailField, regPasswordField, regRePasswordField, regUsernameField;
    private Button registerButton, loginButton;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        regUsernameField = (EditText) findViewById( R.id.regUsernameField );
        regEmailField = (EditText) findViewById( R.id.regEmailField );
        regPasswordField = (EditText) findViewById( R.id.regPasswordField );
        regRePasswordField = (EditText) findViewById( R.id.regRePasswordField );
        registerButton = (Button) findViewById( R.id.registerButton );
        loginButton = (Button) findViewById(R.id.loginButton);
        enableValidation("username");
        enableValidation("email");
        enableValidation("password");
        enableValidation("re-password");

        firebaseAuth = FirebaseAuth.getInstance();

        loginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String username = regUsernameField.getText().toString().trim();
                final String email = regEmailField.getText().toString().trim();
                String password = regPasswordField.getText().toString().trim();
                String rePassword = regRePasswordField.getText().toString().trim();

                if(TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(rePassword)){
                    Toast.makeText(RegisterActivity.this, "You must supply a valid email and password.", Toast.LENGTH_LONG).show();
                }else {
                    if( true ){ // Check if user already exists - no user doesn't exist
                        if( !password.equals(rePassword) ){ // Check if passwords match - no they don't
                            // regPasswordField.setError("Passwords must match");
                            // regRePasswordField.setError("Passwords must match");
                            Toast.makeText(RegisterActivity.this, "Passwords must match", Toast.LENGTH_LONG).show();
                        }else {
                            if( 2 == 3 ){ // Check if the password is strong enough - no it's not
                                Toast.makeText(RegisterActivity.this, "Password isn't strong enough", Toast.LENGTH_LONG).show();
                            }else {
                                Pattern p = Pattern.compile("[\\w\\-\\.\\+]+\\@[a-zA-Z0-9\\.\\-]+\\.[a-zA-z0-9]{2,4}");
                                Matcher m = p.matcher( regEmailField.getText().toString() );
                                if( !m.find() ){ // Check email matches some reg ex - not valid email
                                    regEmailField.setError("Invalid email.");
                                }else {
                                    firebaseAuth
                                            .createUserWithEmailAndPassword(email, password)
                                            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                                @Override
                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                    if(!task.isSuccessful()){
                                                        //firebaseAuth.getCurrentUser().getUid();
                                                        Toast.makeText(RegisterActivity.this, "error registering.", Toast.LENGTH_LONG).show();
                                                    }else {
                                                        String uid = task.getResult().getUser().getUid();
                                                        System.out.println("here");
                                                        // String username = regUsernameField.getText().toString().toLowerCase().trim();
                                                        // Update usernames table
                                                        updateUsernameTable(uid, regUsernameField.getText().toString().toLowerCase().trim());
                                                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                                        finish();
                                                    }
                                                }
                                            });
                                }
                            }
                        }
                    } else { // User already exists
                        Toast.makeText(RegisterActivity.this, "User already exists", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }
    boolean passwordsGood = false;
    public void enableValidation(final String textField ){

        // REGEX FOR EMAIL:  /^ [\w\-\.\+]+\@[a-zA-Z0-9\.\-]+\.[a-zA-z0-9]{2,4} $/
        TextWatcher fieldValidatorTextWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if( passwordsGood ){
                    if( (textField.equals("password") || textField.equals("re-password"))
                            && !regPasswordField.getText().toString().equals(regRePasswordField.getText().toString())){
                        regPasswordField.setError("Passwords must match.");
                        regRePasswordField.setError("Passwords must match.");
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                View view = getCurrentFocus(); // For minimising the keyboard
                if( textField.equals( "email" ) ){
                    Pattern p = Pattern.compile("[\\w\\-\\.\\+]+\\@[a-zA-Z0-9\\.\\-]+\\.[a-zA-z0-9]{2,4}");
                    Matcher m = p.matcher( regEmailField.getText().toString() );
                    if( !m.find() ){
                        regEmailField.setError("Invalid email.");
                    }
                }else if( textField.equals( "password" ) ){
                    Pattern capitalLetter = Pattern.compile(".*[A-Z].*");
                    Pattern numericChar = Pattern.compile(".*[0-9].*");
                    String password = regPasswordField.getText().toString();
                    Matcher cap = capitalLetter.matcher(password);
                    Matcher number = numericChar.matcher(password);
                    boolean passValid = true;

                    String passwordValidationError = "Valid passwords have:";
                    if( !cap.find() ){
                        passwordValidationError += "\n* a capital letter";
                        passValid = false;
                        //regPasswordField.setError("Password must include:\nA capital letter\nA number\nMore than 5 characters long");
                    }
                    if( !number.find() ) {
                        passwordValidationError += "\n* a number";
                        passValid = false;
                    }
                    if( !(password.length() > 5) ){
                        passwordValidationError += "\n* more than 5 characters";
                        passValid = false;
                    }
                    if( passValid ){
                        if( !regPasswordField.getText().toString().equals( regRePasswordField.getText().toString() ) ){
                            regPasswordField.setError("Passwords must match.");
                            regRePasswordField.setError("Passwords must match.");
                        }else {
                            regRePasswordField.setError(null);
                            if( view != null ){
                                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                            }
                        }
                    }else {
                        regPasswordField.setError(passwordValidationError);
                    }
                }else if( textField.equals("re-password") ){
                    if( !regRePasswordField.getText().toString().equals( regPasswordField.getText().toString() ) ){
                        regPasswordField.setError("Passwords must match.");
                        regRePasswordField.setError("Passwords must match.");
                    }else {
                        regPasswordField.setError(null);
                        if( view != null ){
                            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }
                    }
                }else if( textField.equals( "username" ) ){
                    if( regUsernameField.getText().toString().length() > 15 ){
                        regUsernameField.setError("A username should be less than 15 characters");
                    }
                }
            }
        };
        if( textField.equals( "email" ) ){
            regEmailField.addTextChangedListener(fieldValidatorTextWatcher);
        }else if( textField.equals( "password" ) ){
            regPasswordField.addTextChangedListener( fieldValidatorTextWatcher );
        }else if( textField.equals("re-password") ){
            regRePasswordField.addTextChangedListener( fieldValidatorTextWatcher );
        }
    }

    public void updateUsernameTable(String uid, String username){
       // String uid = firebaseUser.getUid();
        DatabaseReference usernamesDR = databaseReference.child("Usernames").child(uid);
        // DatabaseReference usernameSubDR = usernamesDR.child(uid);
        DatabaseReference usernamesDR1 = usernamesDR.push();
        usernamesDR1.setValue(new Username(uid, username));
    }

}

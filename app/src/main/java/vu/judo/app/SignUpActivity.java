package vu.judo.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class SignUpActivity extends AppCompatActivity {

    static final String TAG = "SignUp";

    FirebaseAuth firebaseAuth;
    EditText nameCapture, emailAddressCapture, passwordCapture, passwordReenterCapture;
    String name, emailAddress, password, passwordReenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {
            Log.e(TAG, "getSupportActionBar().setDisplayHomeAsUpEnabled:failure", e);
        }
        firebaseAuth = FirebaseAuth.getInstance();

        nameCapture = findViewById(R.id.signUpName);
        emailAddressCapture = findViewById(R.id.signUpEmailAddress);
        passwordCapture = findViewById(R.id.signUpPassword);
        passwordReenterCapture = findViewById(R.id.signUpPasswordConfirmation);

        //Clear any existing text on creation
        nameCapture.getText().clear();
        emailAddressCapture.getText().clear();
        passwordCapture.getText().clear();
        passwordReenterCapture.getText().clear();
    }

    public void submitSignUp(View view) {
        name = nameCapture.getText().toString();
        emailAddress = emailAddressCapture.getText().toString();
        password = passwordCapture.getText().toString();
        passwordReenter = passwordReenterCapture.getText().toString();

        //GREY OUT INPUT AND SIGN UP BUTTON SO THEY CAN'T BE USED. DISPLAY LOADING SPINNER
        //...

        //Check if name is valid
        if (name.length() > 2) {
            //Check if emailAddress is a valid email address
            if (android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
                //Make sure password & password reenter are not empty
                if (!password.isEmpty() && !passwordReenter.isEmpty()) {
                    //Check if password matches re-entered password
                    if (password.equals(passwordReenter)) {
                        //Sign up for account using email and password
                        firebaseAuth.createUserWithEmailAndPassword(emailAddress, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    //Sign up is successful, return to Log In Screen
                                    startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                                    Log.d(TAG, "createUserWithEmail:success");
                                } else {
                                    //Sign up is unsuccessful, notify user why
                                    try {
                                        throw task.getException();
                                    } catch (FirebaseAuthWeakPasswordException weakPassword) {
                                        //This password is too weak
                                        Toast.makeText(SignUpActivity.this, "This password is too weak", Toast.LENGTH_LONG).show();
                                        Log.w(TAG, "createUserWithEmail:failure", weakPassword);
                                    } catch (FirebaseAuthUserCollisionException userAlreadyExists) {
                                        //User with this email already exists
                                        Toast.makeText(SignUpActivity.this, "A user with this email address already exists", Toast.LENGTH_LONG).show();
                                        Log.w(TAG, "createUserWithEmail:failure", userAlreadyExists);
                                    } catch (Exception e) {
                                        //Unknown Error
                                        Toast.makeText(SignUpActivity.this, "Authentication failed. Please try again shortly", Toast.LENGTH_LONG).show();
                                        Log.e(TAG, "createUserWithEmail:failure", e);
                                    }
                                }
                            }
                        });
                    } else {
                        //Passwords do not match
                        Toast.makeText(SignUpActivity.this, "Passwords do not match", Toast.LENGTH_LONG).show();
                    }
                } else {
                    //Password or password reeneter is empty
                    Toast.makeText(SignUpActivity.this, "Please enter a password", Toast.LENGTH_LONG).show();
                }
            } else {
                //Invalid email address
                Toast.makeText(SignUpActivity.this, "Invalid email address", Toast.LENGTH_LONG).show();
            }
        } else {
            //Invalid name
            Toast.makeText(SignUpActivity.this, "Please enter a valid name", Toast.LENGTH_LONG).show();
        }
    }
}

package vu.judo.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
// import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    static final String TAG = "SignUp";

    String firstName, lastName, emailAddress, password, passwordReenter;
    EditText firstNameCapture, lastNameCapture, emailAddressCapture, passwordCapture, passwordReenterCapture;
    Button signUpButton;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {
            // Log.e(TAG, "getSupportActionBar().setDisplayHomeAsUpEnabled:failure", e);
        }
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        firstNameCapture = findViewById(R.id.signUpFirstName);
        lastNameCapture = findViewById(R.id.signUpLastName);
        emailAddressCapture = findViewById(R.id.signUpEmailAddress);
        passwordCapture = findViewById(R.id.signUpPassword);
        passwordReenterCapture = findViewById(R.id.signUpPasswordConfirmation);
        signUpButton = findViewById(R.id.signUpButton);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Clear any existing text
        firstNameCapture.getText().clear();
        lastNameCapture.getText().clear();
        emailAddressCapture.getText().clear();
        passwordCapture.getText().clear();
        passwordReenterCapture.getText().clear();
    }

    public void submitSignUp(View view) {
        firstName = firstNameCapture.getText().toString();
        lastName = lastNameCapture.getText().toString();
        emailAddress = emailAddressCapture.getText().toString();
        password = passwordCapture.getText().toString();
        passwordReenter = passwordReenterCapture.getText().toString();

        //Disable UI while attempting to Sign Up
        view.setClickable(false);
        firstNameCapture.setEnabled(false);
        lastNameCapture.setEnabled(false);
        emailAddressCapture.setEnabled(false);
        passwordCapture.setEnabled(false);
        passwordReenterCapture.setEnabled(false);

        //Check if name is valid
        if (firstName.length() > 2 && lastName.length() > 2) {
            //Check if emailAddress is a valid email address
            if (android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
                //Make sure password & password reenter are not empty
                if (!password.isEmpty() && !passwordReenter.isEmpty()) {
                    //Check if password matches re-entered password
                    if (password.equals(passwordReenter)) {
                        //Sign up for account using email and password
                        firebaseAuth.createUserWithEmailAndPassword(emailAddress, password).addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                //Sign up successful, create user document
                                Map<String, Object> user = new HashMap<>();
                                user.put("email", emailAddress);
                                user.put("firstName", firstName);
                                user.put("lastName", lastName);
                                user.put("score", 0);
                                // Add a new document with a generated ID
                                db.collection("users").document(emailAddress).set(user).addOnSuccessListener(aVoid -> {
                                    //Creation of user document successful, return to log in screen
                                    Toast.makeText(SignUpActivity.this, "Sign up successful!", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                                    // Log.d(TAG, "DocumentSnapshot successfully written!");
                                }).addOnFailureListener(e -> {
                                    //Creation of user document failed, notify user
                                    Toast.makeText(SignUpActivity.this, "Failed to create user. Please contact you system administrator", Toast.LENGTH_LONG).show();
                                    // Log.w(TAG, "Error writing document", e);
                                });
                                // Log.d(TAG, "createUserWithEmail:success");
                            } else {
                                //Sign up is unsuccessful, notify user why
                                try {
                                    throw task.getException();
                                } catch (FirebaseAuthWeakPasswordException weakPassword) {
                                    //This password is too weak
                                    Toast.makeText(SignUpActivity.this, "This password is too weak", Toast.LENGTH_LONG).show();
                                    // Log.w(TAG, "createUserWithEmail:failure", weakPassword);
                                } catch (FirebaseAuthUserCollisionException userAlreadyExists) {
                                    //User with this email already exists
                                    Toast.makeText(SignUpActivity.this, "A user with this email address already exists", Toast.LENGTH_LONG).show();
                                    // Log.w(TAG, "createUserWithEmail:failure", userAlreadyExists);
                                } catch (Exception e) {
                                    //Unknown Error
                                    Toast.makeText(SignUpActivity.this, "Sign up failed. Please try again shortly", Toast.LENGTH_LONG).show();
                                    // Log.e(TAG, "createUserWithEmail:failure", e);
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

        //Whether successful or not, re-enable UI
        view.setClickable(true);
        firstNameCapture.setEnabled(true);
        lastNameCapture.setEnabled(true);
        emailAddressCapture.setEnabled(true);
        passwordCapture.setEnabled(true);
        passwordReenterCapture.setEnabled(true);
    }
}

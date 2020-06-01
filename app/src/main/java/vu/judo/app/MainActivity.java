package vu.judo.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class MainActivity extends AppCompatActivity {

    static final String TAG = "Main";

    String emailAddress, password;

    EditText emailAddressCapture, passwordCapture;
    Button logInButton;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();

        emailAddressCapture = findViewById(R.id.logInEmailAddress);
        passwordCapture = findViewById(R.id.logInPassword);
        logInButton = findViewById(R.id.logInButton);

        //Brings cursor to beginning of entry upon focus
        emailAddressCapture.setOnFocusChangeListener((v, hasFocus) -> emailAddressCapture.setSelection(0));
        passwordCapture.setOnFocusChangeListener((v, hasFocus) -> passwordCapture.setSelection(0));
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Clear any existing text
        emailAddressCapture.getText().clear();
        passwordCapture.getText().clear();
    }

    @SuppressWarnings("ConstantConditions")
    public void logIn(View view) {
        emailAddress = emailAddressCapture.getText().toString();
        password = passwordCapture.getText().toString();

        //Disable UI while attempting to Log In
        view.setClickable(false);
        emailAddressCapture.setEnabled(false);
        passwordCapture.setEnabled(false);

        //Check if emailAddress is a valid email address
        if (android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
            //Make sure password is not empty
            if (!password.isEmpty()) {
                //Sign into FireBaseDB using email and password
                firebaseAuth.signInWithEmailAndPassword(emailAddress, password).addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        //Sign in is successful, navigate to home screen
                        Toast.makeText(MainActivity.this, "Welcome!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, HomeActivity.class));
                        Log.d(TAG, "signInWithEmail:success");
                    } else {
                        //Sign in is unsuccessful, notify user why
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthInvalidUserException noExistingUser) {
                            //There is no account with this email
                            Toast.makeText(MainActivity.this, "No user exists with this email address", Toast.LENGTH_LONG).show();
                            Log.w(TAG, "signInWithEmail:failure", noExistingUser);
                        } catch (FirebaseAuthInvalidCredentialsException wrongPassword) {
                            //Incorrect Password
                            Toast.makeText(MainActivity.this, "Incorrect Password", Toast.LENGTH_LONG).show();
                            Log.w(TAG, "signInWithEmail:failure", wrongPassword);
                        } catch (Exception e) {
                            //Unknown Error
                            Toast.makeText(MainActivity.this, "Authentication failed. Please try again shortly", Toast.LENGTH_LONG).show();
                            Log.e(TAG, "signInWithEmail:failure" + e.getMessage(), e);
                        }
                    }
                });
            } else {
                //Password is empty
                Toast.makeText(MainActivity.this, "Please enter your password", Toast.LENGTH_LONG).show();
            }
        } else {
            //Invalid Email Address
            Toast.makeText(MainActivity.this, "Invalid email address", Toast.LENGTH_LONG).show();
        }

        //Whether successful or not, re-enable UI
        view.setClickable(true);
        emailAddressCapture.setEnabled(true);
        passwordCapture.setEnabled(true);
    }

    public void signUp(View view) {
        startActivity(new Intent(this, SignUpActivity.class));
    }

    public void forgotPassword(View view) {
        startActivity(new Intent(this, ForgotPasswordActivity.class));
    }
}

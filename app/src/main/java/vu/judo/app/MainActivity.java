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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class MainActivity extends AppCompatActivity {

    static final String TAG = "Main";

    FirebaseAuth firebaseAuth;
    EditText emailAddressCapture, passwordCapture;
    String emailAddress, password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();

        emailAddressCapture = findViewById(R.id.logInEmailAddress);
        passwordCapture = findViewById(R.id.logInPassword);
    }

    public void logIn(View view) {
        emailAddress = emailAddressCapture.getText().toString();
        password = passwordCapture.getText().toString();

        //Check if emailAddress is a valid email address
        if (android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
            //Sign into FireBaseDB using email and password
            firebaseAuth.signInWithEmailAndPassword(emailAddress, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        //Sign in is successful, log the success for later
                        startActivity(new Intent(MainActivity.this, HomeActivity.class));
                        Log.d(TAG, "signInWithEmail:success");
                    } else {
                        //Sign in is unsuccessful, notify user why
                        try {
                            throw task.getException();
                        } catch (FirebaseAuthInvalidUserException noExistingUser) {
                            //There is no account with this email
                            Toast.makeText(MainActivity.this, "No user exists with this email address", Toast.LENGTH_SHORT).show();
                            Log.w(TAG, "signInWithEmail:failure", noExistingUser);
                        } catch (FirebaseAuthInvalidCredentialsException wrongPassword) {
                            //Incorrect Password
                            Toast.makeText(MainActivity.this, "Incorrect Password", Toast.LENGTH_SHORT).show();
                            Log.w(TAG, "signInWithEmail:failure", wrongPassword);
                        } catch (Exception e) {
                            //Unknown Error
                            Toast.makeText(MainActivity.this, "Authentication failed. Please try again shortly", Toast.LENGTH_SHORT).show();
                            Log.e(TAG, "signInWithEmail:failure" + e.getMessage(), e);
                        }
                    }
                }
            });
        } else {
            //Invalid Email Address
            Toast.makeText(MainActivity.this, "Invalid email address", Toast.LENGTH_SHORT).show();
        }
    }

    public void signUp(View view) {
        startActivity(new Intent(this, SignUpActivity.class));
    }

    public void forgotPassword(View view) {
        startActivity(new Intent(this, ForgotPasswordActivity.class));
    }
}

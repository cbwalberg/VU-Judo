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
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class ForgotPasswordActivity extends AppCompatActivity {

   // static final String TAG = "ForgotPassword";

    String emailAddress;
    EditText emailAddressCapture;
    Button forgotPasswordButton;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {
            // Log.e(TAG, "getSupportActionBar().setDisplayHomeAsUpEnabled:failure", e);
        }
        firebaseAuth = FirebaseAuth.getInstance();

        emailAddressCapture = findViewById(R.id.forgotPassEmailAddress);
        forgotPasswordButton = findViewById(R.id.forgotPassConfirmButton);

        // Clear any existing text on creation
        emailAddressCapture.getText().clear();
    }

    public void confirmEmailAddress(View view) {
        emailAddress = emailAddressCapture.getText().toString();

        // Disable UI while attempting to send email
        forgotPasswordButton.setEnabled(false);
        emailAddressCapture.setEnabled(false);

        // Check if emailAddress is a valid email address
        if (android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
            // Send email to user to reset their password
            firebaseAuth.sendPasswordResetEmail(emailAddress).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Email successfully sent
                    Toast.makeText(ForgotPasswordActivity.this, "Email sent", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(ForgotPasswordActivity.this, MainActivity.class));
                    // Log.d(TAG, "Reset Password Email sent.");
                } else {
                    // Email did not send, notify user why
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthInvalidUserException noExistingUser) {
                        // There is no account with this email
                        Toast.makeText(ForgotPasswordActivity.this, "No user exists with this email address", Toast.LENGTH_LONG).show();
                        // Log.w(TAG, "Reset Password Email sent.");
                    } catch (Exception e) {
                        // Unknown error
                        Toast.makeText(ForgotPasswordActivity.this, "Failed to send email. Please try again shortly", Toast.LENGTH_LONG).show();
                        // Log.e(TAG, "sendPasswordResetEmail:failure", e);
                    }
                }
            });
        } else {
            // Invalid email address
            Toast.makeText(ForgotPasswordActivity.this, "Invalid email address", Toast.LENGTH_LONG).show();
        }

        // Whether successful or not, re-enable UI
        forgotPasswordButton.setEnabled(false);
        emailAddressCapture.setEnabled(false);
    }
}

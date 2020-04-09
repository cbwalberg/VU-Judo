package vu.judo.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class ForgotPasswordActivity extends AppCompatActivity {

    EditText emailAddressCapture;
    String emailAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        emailAddressCapture = findViewById(R.id.forgotPassEmailAddress);
    }

    public void confirmEmailAddress(View view) {
        emailAddress = emailAddressCapture.getText().toString();

        //Check if emailAddress is a valid email address
        if (android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
            //Query DB to check if emailAddress is tied to existing account
            if (true) {
                startActivity(new Intent(this, ResetPasswordActivity.class));
            } else {
                //There is no account tied to this email
            }
        } else {
            //invalid email address
        }
    }
}

package vu.judo.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class SignUpActivity extends AppCompatActivity {

    EditText nameCapture, emailAddressCapture, passwordCapture, passwordReenterCapture;
    String name, emailAddress, password, passwordReenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        nameCapture = findViewById(R.id.signUpName);
        emailAddressCapture = findViewById(R.id.signUpEmailAddress);
        passwordCapture = findViewById(R.id.signUpPassword);
        passwordReenterCapture = findViewById(R.id.signUpPasswordConfirmation);
    }

    public void submitSignUp(View view) {
        name = nameCapture.getText().toString();
        emailAddress = emailAddressCapture.getText().toString();
        password = passwordCapture.getText().toString();
        passwordReenter = passwordReenterCapture.getText().toString();

        //Add security questions


        //Check if emailAddress is a valid email address
        if (android.util.Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
            //Query DB to check if emailAddress is tied to existing account
            if (true) {
                if (password.equals(passwordReenter)) {
                    //Write info to DB and return to Log In Screen
                    //write info to DB
                    startActivity(new Intent(this, MainActivity.class));
                } else {
                    //passwords do not match
                }
            } else {
                //An account for this email already exists
            }
        } else {
            //invalid email address
        }
    }
}

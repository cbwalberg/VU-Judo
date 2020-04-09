package vu.judo.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    EditText emailAddressCapture, passwordCapture;
    String emailAddress, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailAddressCapture = findViewById(R.id.logInEmailAddress);
        passwordCapture = findViewById(R.id.logInPassword);
    }

    public void logIn(View view) {
        emailAddress = emailAddressCapture.getText().toString();
        password = passwordCapture.getText().toString();

        //Query DB to validate credentials
        if (true) {
            startActivity(new Intent(this, HomeActivity.class));
        } else {
            //incorrect email or password
        }

    }

    public void signUp(View view) {
        startActivity(new Intent(this, SignUpActivity.class));
    }

    public void forgotPassword(View view) {
        startActivity(new Intent(this, ForgotPasswordActivity.class));
    }
}

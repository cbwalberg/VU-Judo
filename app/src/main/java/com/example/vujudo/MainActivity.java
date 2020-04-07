package com.example.vujudo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.vujudo.RealmModel.User;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    /**
     * REALM RESOURCES:
     * https://www.youtube.com/watch?v=x_5Ifs8kIrI
     */

    EditText emailAddressCapture;
    EditText passwordCapture;
    String emailAddress;
    String password;

    Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailAddressCapture = findViewById(R.id.logInEmailAddress);
        passwordCapture = findViewById(R.id.logInPassword);

        realm = Realm.getDefaultInstance();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    public void logInAttempt(View view) {
        emailAddress = emailAddressCapture.getText().toString();
        password = passwordCapture.getText().toString();

        //Validate credentials
        RealmResults<User> users = realm.where(User.class).equalTo("emailAddress", emailAddress).and().equalTo("password", password).findAll();

        if (!users.isEmpty()) {
            startActivity(new Intent(this, HomeActivity.class));
        } else {
            //display error message: incorrect email or password
        }

    }

    public void signUp(View view) {
        startActivity(new Intent(this, SignUpActivity.class));
    }
}

package vu.judo.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivity extends AppCompatActivity {

    static final String TAG = "Home";

    int score, rank;
    String email, firstName;
    TextView userNameDisplay;
    FirebaseFirestore db;
    FirebaseUser userAuth;
    DocumentReference userDoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        userNameDisplay = findViewById(R.id.homeUserName);

        userAuth = FirebaseAuth.getInstance().getCurrentUser();
        email = userAuth.getEmail();

        db = FirebaseFirestore.getInstance();
        userDoc = db.collection("users").document(email);
        userDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        firstName = document.getString("firstName");
                        score =  document.getLong("score").intValue();

                        //Sort array of all user scores from highest to lowest, index + 1 of score = rank. Check for ties in score: if index != 0, while score == next highest score in array, rank = previous index...
                        //...

                        userNameDisplay.setText(firstName);
                        //Set score display to score variable
                        //...
                        //Set rank display to rank variable
                        //...
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "Failed to find user information. Please restart application", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
}

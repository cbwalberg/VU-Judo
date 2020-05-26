package vu.judo.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class LeaderboardActivity extends AppCompatActivity {

    //https://www.youtube.com/watch?v=NhiUTjm2BrE

    static final String TAG = "Leaderboard";

    Map<String, Integer> leaderboard; //key = email, val = score

    ListView top3List, leaderboardList;

    FirebaseFirestore db;
    CollectionReference users;

    @Override
    @SuppressWarnings("ConstantConditions")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {
            Log.e(TAG, "getSupportActionBar().setDisplayHomeAsUpEnabled:failure", e);
        }

        leaderboard = new HashMap<>();
        top3List = findViewById(R.id.top3List);
        leaderboardList = findViewById(R.id.leaderboardList);

        db = FirebaseFirestore.getInstance();
        users = db.collection("users");
        users.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }
}

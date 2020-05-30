package vu.judo.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LeaderboardActivity extends AppCompatActivity {

    static final String TAG = "Leaderboard";

    User temp;
    ArrayList<User> leaderboardList, top3List;

    ListView top3ListView, leaderboardListView;

    FirebaseFirestore db;
    CollectionReference users, leaderboard;

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
        leaderboardList = new ArrayList<>();
        top3List = new ArrayList<>();

        top3ListView = findViewById(R.id.top3List);
        leaderboardListView = findViewById(R.id.leaderboardList);

        db = FirebaseFirestore.getInstance();
        leaderboard = db.collection("leaderboard");
        users = db.collection("users");

        leaderboard.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    //find most recent sunday: https://stackoverflow.com/questions/12783102/how-to-get-the-last-sunday-before-current-date
                    //Take only top three from last week's leaderboard
                }

                //https://github.com/codepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView
                ArrayAdapter<User> top3Adapter = new ArrayAdapter<User>(this, android.R.layout.simple_list_item_1, top3List);
                top3ListView.setAdapter(top3Adapter);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        users.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    temp = new User(document.getString("email"), document.getString("firstName"), document.getString("lastName"), document.getLong("score").intValue());
                    leaderboard.add(temp);
                }

                //https://github.com/codepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView
                ArrayAdapter<User> leaderboardAdapter = new ArrayAdapter<User>(this, android.R.layout.simple_list_item_1, leaderboardList);
                leaderboardListView.setAdapter(leaderboardAdapter);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LeaderboardActivity.this, "Failed to find users", Toast.LENGTH_LONG).show();
                Log.d(TAG, "Error connecting to users collection from database ", e);
            }
        });
    }
}

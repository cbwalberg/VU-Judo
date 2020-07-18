package vu.judo.app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class LeaderboardActivity extends AppCompatActivity {

    static final String TAG = "Leaderboard";

    String lastWeekLeaderboardName;

    User temp;
    ArrayList<User> leaderboardList, top3List;

    ListView top3ListView, leaderboardListView;

    FirebaseFirestore db;
    DocumentReference lastWeekLeaderboard;
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
        leaderboardList = new ArrayList<>();
        top3List = new ArrayList<>();

        top3ListView = findViewById(R.id.top3List);
        leaderboardListView = findViewById(R.id.leaderboardList);

        db = FirebaseFirestore.getInstance();

        //Find leaderboard from most recent sunday (https://stackoverflow.com/questions/12783102/how-to-get-the-last-sunday-before-current-date)
        Calendar lastSunday = Calendar.getInstance();

        //If today is Sunday, get the Sunday before, otherwise get the most recent Sunday
        //TEST EVERYDAY OF THE WEEK
        if (lastSunday.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            lastSunday.add(Calendar.DAY_OF_WEEK, lastSunday.get(Calendar.DAY_OF_WEEK)-8);
        } else {
            lastSunday.add(Calendar.DAY_OF_WEEK, -(lastSunday.get(Calendar.DAY_OF_WEEK)-Calendar.SUNDAY));
        }

        lastWeekLeaderboardName = "Week of " + (lastSunday.get(Calendar.MONTH)+1) + "-" + lastSunday.get(Calendar.DATE) + "-" + lastSunday.get(Calendar.YEAR);
        Toast.makeText(LeaderboardActivity.this, lastWeekLeaderboardName, Toast.LENGTH_SHORT).show();

        lastWeekLeaderboard = db.collection("leaderboard").document(lastWeekLeaderboardName);
        users = db.collection("users");

        //Top 3 only needs to be built once since it doesn't change moment to moment
        buildTop3();
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Build leaderboard each time activity is started
        buildLeaderboard();
    }

    public void buildTop3() {
        lastWeekLeaderboard.get().addOnSuccessListener(documentSnapshot -> {
            //Save top 3 scorers from leaderboard into top3List
            //...

            //Adapt top3List ArrayList to ListView
            UsersAdapter top3Adapter = new UsersAdapter(LeaderboardActivity.this, top3List);
            top3ListView.setAdapter(top3Adapter);
        }).addOnFailureListener(e -> {
            Toast.makeText(LeaderboardActivity.this, "Failed to find Last Week's top 3", Toast.LENGTH_LONG).show();
            Log.d(TAG, "Error connecting to lastWeekLeaderboard document from database ", e);
        });
    }

    public void buildLeaderboard() {
        users.get().addOnSuccessListener(queryDocumentSnapshots -> {
            //Gather all users into leaderboardList
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                temp = new User(document.getString("email"), document.getString("firstName"), document.getString("lastName"), document.getLong("score").intValue());
                leaderboardList.add(temp);
            }

            //Sort leaderboardList by score in descending order
            Collections.sort(leaderboardList, (o1, o2) -> o2.getScore() - o1.getScore());

            //Adapt leaderboardList ArrayList to ListView
            UsersAdapter leaderboardAdapter = new UsersAdapter(LeaderboardActivity.this, leaderboardList);
            leaderboardListView.setAdapter(leaderboardAdapter);
        }).addOnFailureListener(e -> {
            Toast.makeText(LeaderboardActivity.this, "Failed to find leaderboard", Toast.LENGTH_LONG).show();
            Log.d(TAG, "Error connecting to users collection from database ", e);
        });
    }
}

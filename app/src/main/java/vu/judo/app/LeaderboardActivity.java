package vu.judo.app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
// import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

public class LeaderboardActivity extends AppCompatActivity {

    // static final String TAG = "Leaderboard";

    boolean weekly;

    String leaderboardHistoryDoc;

    User temp;
    ArrayList<User> leaderboardList, top3List;

    ListView top3ListView, leaderboardListView;

    FirebaseFirestore db;
    CollectionReference users, leaderboardHistoryUsers;

    @Override
    @SuppressWarnings("ConstantConditions")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getExtras().getString("leaderboard").equals("weekly")) {
            weekly = true;
            setContentView(R.layout.activity_weekly_leaderboard);
        } else {
            weekly = false;
            setContentView(R.layout.activity_all_time_leaderboard);
        }

        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {
            // Log.e(TAG, "getSupportActionBar().setDisplayHomeAsUpEnabled:failure", e);
        }

        leaderboardList = new ArrayList<>();
        leaderboardListView = findViewById(R.id.leaderboardList);

        db = FirebaseFirestore.getInstance();

        if (weekly) {
            top3List = new ArrayList<>();
            top3ListView = findViewById(R.id.top3List);

            // Find leaderboard from most recent sunday (https://stackoverflow.com/questions/12783102/how-to-get-the-last-sunday-before-current-date)
            Calendar lastSunday = Calendar.getInstance();

            // Get the Sunday before the most recent Sunday
            lastSunday.add(Calendar.DAY_OF_WEEK, (-(lastSunday.get(Calendar.DAY_OF_WEEK)-Calendar.SUNDAY))-7);

            // Set the name to the Sunday before the most recent Sunday
            leaderboardHistoryDoc = "Week of " + (lastSunday.get(Calendar.MONTH)+1) + "-" + lastSunday.get(Calendar.DATE) + "-" + lastSunday.get(Calendar.YEAR);

            leaderboardHistoryUsers = db.collection("leaderboard_history").document(leaderboardHistoryDoc).collection("users");

            // Top 3 only needs to be built once since it doesn't change moment to moment
            buildTop3();
        }

        users = db.collection("users");
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Build leaderboard each time activity is started
        buildLeaderboard(getIntent().getExtras().getString("leaderboard"));
    }

    public void buildTop3() {
        leaderboardHistoryUsers.get().addOnSuccessListener(queryDocumentSnapshots -> {
            // Gather all user history from leaderboardHistoryDoc into top3List
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                temp = new User(document.getString("email"), document.getString("firstName"), document.getString("lastName"), document.getDouble("score"));
                top3List.add(temp);
            }

            // Sort leaderboardList by score in descending order
            Collections.sort(top3List, (o1, o2) -> Double.compare(o2.getScore(), o1.getScore()));

            if (top3List.size() > 3) {
                top3List = new ArrayList<>(top3List.subList(0, 3));
            }

            // Adapt top3List ArrayList to ListView
            UsersAdapter top3Adapter = new UsersAdapter(LeaderboardActivity.this, top3List);
            top3ListView.setAdapter(top3Adapter);
        }).addOnFailureListener(e -> {
            Toast.makeText(LeaderboardActivity.this, "Failed to find Last Week's top 3", Toast.LENGTH_LONG).show();
            // Log.d(TAG, "Error connecting to lastWeekLeaderboard document from database ", e);
        });
    }

    public void buildLeaderboard(String type) {
        leaderboardList.clear();
        users.get().addOnSuccessListener(queryDocumentSnapshots -> {
            // Gather all users into leaderboardList
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                if (type.equals("weekly")) {
                    temp = new User(document.getString("email"), document.getString("firstName"), document.getString("lastName"), document.getDouble("score"));
                } else {
                    temp = new User(document.getString("email"), document.getString("firstName"), document.getString("lastName"), document.getDouble("allTimeScore"));
                }
                leaderboardList.add(temp);
            }

            // Sort leaderboardList by score in descending order
            Collections.sort(leaderboardList, (o1, o2) -> Double.compare(o2.getScore(), o1.getScore()));

            if (leaderboardList.size() > 10) {
                leaderboardList = new ArrayList<>(leaderboardList.subList(0, 10));
            }

            // Adapt leaderboardList ArrayList to ListView
            UsersAdapter leaderboardAdapter = new UsersAdapter(LeaderboardActivity.this, leaderboardList);
            leaderboardListView.setAdapter(leaderboardAdapter);
        }).addOnFailureListener(e -> {
            Toast.makeText(LeaderboardActivity.this, "Failed to find leaderboard", Toast.LENGTH_LONG).show();
            // Log.d(TAG, "Error connecting to users collection from database ", e);
        });
    }
}
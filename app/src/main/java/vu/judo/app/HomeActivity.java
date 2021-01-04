package vu.judo.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
// import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;

public class HomeActivity extends AppCompatActivity {

    // static final String TAG = "Home";

    double weeklyScore, allTimeScore;
    float weeklyUchikomiMultiplier;
    String email, firstName, weeklyRank, allTimeRank, weeklyUchikomi;
    ArrayList<Double> weeklyScores, allTimeScores;

    TextView userNameDisplay, userWeeklyScoreDisplay, userAllTimeScoreDisplay;

    FirebaseFirestore db;
    FirebaseUser userAuth;

    DocumentReference userDoc;
    CollectionReference users, uchikomi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        weeklyScores =  new ArrayList<>(); allTimeScores = new ArrayList<>();
        userNameDisplay = findViewById(R.id.homeUserName);
        userWeeklyScoreDisplay = findViewById(R.id.homeUserWeeklyScore);
        userAllTimeScoreDisplay = findViewById(R.id.homeUserAllTimeScore);

        userAuth = FirebaseAuth.getInstance().getCurrentUser();
        if (userAuth != null) {
            email = userAuth.getEmail();
        }

        db = FirebaseFirestore.getInstance();
        users = db.collection("users");
        users.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    weeklyScores.add(document.getDouble("score").doubleValue());
                    allTimeScores.add(document.getDouble("allTimeScore").doubleValue());
                }
                Collections.sort(weeklyScores, Collections.reverseOrder());
                Collections.sort(allTimeScores, Collections.reverseOrder());

            } else {
                // Log.d(TAG, "Error getting documents: ", task.getException());
            }
            getUserInfo();
        });

        uchikomi = db.collection("waza");
        uchikomi.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    if (document.getBoolean("weeklyAssignment")) {
                        weeklyUchikomi = document.getString("name");
                        weeklyUchikomiMultiplier = document.getDouble("multiplier").floatValue();
                        break;
                    }
                }
            } else {
                // Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
    }

    public void weeklyAssignment(View view) {
        Bundle thisAssignment = new Bundle();
        thisAssignment.putString("goto", "HomeActivity");
        thisAssignment.putString("exercise", weeklyUchikomi);
        thisAssignment.putFloat("multiplier", weeklyUchikomiMultiplier);
        startActivity(new Intent(this, LogActivity.class).putExtras(thisAssignment));
    }

    public void library(View view) {
        startActivity(new Intent(this, LibraryActivity.class));
    }

    public void weeklyLeaderboard(View view) {
        Bundle leaderboard = new Bundle();
        leaderboard.putString("leaderboard", "weekly");
        startActivity(new Intent(this, LeaderboardActivity.class).putExtras(leaderboard));
    }

    public void allTimeLeaderboard(View view) {
        Bundle leaderboard = new Bundle();
        leaderboard.putString("leaderboard", "allTime");
        startActivity(new Intent(this, LeaderboardActivity.class).putExtras(leaderboard));
    }

    private void getUserInfo() {
        // Get user info from DB
        userDoc = db.collection("users").document(email);
        userDoc.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    firstName = document.getString("firstName");
                    userNameDisplay.setText(firstName);

                    weeklyScore = document.getDouble("score").doubleValue();
                    allTimeScore = document.getDouble("allTimeScore").doubleValue();

                    // Calculate ranks based on score
                    weeklyRank = setRank(weeklyScores, weeklyScore);
                    allTimeRank = setRank(allTimeScores, allTimeScore);

                    String weeklyScoreString = weeklyScore + " (" + weeklyRank + ")";
                    String allTimeScoreString = allTimeScore + " (" + allTimeRank + ")";

                    userWeeklyScoreDisplay.setText(weeklyScoreString);
                    userAllTimeScoreDisplay.setText(allTimeScoreString);

                    // Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                } else {
                    // Log.d(TAG, "No such document");
                }
            } else {
                Toast.makeText(HomeActivity.this, "Failed to find user information. Please restart the application", Toast.LENGTH_LONG).show();
                // Log.d(TAG, "get failed with ", task.getException());
            }
        });
    }

    private String setRank(ArrayList<Double> scores, double score) {
        int rank = 0;
        String rankText;
        if (!scores.isEmpty()) {
            for (int i=0; i<scores.size(); i++) {
                if (score == scores.get(i)) {
                    rank = i+1;
                    break;
                }
            }

            switch (rank % 10) {
                case 1:
                    rankText = rank + "st";
                    break;
                case 2:
                    rankText = rank + "nd";
                    break;
                case 3:
                    rankText = rank + "rd";
                    break;
                default:
                    rankText = rank + "th";
                    break;
            }
        } else {
            rankText = "1st";
        }

        return rankText;
    }
}

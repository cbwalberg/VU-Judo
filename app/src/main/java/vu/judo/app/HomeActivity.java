package vu.judo.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class HomeActivity extends AppCompatActivity {

    static final String TAG = "Home";

    int score, rank;
    String email, firstName, rankText, dailyWaza;
    ArrayList<Integer> scores;

    TextView userNameDisplay, userScoreDisplay, userRankDisplay;

    FirebaseFirestore db;
    FirebaseUser userAuth;

    DocumentReference userDoc;
    CollectionReference users, waza;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        scores =  new ArrayList<>();
        userNameDisplay = findViewById(R.id.homeUserName);
        userScoreDisplay = findViewById(R.id.homeUserScore);
        userRankDisplay = findViewById(R.id.homeUserRank);

        userAuth = FirebaseAuth.getInstance().getCurrentUser();
        if (userAuth != null) {
            email = userAuth.getEmail();
        }

        db = FirebaseFirestore.getInstance();
        users = db.collection("users");
        users.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            @SuppressWarnings("ConstantConditions")
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        scores.add(document.getLong("score").intValue());
                    }
                    Collections.sort(scores, Collections.reverseOrder());
                    getUserInfo();
                } else {
                    getUserInfo();
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

        waza = db.collection("waza");
        waza.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            @SuppressWarnings("ConstantConditions")
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.getBoolean("dailyAssignment")) {
                            dailyWaza = document.getString("name");
                            break;
                        }
                    }
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public void dailyAssignment(View view) {
        Bundle thisAssignment = new Bundle();
        thisAssignment.putString("goto", "HomeActivity");
        thisAssignment.putString("exercise", dailyWaza);
        thisAssignment.putString("type", "waza");
        startActivity(new Intent(this, LogActivity.class).putExtras(thisAssignment));
    }

    public void leaderboard(View view) {
        startActivity(new Intent(this, LeaderboardActivity.class));
    }

    public void library(View view) {
        startActivity(new Intent(this, LibraryActivity.class));
    }

    private void getUserInfo() {
        //Get user info from DB
        userDoc = db.collection("users").document(email);
        userDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            @SuppressWarnings("ConstantConditions")
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        firstName = document.getString("firstName");
                        score =  document.getLong("score").intValue();

                        //Calculate rank based on score
                        if (!scores.isEmpty()) {
                            for (int i=0; i<scores.size(); i++) {
                                if (score == scores.get(i)) {
                                    rank = i+1;
                                    break;
                                }
                            }

                            int lastRankDigit = rank % 10;
                            switch (lastRankDigit) {
                                case 1:
                                    rankText = "" + rank + "st";
                                    break;
                                case 2:
                                    rankText = "" + rank + "nd";
                                    break;
                                case 3:
                                    rankText = "" + rank + "rd";
                                    break;
                                default:
                                    rankText = "" + rank + "th";
                                    break;
                            }
                        } else {
                            rankText = "ERROR";
                        }

                        userNameDisplay.setText(firstName);
                        userScoreDisplay.setText(String.format(Locale.getDefault(), "%d", score));
                        userRankDisplay.setText(rankText);

                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "Failed to find user information. Please restart the application", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
}

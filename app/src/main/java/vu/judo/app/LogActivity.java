package vu.judo.app;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
// import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class LogActivity extends AppCompatActivity {

    static final String TAG = "Log";

    boolean waza;
    int reps, wazaMultiplier, exerciseMultiplier, thisMultiplier, selectedDay, selectedMonth, selectedYear;
    String exercise, selectedDate, userEmail;

    TextView exerciseView, dateView;
    EditText repsInput;

    Calendar calendar;

    FirebaseFirestore db;
    DocumentReference userDoc, dateDoc, scoreMultipliers;

    @Override @SuppressWarnings("ConstantConditions")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {
            // Log.e(TAG, "getSupportActionBar().setDisplayHomeAsUpEnabled:failure", e);
        }

        db = FirebaseFirestore.getInstance();

        exerciseView = findViewById(R.id.logExercise);
        repsInput = findViewById(R.id.logReps);
        dateView = findViewById(R.id.logDate);

        //Get the exercise passed to this activity from calling activity
        exercise = getIntent().getExtras().getString("exercise");
        exerciseView.setText(exercise);

        //Get the type of exercise passed to this activity from calling activity. Boolean "waza" true for waza, false for workout exercises
        waza = getIntent().getExtras().getString("type").equals("waza");

        //Set initial date to Today
        calendar = Calendar.getInstance();
        selectedDay = calendar.get(Calendar.DAY_OF_MONTH);
        selectedMonth = calendar.get(Calendar.MONTH);
        selectedYear = calendar.get(Calendar.YEAR);

        //Get wazaMultiplier and exerciseMultiplier values from config file in db
        scoreMultipliers = db.collection("config").document("Score Multipliers");
        scoreMultipliers.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot multipliersDoc = task.getResult();
                wazaMultiplier = multipliersDoc.getLong("Waza Multiplier").intValue();
                exerciseMultiplier = multipliersDoc.getLong("Exercise Multiplier").intValue();
            } else {
                wazaMultiplier = 50; exerciseMultiplier = 10;
                // Log.d(TAG, "Failed to find score multiplier data in DB ", task.getException());
            }
            //Set this specific multiplier based on whether passed exercise is waza or (workout) exercise
            thisMultiplier = waza ? wazaMultiplier : exerciseMultiplier;
        });

        //When the dateView text is clicked, show a dialog allowing users to select a date
        selectedDate = convertMonth(selectedMonth) + " " + selectedDay + " " + selectedYear;
        dateView.setText(selectedDate);
        dateView.setOnClickListener(view -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    LogActivity.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    (datePicker, year, month, day) -> {
                        selectedDay = day;
                        selectedMonth = month;
                        selectedYear = year;
                        String monthText = convertMonth(month);
                        String date = monthText + " " + day + " " + year;
                        dateView.setText(date);
                        selectedDate = date;
                    },
                    selectedYear, selectedMonth, selectedDay);
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            datePickerDialog.show();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Clear any existing text
        repsInput.getText().clear();
        selectedDate = "";
    }

    public void save(View view) {
        reps = repsInput.getText().toString().equals("") ? 0 : Integer.parseInt(repsInput.getText().toString());

        //Make sure reps and date have been entered/selected
        if (!selectedDate.equals("") && reps > 0) {
            // Find current user, add to their score.
            // Then check if Practice Log collection already exists in current user doc, if so add to it, if not, create collection within user document named Practice Log
            // Check for document within Practice Log with name "selectedDate", if it exists edit it, if not create it
            // Check if field "exercise" exists in selectedDate document, if so add to it, if not create it (Fields in selectedDate document are names of waza/exercises, values of the fields are the number of reps)

            userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            userDoc = db.collection("users").document(userEmail);

            // This will save the document reference if it exists, and create and save it if either the document OR the collection don't exist
            dateDoc = userDoc.collection("Practice Log").document(selectedDate);

            userDoc.get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult().exists()) {
                    //Half Joe's score per his request
                    if (userEmail.equals("joemore117@gmail.com")) {
                        task.getResult().getReference().update("score", task.getResult().getLong("score").intValue() + ((reps * thisMultiplier)/2));
                    } else {
                        task.getResult().getReference().update("score", task.getResult().getLong("score").intValue() + (reps * thisMultiplier));
                    }

                    dateDoc.get().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task1.getResult();
                            if (documentSnapshot.contains(exercise)) {
                                //selectedDate doc contains exercise, update
                                dateDoc.update(exercise, documentSnapshot.getLong(exercise).intValue() + reps);
                            } else {
                                //selectedDate doc does not contain exercise, set value
                                Map<String, Object> exerciseInfo = new HashMap<>();
                                exerciseInfo.put(exercise, reps);
                                dateDoc.set(exerciseInfo, SetOptions.merge());
                            }
                            Toast.makeText(LogActivity.this, "" + reps + " reps of " + exercise + " saved for " + selectedDate, Toast.LENGTH_LONG).show();
                            startActivity(new Intent(LogActivity.this, HomeActivity.class));
                        } else {
                            Toast.makeText(LogActivity.this, "Could not find reference to document", Toast.LENGTH_LONG).show();
                            // Log.d(TAG, "Failed to find document reference ", task1.getException());
                        }
                    });
                } else {
                    Toast.makeText(LogActivity.this, "Failed to find user information. Please restart the application", Toast.LENGTH_LONG).show();
                    // Log.d(TAG, "Could not find active user info in DB");
                }
            });
        } else {
            Toast.makeText(LogActivity.this, "Please enter number of reps and select a date to save", Toast.LENGTH_LONG).show();
        }
    }

    public String convertMonth(int month) {
        String monthText = "";
        switch (month) {
            case 0:
                monthText = "Jan";
                break;
            case 1:
                monthText = "Feb";
                break;
            case 2:
                monthText = "Mar";
                break;
            case 3:
                monthText = "Apr";
                break;
            case 4:
                monthText = "May";
                break;
            case 5:
                monthText = "Jun";
                break;
            case 6:
                monthText = "Jul";
                break;
            case 7:
                monthText = "Aug";
                break;
            case 8:
                monthText = "Sep";
                break;
            case 9:
                monthText = "Oct";
                break;
            case 10:
                monthText = "Nov";
                break;
            case 11:
                monthText = "Dec";
                break;
        }
        return monthText;
    }

    @Override
    public Intent getSupportParentActivityIntent() {
        return getParentActivityIntentImplement();
    }

    @Override
    public Intent getParentActivityIntent() {
        return getParentActivityIntentImplement();
    }

    @SuppressWarnings("ConstantConditions")
    private Intent getParentActivityIntentImplement() {
        Intent intent;

        if (getIntent().getExtras().getString("goto").equals("HomeActivity")) {
            intent = new Intent(this, HomeActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        } else {
            intent = new Intent(this, LibraryActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        }

        return intent;
    }
}

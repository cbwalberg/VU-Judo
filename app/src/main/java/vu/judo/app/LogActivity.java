package vu.judo.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class LogActivity extends AppCompatActivity {

    static final String TAG = "Log";

    boolean waza, isDateSelected;
    int reps, wazaMultiplier, exerciseMultiplier, thisMultiplier, selectedDay, selectedMonth, selectedYear;
    String exercise, selectedDate, userEmail;

    TextView exerciseView, dateView;
    EditText repsInput;

    Calendar calendar;

    FirebaseFirestore db;
    DocumentReference userDoc, dateDoc, scoreMultipliers;
    CollectionReference practiceLog;

    @Override @SuppressWarnings("ConstantConditions")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {
            Log.e(TAG, "getSupportActionBar().setDisplayHomeAsUpEnabled:failure", e);
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
        scoreMultipliers.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override @SuppressWarnings("ConstantConditions")
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot multipliersDoc = task.getResult();
                    wazaMultiplier = multipliersDoc.getLong("Waza Multiplier").intValue();
                    exerciseMultiplier = multipliersDoc.getLong("Exercise Multiplier").intValue();
                } else {
                    wazaMultiplier = 50; exerciseMultiplier = 10;
                    Log.d(TAG, "Failed to find score multiplier data in DB ", task.getException());
                }
                //Set this specific multiplier based on whether passed exercise is waza or (workout) exercise
                thisMultiplier = waza ? wazaMultiplier : exerciseMultiplier;
            }
        });

        //When the dateView text is clicked, show a dialog allowing users to select a date
        dateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        LogActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                selectedDay = day;
                                selectedMonth = month;
                                selectedYear = year;
                                String monthText = "";
                                switch (month) {
                                    case 0:
                                        monthText = "JAN";
                                        break;
                                    case 1:
                                        monthText = "FEB";
                                        break;
                                    case 2:
                                        monthText = "MAR";
                                        break;
                                    case 3:
                                        monthText = "APR";
                                        break;
                                    case 4:
                                        monthText = "MAY";
                                        break;
                                    case 5:
                                        monthText = "JUN";
                                        break;
                                    case 6:
                                        monthText = "JUL";
                                        break;
                                    case 7:
                                        monthText = "AUG";
                                        break;
                                    case 8:
                                        monthText = "SEP";
                                        break;
                                    case 9:
                                        monthText = "OCT";
                                        break;
                                    case 10:
                                        monthText = "NOV";
                                        break;
                                    case 11:
                                        monthText = "DEC";
                                        break;
                                }
                                String date = monthText + " " + day + " " + year;
                                dateView.setText(date);
                                selectedDate = date;
                            }
                        },
                        selectedYear, selectedMonth, selectedDay);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
                isDateSelected = true;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        isDateSelected = false;

        //Clear any existing text
        repsInput.getText().clear();
    }

    public void save(View view) {
        reps = Integer.parseInt(repsInput.getText().toString());

        //Make sure reps and date have been entered/selected
        if (isDateSelected && reps != 0) {
            // Find current user, add to their score.
            // Then check if Practice Log collection already exists in current user doc, if so add to it, if not, create collection within user document named Practice Log
            // Check for document within Practice Log with name "selectedDate", if it exists edit it, if not create it
            // Check if field "exercise" exists in selectedDate document, if so add to it, if not create it (Fields in selectedDate document are names of waza/exercises, values of the fields are the number of reps)

            userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
            userDoc = db.collection("users").document(userEmail);
            // This will save the document reference if it exists, and create and save it if either the document OR the colleciton don't exist
            dateDoc = userDoc.collection("Practice Log").document(selectedDate);

            userDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override @SuppressWarnings("ConstantConditions")
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        task.getResult().getReference().update("score", task.getResult().getLong("score").intValue() + (reps * thisMultiplier));

                        dateDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override @SuppressWarnings("ConstantConditions")
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot documentSnapshot = task.getResult();
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
                                    Log.d(TAG, "Failed to find document reference ", task.getException());
                                }
                            }
                        });
                    } else {
                        Toast.makeText(LogActivity.this, "Failed to find user information. Please restart the application", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "Could not find active user info in DB");
                    }
                }
            });
        } else {
            Toast.makeText(LogActivity.this, "Please enter number of reps and select a date to save", Toast.LENGTH_LONG).show();
        }
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

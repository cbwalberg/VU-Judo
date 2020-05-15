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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Calendar;
import java.util.Map;

public class LogActivity extends AppCompatActivity {

    static final String TAG = "Log";

    int reps, wazaModifier = 50, exerciseModifier = 10, selectedDay, selectedMonth, selectedYear;
    String exercise, selectedDate, userEmail;

    TextView exerciseView, dateView;
    EditText repsInput;

    Calendar calendar;

    FirebaseFirestore db;
    DocumentReference userDoc, exerciseDoc;
    CollectionReference dateCollection;


    @Override @SuppressWarnings("ConstantConditions")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {
            Log.e(TAG, "getSupportActionBar().setDisplayHomeAsUpEnabled:failure", e);
        }
        exerciseView = findViewById(R.id.logExercise);
        repsInput = findViewById(R.id.logReps);
        dateView = findViewById(R.id.logDate);

        //Get the exercise passed to this activity from calling activity
        exercise = getIntent().getExtras().getString("exercise");
        exerciseView.setText(exercise);

        //Set initial date to Today
        calendar = Calendar.getInstance();
        selectedDay = calendar.get(Calendar.DAY_OF_MONTH);
        selectedMonth = calendar.get(Calendar.MONTH);
        selectedYear = calendar.get(Calendar.YEAR);

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
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Clear any existing text
        repsInput.getText().clear();
    }

    public void save(View view) {
        // Find current user, check if Practice Log collection already exists, if so add to it, if not, create collection within user document named Practice Log
        // Add new document to Practice Log with name "selectedDate". Fields in selectedDate document are names of waza/exercises, values of the fields are the number of reps
        db = FirebaseFirestore.getInstance();

        userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        reps = Integer.parseInt(repsInput.getText().toString());

        userDoc = db.collection("users").document(userEmail);
        userDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override @SuppressWarnings("ConstantConditions")
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot userDocument = task.getResult();
                    if (userDocument.exists()) {
                        dateCollection = db.collection("Practice Log");
                        dateCollection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    if (task.getResult().size() > 0) { //Collection of name "Practice Log" exists, check for field "exercise" in document "selectedDate", if it exists, read its value
                                        for (QueryDocumentSnapshot dateDocument : task.getResult()) {
                                            if (dateDocument.getString("date").equals(selectedDate)) {
                                                //...
                                            }
                                        }
                                    } else { //No collection of name "Practice Log" exists, create a new one
                                        //...
                                    }
                                } else {
                                    Toast.makeText(LogActivity.this, "Failed to query database. Please restart the application", Toast.LENGTH_LONG).show();
                                    Log.d(TAG, "get failed with ", task.getException());
                                }
                            }
                        });
                    } else {
                        Toast.makeText(LogActivity.this, "Failed to find user information. Please restart the application", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Toast.makeText(LogActivity.this, "Failed to find user information. Please restart the application", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
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

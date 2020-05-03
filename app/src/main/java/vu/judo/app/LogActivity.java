package vu.judo.app;

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

import java.util.Calendar;

public class LogActivity extends AppCompatActivity {

    static final String TAG = "Log";

    int selectedDay, selectedMonth, selectedYear;
    String exercise;

    TextView exerciseView, dateView;
    EditText reps;

    Calendar calendar;

    @Override
    @SuppressWarnings("ConstantConditions")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {
            Log.e(TAG, "getSupportActionBar().setDisplayHomeAsUpEnabled:failure", e);
        }
        exerciseView = findViewById(R.id.logExercise);
        reps = findViewById(R.id.logReps);
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
        reps.getText().clear();
    }

    public void save(View view) {
        //...
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

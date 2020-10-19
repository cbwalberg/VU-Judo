package vu.judo.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
// import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class LibraryActivity extends AppCompatActivity {

    static final String TAG = "Library";

    int imageResource, previousViewId, previousButtonId;
    String uri;

    Drawable res;

    ConstraintLayout layout;
    ConstraintSet constraints;
    TextView tempView;
    ImageButton tempButton;

    FirebaseFirestore db;
    CollectionReference uchikomi, exercises;

    @Override
    @SuppressWarnings("ConstantConditions")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        layout = findViewById(R.id.libraryConstraintLayout);
        constraints = new ConstraintSet();

        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {
            // Log.e(TAG, "getSupportActionBar().setDisplayHomeAsUpEnabled:failure", e);
        }

        //Gets the image used for the plus button for creating the dynamic view
        uri = "@android:drawable/ic_input_add";
        imageResource = getResources().getIdentifier(uri, null, getPackageName());
        res = getResources().getDrawable(imageResource);

        db = FirebaseFirestore.getInstance();
        uchikomi = db.collection("waza");
        uchikomi.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                previousViewId = R.id.uchikomiTitle;
                for (QueryDocumentSnapshot document : task.getResult()) {
                    buildView(document.getString("name"), "waza");
                }

                constraints.connect(R.id.exercisesTitle, ConstraintSet.TOP, previousViewId, ConstraintSet.BOTTOM);
                constraints.applyTo(layout);

                exercises = db.collection("exercises");
                exercises.get().addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        previousViewId = R.id.exercisesTitle;
                        for (QueryDocumentSnapshot document : task1.getResult()) {
                            buildView(document.getString("name"), "exercise");
                        }
                    } else {
                        Toast.makeText(LibraryActivity.this, "Failed to find exercise list", Toast.LENGTH_LONG).show();
                        // Log.d(TAG, "Error getting documents: ", task1.getException());
                    }
                });
            } else {
                Toast.makeText(LibraryActivity.this, "Failed to find waza list", Toast.LENGTH_LONG).show();
                // Log.d(TAG, "Error getting documents: ", task.getException());
            }
        });
    }

    public void buildView(final String name, final String type) {
        tempView = new TextView(this);
        tempView.setId(View.generateViewId());
        tempView.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT));
        tempView.setText(name);
        tempView.setTextSize(22);
        layout.addView(tempView);

        tempButton = new ImageButton(this);
        tempButton.setId(View.generateViewId());
        tempButton.setLayoutParams(new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT));
        tempButton.setImageDrawable(res);
        tempButton.setBackgroundColor(Color.TRANSPARENT);
        tempButton.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("exercise", name);
            bundle.putString("type", type);
            bundle.putString("goto", "LibraryActivity");
            startActivity(new Intent(LibraryActivity.this, LogActivity.class).putExtras(bundle));
        });
        layout.addView(tempButton);

        constraints.clone(layout);
        constraints.connect(tempView.getId(), ConstraintSet.TOP, previousViewId, ConstraintSet.BOTTOM, dpToPx(30, this));
        constraints.connect(tempView.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        constraints.connect(tempView.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
        constraints.setVerticalBias(tempView.getId(), 0.0f);

        constraints.connect(tempButton.getId(), ConstraintSet.TOP, tempView.getId(), ConstraintSet.TOP);
        constraints.connect(tempButton.getId(), ConstraintSet.BOTTOM, tempView.getId(), ConstraintSet.BOTTOM);
        constraints.connect(tempButton.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
        constraints.setVerticalBias(tempButton.getId(), 0.5f);

        //First waza & exercise are constrained slightly differently than the rest
        if (previousViewId == R.id.uchikomiTitle || previousViewId == R.id.exercisesTitle) {
            constraints.connect(tempView.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
            constraints.setHorizontalBias(tempView.getId(), 0.15f);
            if (previousViewId == R.id.uchikomiTitle) {
                constraints.connect(tempButton.getId(), ConstraintSet.START, previousViewId, ConstraintSet.END);
                constraints.setHorizontalBias(tempButton.getId(), 0.5f);
            } else {
                constraints.connect(tempButton.getId(), ConstraintSet.START, previousButtonId, ConstraintSet.START);
                constraints.setHorizontalBias(tempButton.getId(), 0.0f);
            }
        } else {
            constraints.connect(tempView.getId(), ConstraintSet.START, previousViewId, ConstraintSet.START);
            constraints.setHorizontalBias(tempView.getId(), 0.0f);

            constraints.connect(tempButton.getId(), ConstraintSet.START, previousButtonId, ConstraintSet.START);
            constraints.setHorizontalBias(tempButton.getId(), 0.0f);
        }

        constraints.applyTo(layout);
        previousViewId = tempView.getId();
        previousButtonId = tempButton.getId();
    }

    public int dpToPx(int dp, Context context) {
        float density = context.getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }
}

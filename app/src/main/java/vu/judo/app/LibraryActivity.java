package vu.judo.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class LibraryActivity extends AppCompatActivity {

    static final String TAG = "Library";

    int imageResource, tempViewID, finalNameId, previousId;
    String uri;
    ArrayList<String> names;

    Drawable res;

    ConstraintLayout layout;
    ConstraintSet constraints;
    TextView tempView;
    ImageButton tempButton;

    FirebaseFirestore db;
    CollectionReference waza, exercises;

    @Override
    @SuppressWarnings("ConstantConditions")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        names =  new ArrayList<>();
        layout = findViewById(R.id.libraryConstraintLayout);
        constraints = new ConstraintSet();
        constraints.clone(layout);

        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {
            Log.e(TAG, "getSupportActionBar().setDisplayHomeAsUpEnabled:failure", e);
        }

        //Gets the image used for the plus button for creating the dynamic view
        uri = "@android:drawable/ic_input_add";
        imageResource = getResources().getIdentifier(uri, null, getPackageName());
        res = getResources().getDrawable(imageResource);

        db = FirebaseFirestore.getInstance();
        waza = db.collection("waza");
        waza.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            @SuppressWarnings("ConstantConditions")
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    previousId = R.id.exampleWaza;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        buildView(document.getString("name"));
                    }
                    //buildView("Test2");
                    //buildView("Test3");

                    constraints.clone(layout);
                    constraints.connect(R.id.exercisesTitle, ConstraintSet.TOP, previousId, ConstraintSet.BOTTOM);
                    constraints.applyTo(layout);

                    /*
                    exercises = db.collection("exercises");
                    exercises.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        @SuppressWarnings("ConstantConditions")
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                previousId = R.id.exercisesTitle;
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    buildView(document.getString("name"));
                                }
                            } else {
                                Toast.makeText(LibraryActivity.this, "Failed to find exercise list", Toast.LENGTH_LONG).show();
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
                    */
                } else {
                    Toast.makeText(LibraryActivity.this, "Failed to find waza list", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public void buildView(String name) {
            Log.d(TAG, "Name: " + name);

            tempView = new TextView(this);
            tempView.setId(View.generateViewId());
            tempView.setLayoutParams(layout.getLayoutParams());

            tempView.setText(name);
            tempView.setTextSize(24);

            layout.addView(tempView);

            constraints.clone(layout);
            constraints.connect(tempView.getId(), ConstraintSet.TOP, previousId, ConstraintSet.BOTTOM, dpToPx(30, this));
            constraints.connect(tempView.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT);
            constraints.connect(tempView.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT);
            constraints.connect(tempView.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);

            constraints.setHorizontalBias(tempView.getId(), (float)0.15);
            constraints.setVerticalBias(tempView.getId(), (float)0.0);

            constraints.applyTo(layout);

            /*

            tempButton = new ImageButton(this);
            tempButton.setImageDrawable(res);
            tempButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("exercise", names.get(i));
                    //startActivity(new Intent(this, LogActivity.class).putExtras(bundle));
                }
            });

            */

            previousId = tempView.getId();
    }

    public int dpToPx(int dp, Context context) {

        float density = context.getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }
}
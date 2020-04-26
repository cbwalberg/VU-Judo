package vu.judo.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

    String name;
    ArrayList<String> names;
    ArrayList<Integer> nameIDs;

    ConstraintLayout layout;
    ConstraintSet constraints = new ConstraintSet();
    TextView tempView;

    FirebaseFirestore db;
    CollectionReference waza, exercises;

    @Override
    @SuppressWarnings("ConstantConditions")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        layout = findViewById(R.id.libraryConstraintLayout);
        constraints.clone(layout);

        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {
            Log.e(TAG, "getSupportActionBar().setDisplayHomeAsUpEnabled:failure", e);
        }

        db = FirebaseFirestore.getInstance();
        waza = db.collection("waza");
        waza.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            @SuppressWarnings("ConstantConditions")
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        name = document.getString("name");
                        buildView(name);
                    }

                    //Replace R.id.exampleWaza with final waza textViewId
                    //...
                    constraints.connect(R.id.exercisesTitle, ConstraintSet.TOP, R.id.exampleWaza, ConstraintSet.BOTTOM);
                    constraints.applyTo(layout);

                    exercises = db.collection("exercises");
                    exercises.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        @SuppressWarnings("ConstantConditions")
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    name = document.getString("name");
                                    buildView(name);
                                }
                            } else {
                                Toast.makeText(LibraryActivity.this, "Failed to find exercise list", Toast.LENGTH_LONG).show();
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        }
                    });
                } else {
                    Toast.makeText(LibraryActivity.this, "Failed to find waza list", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    public void logActivity(View view) {
        Bundle bundle = new Bundle();
        bundle.putString("goto", "LibraryActivity");

        //IMPLEMENT
        /*
        for (int i=0; i<nameIDs.size(); i++) {
            if (view.getId() == nameIDs.get(i)) {
                bundle.putString("exercise", names.get(i));
            }
        }

         */

        startActivity(new Intent(this, LogActivity.class).putExtras(bundle));
    }

    //IMPLEMENT
    public void buildView(String name) {
        tempView = new TextView(this);

        names.add(name);
        nameIDs.add(tempView.getId());
    }
}

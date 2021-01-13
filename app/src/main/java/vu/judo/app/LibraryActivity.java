package vu.judo.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
// import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;

public class LibraryActivity extends AppCompatActivity {

    // static final String TAG = "Library";

    NonScrollListView uchikomiListView, exerciseListView;

    UchikomiOrExercise temp;
    ArrayList<UchikomiOrExercise> uchikomiLibrary, exerciseLibrary;

    FirebaseFirestore db;
    CollectionReference uchikomi, exercises;

    @Override
    @SuppressWarnings("ConstantConditions")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {
            // Log.e(TAG, "getSupportActionBar().setDisplayHomeAsUpEnabled:failure", e);
        }

        uchikomiLibrary = new ArrayList<>();
        exerciseLibrary = new ArrayList<>();

        uchikomiListView = findViewById(R.id.uchikomiList);
        exerciseListView = findViewById(R.id.exerciseList);

        db = FirebaseFirestore.getInstance();
        uchikomi = db.collection("waza");
        exercises = db.collection("exercises");

        buildLibrary(uchikomi, uchikomiLibrary, uchikomiListView);
        buildLibrary(exercises, exerciseLibrary, exerciseListView);
    }

    public void buildLibrary(CollectionReference library, ArrayList<UchikomiOrExercise> list, ListView listView) {
        library.get().addOnSuccessListener(queryDocumentSnapshots -> {
            // Gather all uchikomi into uchikomiLibrary
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                temp = new UchikomiOrExercise(document.getString("name"), document.getDouble("multiplier"));
                list.add(temp);
            }

            // Sort uchikomiLibrary in alphabetical order
            Collections.sort(list, (o1, o2) -> o1.getName().compareTo(o2.getName()));

            // Adapt uchikomiLibrary ArrayList to ListView
            UchikomiOrExercisesAdapter adapter = new UchikomiOrExercisesAdapter(LibraryActivity.this, list);
            listView.setAdapter(adapter);
        }).addOnFailureListener(e -> {
            Toast.makeText(LibraryActivity.this, "Failed to find" + library + " collection", Toast.LENGTH_LONG).show();
            // Log.d(TAG, "Error connecting to " + library + " collection from database ", e);
        });
    }

    public void toLogActivity(View v) {
        TextView element = (TextView) v;
        Bundle bundle = new Bundle();
        bundle.putString("exercise", element.getText().toString());
        bundle.putDouble("multiplier", (double) element.getTag());
        bundle.putString("goto", "LibraryActivity");
        startActivity(new Intent(LibraryActivity.this, LogActivity.class).putExtras(bundle));
    }
}

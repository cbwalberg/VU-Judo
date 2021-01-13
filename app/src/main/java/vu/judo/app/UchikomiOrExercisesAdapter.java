package vu.judo.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

// https://github.com/codepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView
public class UchikomiOrExercisesAdapter extends ArrayAdapter<UchikomiOrExercise> {

    // View lookup cache
    private static class ViewHolder {
        TextView nameView;
    }

    public UchikomiOrExercisesAdapter(Context context, ArrayList<UchikomiOrExercise> uchikomiOrExercises) {
        super(context, R.layout.uchikomi_or_exercise_item, uchikomiOrExercises);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        UchikomiOrExercise uchikomiOrExercise = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();

            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.uchikomi_or_exercise_item, parent, false);
            viewHolder.nameView = convertView.findViewById(R.id.uchikomiOrExerciseItemName);

            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Populate the data from the data object via the viewHolder object into the template view.
        viewHolder.nameView.setText(uchikomiOrExercise.getName());
        viewHolder.nameView.setTag(uchikomiOrExercise.getMultiplier());

        // Return the completed view to render on screen
        return convertView;
    }
}

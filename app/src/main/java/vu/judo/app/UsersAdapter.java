package vu.judo.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

// https://github.com/codepath/android_guides/wiki/Using-an-ArrayAdapter-with-ListView
public class UsersAdapter extends ArrayAdapter<User> {

    int score = 0, rank = 0;

    // View lookup cache
    private static class ViewHolder {
        TextView userRankView;
        TextView userNameView;
        TextView userScoreView;
    }

    public UsersAdapter(Context context, ArrayList<User> users) {
        super(context, R.layout.user_item, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        User user = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();

            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.user_item, parent, false);
            viewHolder.userRankView = convertView.findViewById(R.id.userItemRank);
            viewHolder.userNameView = convertView.findViewById(R.id.userItemName);
            viewHolder.userScoreView = convertView.findViewById(R.id.userItemScore);

            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // If this is the first entry, rank is 1, if this user's score does not equal the previous user's score, than rank is position + 1, else rank remains unchanged as it is equal to the previous user's score
        if (position == 0) {
            rank = 1;
        } else if (user.getScore() != score) {
            rank = position + 1;
        }

        String rankText;
        switch(rank % 10) {
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

        score = user.getScore();

        // Populate the data from the data object via the viewHolder object into the template view.
        String userName = user.getFname() + " " + user.getLname();
        viewHolder.userRankView.setText(rankText);
        viewHolder.userNameView.setText(userName);
        viewHolder.userScoreView.setText(String.valueOf(user.getScore()));

        // Return the completed view to render on screen
        return convertView;
    }
}

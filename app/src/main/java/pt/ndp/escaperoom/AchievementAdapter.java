package pt.ndp.escaperoom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

/**
 * Translates the achievements list of a PlayerActivity into a series of Views that a ListView is
 * able to display.
 */
public class AchievementAdapter extends BaseAdapter implements ListAdapter {

    PlayerActivity parent;
    private static LayoutInflater inflater;

    public AchievementAdapter(PlayerActivity parent) {
        this.parent = parent;
        inflater = (LayoutInflater) parent.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (parent.room.achievements != null) {
            return parent.room.achievements.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int i) {
        if (parent.room.achievements != null) {
            return parent.room.achievements.get(i);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;
        if (v == null) {
            v = inflater.inflate(R.layout.row_achievement, viewGroup, false);
            //TODO: Handle InflateException
        }

        ImageView icon = (ImageView)v.findViewById(R.id.achievement_icon);
        TextView name = (TextView)v.findViewById(R.id.achievement_name);
        TextView description = (TextView)v.findViewById(R.id.achievement_description);
        TextView text = (TextView)v.findViewById(R.id.achievement_text);

        Achievement a = (Achievement) getItem(i);

        icon.setImageDrawable(a.icon);
        name.setText(a.name);
        if (a.isAchieved()) {
            description.setText(a.description);
            text.setText(a.text);
        } else {
            // TODO: Customizable descriptions for failed achievements
            description.setText("Not achieved. Better luck next time!");
            text.setText("");
        }

        return v;
    }
}

package pt.ndp.escaperoom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;

import java.util.ArrayList;

/**
 * This class stores the data for a ListView containing player details.
 *
 * Player names are stored at even indices in the ArrayList, and emails are stored at odd indices.
 */
public class PlayerAdapter extends BaseAdapter implements ListAdapter {

    ArrayList<String> players;
    Context parent;

    public PlayerAdapter(Context c, ArrayList<String> p) {
        this.parent = c;
        this.players = p;
    }

    @Override
    public int getCount() {
        return players.size()/2;
    }

    @Override
    public String getItem(int i) {
        return players.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i/2;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if (view == null) {
            LayoutInflater inflater;
            inflater = LayoutInflater.from(parent);
            view = inflater.inflate(R.layout.row_player, null);
        }

        ((ViewGroup) view).getChildAt(0);


        String p = getItem(i);

       /* if (p != null) {
            TextView name = (TextView) view.findViewById(R.id.player_name);
            TextView email = (TextView) view.findViewById(R.id.player_email);

            if (name != null) {
                name.setText(p.getLeft());
            }

            if (email != null) {
                email.setText(p.getRight());
            }

        }*/

        return view;
    }
}

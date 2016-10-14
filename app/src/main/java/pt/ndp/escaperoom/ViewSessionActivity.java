package pt.ndp.escaperoom;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by VIP on 7/09/2016.
 */
public class ViewSessionActivity extends AppCompatActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_summary);

        TextView room = (TextView) findViewById(R.id.summary_room_display);
        TextView timestamp = (TextView) findViewById(R.id.summary_timestamp_display);
        TextView timelimit = (TextView) findViewById(R.id.summary_timelimit_display);
    }

}
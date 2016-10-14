package pt.ndp.escaperoom;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;


/**
 *
 */
public class AdminActivity extends AppCompatActivity {

    ListView playerDisplay;
    
    EditText inputPassword;
    EditText inputHours;
    EditText inputMinutes;
    EditText inputSeconds;

    Spinner roomsList;
    Button launchButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        TextView filesDirectory = (TextView) findViewById(R.id.files_directory);
        filesDirectory.setText("Searching for files in: " +
                Utils.findRoomDirectory(this).getPath());

        roomsList = (Spinner) findViewById(R.id.list_rooms);
        roomsList.setAdapter(getRooms());

    }

    /**
     * Validate inputs, then get the allowed time and the room file from the appropriate views and
     * start PlayerActivity.
     * @param view
     */
    public void launchRoom(View view) {
        if (roomsList.getSelectedItem() == null) {
            Utils.makeToast("No room chosen.", this);
            return;
        }
        long allowedTime = getTime();
        if (allowedTime < 1) {
            Utils.makeToast("Allowed time must be positive.", this);
            return;
        }
        Intent intent = new Intent(this, PlayerActivity.class);
        intent.putExtra(PlayerActivity.ALLOWED_TIME_KEY, allowedTime);
        String roomName = roomsList.getSelectedItem().toString();
        File roomPath = new File(Utils.findRoomDirectory(this), roomName);
        intent.putExtra(PlayerActivity.ROOM_KEY, roomPath);
        startActivity(intent);
    }

    public void addPlayer(View view) {
        // TODO: Allow admin to save players
    }

    /**
     * Converts the 'hours', 'minutes' and 'seconds' fields on the room setup screen into the
     * equivalent number of milliseconds.
     * @return The amount of time that should be on the clock, in milliseconds.
     */
    public long getTime() {
        inputSeconds = (EditText) findViewById(R.id.input_seconds);
        inputMinutes = (EditText) findViewById(R.id.input_minutes);
        inputHours = (EditText) findViewById(R.id.input_hours);
        int seconds = Utils.getNumberFromField(inputSeconds, this);
        int minutes = Utils.getNumberFromField(inputMinutes, this);
        int hours = Utils.getNumberFromField(inputHours, this);

        return hours * 3600000 + minutes * 60000 + seconds * 1000;
    }

    /**
     * Fills an ArrayAdapter with the names of the files in the directory returned by
     * Utils.findRoomDirectory(). If that directory doesn't exist, will return an empty adapter.
     * @return The ArrayAdapter thus created.
     */
    public ArrayAdapter getRooms() {
        ArrayAdapter<CharSequence> adapter;
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        File roomPath = Utils.findRoomDirectory(this);

        if (roomPath.list() == null) {
            Log.d("Filepath", "Rooms list is null.");
            return adapter;
        }
        for (String room: roomPath.list()) {
            if (room.endsWith(".xml")) {
                adapter.add(room);
            }
        }

        return adapter;
    }



}

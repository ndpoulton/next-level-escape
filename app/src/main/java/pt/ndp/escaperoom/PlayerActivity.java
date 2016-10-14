package pt.ndp.escaperoom;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.HashMap;

/**
 * This activity handles all interaction with the player. Upon initialization, it shows
 * activity_player.xml. When the game finishes, it shows ending_screen.xml.
 * TODO: Allow the player to move back and forth between the two views
 */
public class PlayerActivity extends AppCompatActivity implements Timed {


    // A means to exit the player interface and return to the admin interface.
    private static final String adminCode = "stewardesses";

    public final int NOTIFICATION_INITIAL = 1;
    NotificationManager mNotificationManager;
    NotificationCompat.Builder mBuilder;
    Intent returnIntent;
    BroadcastReceiver returnReceiver;

    // These are the keys used to store the activity's persistent data.
    public static final String ALLOWED_TIME_KEY = "pt.ndp.escaperoom.TIME_REMAINING";
    public static final String ROOM_KEY = "pt.ndp.escaperoom.ROOM";
    public static final String CONDITIONS_KEY = "pt.ndp.escaperoom.CONDITIONS";
    public static final String SCORE_KEY = "pt.ndp.escaperoom.SCORE";

    Room room;
    File roomPath;

    private TextView countdown;
    TimeLimit timer;

    TextView feedback;
    int sequenceNumber;
    EditText input;

    TextView scoreDisplay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        countdown = (TextView) findViewById(R.id.display_countdown);
        scoreDisplay = (TextView) findViewById(R.id.display_score);
        input = (EditText) findViewById(R.id.input);
        feedback = (TextView) findViewById(R.id.feedback);
        sequenceNumber = 0;


        // Show on top of lock screen, don't let the screen go off
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);


        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            roomPath = (File)savedInstanceState.getSerializable(ROOM_KEY);
            room = Room.restore(roomPath, (HashMap<String, Boolean>)
                                            savedInstanceState.getSerializable(CONDITIONS_KEY));
            room.score = savedInstanceState.getInt(SCORE_KEY);
            long time = savedInstanceState.getLong(ALLOWED_TIME_KEY);
            if (time > 2001) {
                updateScoreDisplay();
                beginCountdown(time);
            } else {
                end();
            }
            Log.d("Continuity", "Recreating activity");
        }
        // If we came here from an Intent that has appropriate data attached
        else if (getIntent().getLongExtra(ALLOWED_TIME_KEY, -1) != -1) {
            roomPath = (File)getIntent().getSerializableExtra(ROOM_KEY);
            room = Room.fromFile(roomPath);
            beginCountdown(getIntent().getLongExtra(ALLOWED_TIME_KEY, -1));
            Log.d("Continuity", "Starting activity from Intent");
        }
        else {
            // We should never get here. Panic!
            Log.d("Fatal Error", "PlayerActivity.onCreate(): " +
                    "unable to find any valid input. Could not initialize room.");
            finish();
        }
        ViewGroup vg = (ViewGroup) findViewById(R.id.player_root);
        if (vg != null) {
            vg.setBackground(room.background);
        }
        returnReceiver = new ScreenOffReceiver();
        registerReceiver(returnReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
        //sendNotification(NOTIFICATION_INITIAL);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(returnReceiver);
        mNotificationManager.cancel(NOTIFICATION_INITIAL);
        timer.cancel();
        super.onDestroy();
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        if (timer != null) {
            savedInstanceState.putLong(ALLOWED_TIME_KEY, this.timer.timeRemaining);
        } else {
            savedInstanceState.putLong(ALLOWED_TIME_KEY, 0);
        }
        savedInstanceState.putSerializable(ROOM_KEY, this.roomPath);
        savedInstanceState.putSerializable(CONDITIONS_KEY, this.room.encode());
        savedInstanceState.putInt(SCORE_KEY, this.room.score);
        super.onSaveInstanceState(savedInstanceState);
    }

    public void renotify(long timeRemaining) {
        returnIntent.putExtra(ALLOWED_TIME_KEY, timeRemaining);
        PendingIntent pi = PendingIntent.getActivity(this, 0, returnIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        this.mBuilder.setContentIntent(pi);
        mNotificationManager.notify(NOTIFICATION_INITIAL, mBuilder.build());
    }



    /**
     * Sends a notification to the system, thereby allowing a user to return to the app from
     * the lock screen.
     * Mostly copy-pasted off developer.android.com.
     * @param mId The ID of the notification to be sent
     */
    private void sendNotification(int mId) {
        this.mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.hourglass)
                        .setContentTitle("Escape the Room")
                        .setContentText("A countdown is active.");
        this.mBuilder.setShowWhen(false);
        this.returnIntent = new Intent(this, PlayerActivity.class);
        this.returnIntent.putExtra(ROOM_KEY, this.roomPath);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(PlayerActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(returnIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        mNotificationManager.notify(mId, mBuilder.build());
    }


    /**
     * This is the OnClick method for the "Submit" button under the code entry field. It removes
     * the text that's in there, checks if it needs to do anything outside the context of the game,
     * then calls the appropriate methods to do game-related stuff.
     */
    public void inputCode(View view) {
        String inputText = input.getText().toString();
        // If admin code is entered, exit and return to admin interface..
        if (inputText.equals(adminCode)) {
            Intent intent = new Intent(this, AdminActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        input.setText("");
        validateCode(inputText);
        updateScoreDisplay();
    }


    /**
     * Gets score from this activity's score attribute and places it in the scoreDisplay text field.
     */
    void updateScoreDisplay() {
        scoreDisplay.setText(String.format("%d", room.score));
    }


    /**
     * Signals that the game is finished.
     */
    public void end() {
        updateTime(0);
        playEndSound();
        mNotificationManager.cancel(NOTIFICATION_INITIAL);
        showEndingScreen();
    }

    /**
     * Initialize the ending screen and switch the view to it.
     */
    private void showEndingScreen() {
        TextView scoreView = (TextView) findViewById(R.id.ending_score_display);
        String scoreText = "" + this.room.score + " / " + this.room.maxScore;
        scoreView.setText(scoreText);

        ListView achievements = (ListView) findViewById(R.id.ending_achievements_display);
        achievements.setAdapter(new AchievementAdapter(this));

        ViewGroup vg = (ViewGroup) findViewById(R.id.ending_root);
        vg.setBackground(room.background);

        this.setContentView(R.layout.ending_screen);
    }

    /**
     * Play a sound indicating that the game is over.
     */
    public void playEndSound() {
        MediaPlayer mp = MediaPlayer.create(this, R.raw.endsound);
        mp.start();
    }

    /**
     * Change the time remaining on the onscreen timer to the specified number of milliseconds.
     * This is designed to be called every tick by a TimeLimit object. Other classes may call it,
     * but unless the timer is stopped somehow before doing so, there won't be very much point.
     * @param timeRemaining The amount of time to be shown on the clock, in milliseconds.
     */
    public void updateTime(long timeRemaining) {
        long secUntilFinished = timeRemaining / 1000;
        long minutes = secUntilFinished / 60;
        long seconds = secUntilFinished % 60;
        // Handle case where seconds are in single digits.
        // This should probably use String.format(), but I forgot how to make it do padding.
        String padding = "";
        if (seconds < 10) padding = "0";
        this.countdown.setText("" + minutes + ":" + padding + seconds);
    }


    /**
     * Make a TimeLimit object and start it. That TimeLimit will become the activity's
     * designated time limit.
     * @param allowedTime The amount of time initially on the clock.
     * @return The TimeLimit object which keeps track of the time.
     */
    public TimeLimit beginCountdown(long allowedTime) {
        this.timer = new TimeLimit(allowedTime,1000,this);
        this.timer.start();
        return this.timer;
    }

    /**
     * Takes a code and checks to see whether it should be considered correct, then performs
     * the appropriate action.
     * // TODO: Atomize this method - split the actions into their own methods
     * @param in The string to be checked for correctness.
     * @return Whether the string was correct. Will also be false if it was previously entered.
     */
    public boolean validateCode(String in) {
        Resources res = getResources();
        // Check that the answer is of the correct length
        if (room.codelen != 0 && in.length() != room.codelen) {
            updateFeedback(res.getString(R.string.error_length, room.codelen));
            return false;
        }
        // Check to see if answer matches a code
        for (Code code: room.codes) {
            if (in.equalsIgnoreCase(code.name) && !code.isAnswered()) {
                updateFeedback(code.message);
                code.setAnswered(true);
                room.score += code.increment;
                return true;
            } // If answer matches an already-entered code
            else if (in.equalsIgnoreCase(code.name)) {
                updateFeedback(res.getString(R.string.already_answered));
                return false;
            }
        }
        // Answer is incorrect, deduct appropriate amount of points
        room.score -= room.decrement;
        // If we have been provided with things to say when the player is incorrect, then say them.
        if (room.incorrect != null) {
            updateFeedback(this.room.incorrect.get(sequenceNumber));
            if (sequenceNumber < this.room.incorrect.size() - 1) {
                sequenceNumber++;
            } else {
                sequenceNumber = 0;
            }
        } // Otherwise, use a generic notification of incorrectness.
        else {
            updateFeedback(res.getString(R.string.default_incorrect));
        }
        return false;
    }

    /**
     * Places a message in the feedback field of the player interface.
     * @param message The message to display
     */
    void updateFeedback(String message) {
        this.feedback.setText(message);
    }

    @Override
    public void onBackPressed() {
        // If the achievement screen is showing, return to the play screen.
        if (this.getCurrentFocus() == findViewById(android.R.id.content)) {
            this.setContentView(R.layout.activity_player);
        }
        // Otherwise, do nothing. Player cannot use the back button during play.
    }


}

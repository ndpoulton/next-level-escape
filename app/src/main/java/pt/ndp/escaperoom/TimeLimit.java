package pt.ndp.escaperoom;

import android.os.CountDownTimer;

/**
 * A concrete implementation of CountDownTimer, which takes a parent and calls its updateTime()
 * method every tick, and its end method() upon finishing.
 */
public class TimeLimit extends CountDownTimer {

    Timed parent;
    long timeRemaining;

    public TimeLimit(long millisUntilFinished, long countDownInterval, Timed parent) {
        super(millisUntilFinished, countDownInterval);
        this.parent = parent;
        this.timeRemaining = millisUntilFinished;
    }

    public void onTick(long millisUntilFinished) {
        // Reminder: this only happens every tick. That's probably once every second.
        parent.updateTime(millisUntilFinished);
        this.timeRemaining = millisUntilFinished;
    }

    public void onFinish() {
        parent.end();
    }
}

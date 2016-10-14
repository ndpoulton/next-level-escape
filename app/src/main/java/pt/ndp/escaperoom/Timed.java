package pt.ndp.escaperoom;

/**
 * Designed to be implemented by activities under a time limit. See also: TimeLimit.java.
 */
public interface Timed {

    /**
     * This method is called every tick, and is used to keep the activity informed of how much
     * time remains.
     * @param timeRemaining The amount of time remaining on the clock.
     */
    void updateTime(long timeRemaining);

    /**
     * This method is called when time expires.
     */
    void end();
}

package pt.ndp.escaperoom;

/**
 * An object implementing Answerable is required to keep track of a condition, and to report on
 * whether that condition has been fulfilled. Codes are an example of an Answerable: when the player
 * has entered them, their condition has been fulfilled.
 */
public interface Answerable {
    boolean isAnswered();
}

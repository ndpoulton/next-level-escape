package pt.ndp.escaperoom;

import java.util.ArrayList;

/**
 * A trigger is a list of conditions, which must implement the Answerable interface.
 * When all conditions are met, the trigger is considered to have been met.
 */
public class Trigger {
    public ArrayList<Answerable> conditions;

    /**
     * Iterate over the list of conditions. If they are all met, return true.
     * Otherwise, return false.
     * @return Whether the trigger has been met or not.
     */
    public boolean isMet() {
        for (Answerable condition : conditions) {
            if (!condition.isAnswered()) {
                return false;
            }
        }
        return true;
    }
}
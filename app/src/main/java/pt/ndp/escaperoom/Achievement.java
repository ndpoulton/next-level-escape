package pt.ndp.escaperoom;

import android.graphics.drawable.Drawable;

import java.util.ArrayList;

/**
 * An achievement is a reward which the player gets for doing something smart.It is defined in
 * a room's XML file.
 *
 * An achievement is considered to have been unlocked if a call to the isMet() method of any of its
 * Trigger objects returns true. As such, an achievement can be unlocked in multiple different ways.
 */
public class Achievement {
    public String name;
    public String description;
    public String text;
    public String iconPath;
    public Drawable icon;
    public ArrayList<Trigger> triggers;

    public boolean isAchieved() {
        for (Trigger trigger: triggers) {
            if (trigger.isMet()) {
                return true;
            }
        }
        return false;
    }
}

package pt.ndp.escaperoom;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.thoughtworks.xstream.XStream;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * This class contains all the data required to run an escape room session, such as the codes and
 * whatnot.It is responsible for making new instances of itself by decoding XML files, and it is
 * also responsible for saving and loading session data.
 */
public class Room {


    String name;
    int codelen;
    int decrement;
    int maxScore;
    ArrayList<Code> codes;
    ArrayList<Achievement> achievements;
    ArrayList<String> incorrect;

    String backgroundPath;
    Drawable background;

    int score;

    long timeCreated;

    /**
     * Creates a room object from properly-formatted XML.
     * TODO: Make this robust. What happens if an exception is thrown?
     *
     * @param xml The path to an XML file containing the room's data.
     */
    public static Room fromFile(File xml) {
        XStream xstream = new XStream();
        xstream.alias("code", Code.class);
        xstream.alias("room", Room.class);
        xstream.alias("achievement", Achievement.class);
        xstream.alias("trigger", Trigger.class);
        xstream.alias("condition", Answerable.class);
        xstream.alias("message", String.class);
        xstream.addImplicitCollection(Room.class, "codes", "code", Code.class);
        xstream.addImplicitCollection(Room.class, "achievements", "achievement", Achievement.class);
        xstream.addImplicitCollection(Achievement.class, "triggers", "trigger", Trigger.class);
        xstream.addImplicitCollection(Trigger.class, "conditions", "condition", Answerable.class);

        Room room = (Room) xstream.fromXML(xml);
        room.backgroundPath = xml.getParent() + "/" + room.backgroundPath;
        room.background = Drawable.createFromPath(room.backgroundPath);
        for (Achievement a: room.achievements) {
            a.iconPath = xml.getParent() + "/" + a.iconPath;
            a.icon = Drawable.createFromPath(a.iconPath);
        }
        room.score = 0; // I think this is redundant.
        room.timeCreated = Calendar.getInstance().getTimeInMillis();
        return room;
    }

    /**
     * Turn the room's codes into a hash map, with the names as the keys and the 'isAnswered'
     * status as the values. Codes are the only thing that this method saves, because the codes
     * that have been answered is the only thing that currently changes between different sessions
     * of the same room.
     *
     * In future, it might be wise to iterate through this class's fields
     * and find all the Answerables, instead of enumerating them here manually.
     * @return A hash map containing the session's current status.
     */
    public HashMap<String, Boolean> encode() {
        HashMap map = new HashMap<>();
        for (Code code: codes) {
            map.put(code.name, code.isAnswered());
        }
        return map;
    }

    /**
     * Builds the room from the provided xml file, and then restores it to the state saved in the
     * provided hash map.
     * @param xml A properly-formatted xml file containing the elements required to build a room.
     * @param conditions A hash map which contains, as keys, the names of all the codes in the
     *                   provided xml file. The values mapped to the keys should indicate whether
     *                   that code has been entered.
     * @return The restored room.
     */
    public static Room restore(File xml, HashMap<String, Boolean> conditions) {
        Room room = Room.fromFile(xml);
        for (Code code: room.codes) {
            // value will be null if the code does not exist in the HashMap.
            // In other words, it will be null if there is a mismatch between the xml file used
            // to load and the xml file used to save
            Boolean value = conditions.get(code.name);
            if (value != null) {
                code.setAnswered(conditions.get(code.name));
            }
            else {
                // A code doesn't exist in the hashmap. Wrong room.
                // TODO: Notify user if their xml file is incomplete
            }
        }
        return room;
    }




}
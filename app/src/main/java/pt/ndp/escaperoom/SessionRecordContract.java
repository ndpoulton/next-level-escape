package pt.ndp.escaperoom;

import android.provider.BaseColumns;

/**
 * This is the canonical place for recording the database schema. All references to column and
 * table names have to come from here. That way, it's relatively easy to look at and change
 * the schema.
 */
public final class SessionRecordContract {

    /**
     * Class not designed to be instantiated.
     */
    public SessionRecordContract() {}

    public static abstract class SessionEntry implements BaseColumns {
        public static final String TABLE_NAME = "Session";
        public static final String COLUMN_NAME_TIME = "Time";
        public static final String COLUMN_NAME_ROOM = "Room";
        public static final String COLUMN_NAME_SCORE = "Score";
        public static final String COLUMN_NAME_CONDITIONS = "Conditions";
    }

    // A PlayerSession is a record of a player participating in a session.
    // Since a single player can participate in many sessions, and a single session can have many
    // players, it's easiest to do it with a separate table.
    public static abstract class PlayerSession implements BaseColumns {
        public static final String TABLE_NAME = "PlayerSession";
        public static final String COLUMN_NAME_PLAYER_ID = "PlayerID";
        public static final String COLUMN_NAME_SESSION_ID = "SessionID";
    }

    public static abstract class PlayerEntry implements BaseColumns {
        public static final String TABLE_NAME = "Player";
        public static final String COLUMN_NAME_FIRST_NAME = "FirstName";
        public static final String COLUMN_NAME_LAST_NAME = "LastName";
        public static final String COLUMN_NAME_EMAIL = "EmailAddress";
    }
}

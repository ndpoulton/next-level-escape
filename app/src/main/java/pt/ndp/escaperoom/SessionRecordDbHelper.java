package pt.ndp.escaperoom;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import pt.ndp.escaperoom.SessionRecordContract.*;

/**
 * This class stores and executes SQL statements for the session record database. As such, it's
 * responsible for saving stuff to and loading stuff from the database.
 */
public class SessionRecordDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "sessions.db";

    public static final String NOTNULL = " NOT NULL";
    public static final String SEP = ",";
    public static final String END = ")";

    public static final String SQL_CREATE_SESSION =
            "CREATE TABLE " + SessionEntry.TABLE_NAME + " (" +
                    SessionEntry._ID + " INTEGER PRIMARY KEY" + SEP +
                    SessionEntry.COLUMN_NAME_ROOM + " TEXT" + SEP +
                    SessionEntry.COLUMN_NAME_TIME + " INTEGER" + SEP +
                    SessionEntry.COLUMN_NAME_SCORE + " INTEGER" + SEP +
                    SessionEntry.COLUMN_NAME_CONDITIONS + " BLOB" + END;

    public static final String SQL_CREATE_PLAYER =
            "CREATE TABLE " + PlayerEntry.TABLE_NAME + " (" +
                    PlayerEntry._ID + " INTEGER PRIMARY KEY" + SEP +
                    PlayerEntry.COLUMN_NAME_FIRST_NAME + " TEXT" + NOTNULL + SEP +
                    PlayerEntry.COLUMN_NAME_LAST_NAME + " TEXT" + SEP +
                    PlayerEntry.COLUMN_NAME_EMAIL + " TEXT" + END;

    public static final String SQL_CREATE_PLAYER_SESSION =
            "CREATE TABLE " + PlayerSession.TABLE_NAME + " (" +
                    PlayerSession.COLUMN_NAME_PLAYER_ID + " INTEGER" + NOTNULL + SEP +
                    PlayerSession.COLUMN_NAME_SESSION_ID + " INTEGER" + NOTNULL + SEP +
                    "FOREIGN KEY(" + PlayerSession.COLUMN_NAME_SESSION_ID + ") REFERENCES " +
                    SessionEntry.TABLE_NAME + "(" + SessionEntry._ID + ")" + SEP +
                    "FOREIGN KEY(" + PlayerSession.COLUMN_NAME_PLAYER_ID + ") REFERENCES " +
                    PlayerEntry.TABLE_NAME + "(" + PlayerEntry._ID + ")" + END;



    public SessionRecordDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_SESSION);
        db.execSQL(SQL_CREATE_PLAYER);
        db.execSQL(SQL_CREATE_PLAYER_SESSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        // No upgrade policy.
    }

    public static final String SQL_SAVE_ROOM =
            "INSERT INTO " + SessionEntry.TABLE_NAME + " (" +
                    SessionEntry.COLUMN_NAME_ROOM + SEP +
                    SessionEntry.COLUMN_NAME_TIME + SEP +
                    SessionEntry.COLUMN_NAME_SCORE + SEP +
                    SessionEntry.COLUMN_NAME_CONDITIONS + ") VALUES (?,?,?,?)";

    public void saveRoomToDatabase(SQLiteDatabase db, Room room) {
        byte[] conditions = Utils.toByteArray(room.encode());
        SQLiteStatement statement = db.compileStatement(SQL_SAVE_ROOM);
        statement.bindString(1, room.name);
        statement.bindLong(2, room.timeCreated);
        statement.bindLong(3, room.score);
        statement.bindBlob(4, conditions);
        statement.executeInsert();
    }
}

package pt.ndp.escaperoom;

import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

/**
 * Opens a database for writing.
 */
public class OpenDatabaseTask extends AsyncTask<SessionRecordDbHelper, Void, SQLiteDatabase> {

    @Override
    protected SQLiteDatabase doInBackground(SessionRecordDbHelper... helpers) {
        return helpers[0].getWritableDatabase();
    }
}
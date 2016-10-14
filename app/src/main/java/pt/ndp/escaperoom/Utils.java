package pt.ndp.escaperoom;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Contains helper functions designed to be used by multiple classes.
 *
 * Outstanding issues:
 *
 * No custom 'failed achievement' text
 * No custom ending text
 * It doesn't look cool.
 * Have to kill app to return from ending screen
 * Can't send emails to players
 */
public class Utils {

    public static void makeToast(String s, Context c) {
        Toast toast = Toast.makeText(c, s, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static int getNumberFromField(EditText in, Context c) {
        String str = in.getText().toString();
        if (str.equals("")) {
            return 0;
        }
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            makeToast("Could not interpret number field.", c);
            return 0;
        }
    }

    public static File findRoomDirectory(Context c) {
        File roomDirectory = c.getExternalFilesDir(null);
        if (roomDirectory == null) {
            Log.d("Filepath", "External storage not mounted.");
            return null;
        }
        roomDirectory.mkdirs();
        if (!roomDirectory.isDirectory()) {
            Log.d("Filepath", "Could not create rooms directory.");
        }
        return roomDirectory;
    }

    public static byte[] toByteArray(Serializable in) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        byte[] bytes = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(in);
            bytes = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            // TODO: Make this obvious to the user
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                // ignore close exception
            }
            try {
                bos.close();
            } catch (IOException e) {
                // ignore close exception
            }
        }
        return bytes;
    }

    public static Object fromByteArray(byte[] in) {
        ByteArrayInputStream bis = new ByteArrayInputStream(in);
        ObjectInput oi = null;
        Object object = null;
        try {
            oi = new ObjectInputStream(bis);
            object = oi.readObject();

        } catch (Exception e) {
            e.printStackTrace();
            // TODO: Make this more obvious to the user
        }
        finally {
            try {
                bis.close();
            } catch (IOException ex) {
                // ignore close exception
            }
            try {
                if (oi != null) {
                    oi.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }
        return object;
    }


}
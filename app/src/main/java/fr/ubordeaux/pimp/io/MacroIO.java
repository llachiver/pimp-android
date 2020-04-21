package fr.ubordeaux.pimp.io;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import fr.ubordeaux.pimp.fragments.Macro;

/**
 * Class containing several static methods to write and read macros of effects
 */
public class MacroIO {

    private static HashMap<Macro, Integer> macrosIDs = new HashMap<>();
    private static int currentID = 0;

    /**
     * Create a "macros" folder in app files, only if not alreay existing.
     *
     * @param context App context
     * @return false if something goes wrong
     **/
    public static boolean createMacroDir(Context context) {
        File f = new File(context.getFilesDir(), "macros");
        return f.mkdir();
    }

    /**
     * Scan macros folder and load all macros in it.
     *
     * @param context of the app
     * @return false if something goes wrong. May be use {@link #createMacroDir(Context)}
     */
    public static boolean loadAllMacros(Context context) {
        File dir = new File(context.getFilesDir(), "macros");
        String[] files = dir.list();
        if (files == null) {
            Log.e("pimp", "No folder \"macros\"");
            return false;
        }

        //Scan files :
        int highest = -1;
        for (String file : files) {
            String[] array = file.split("\\.(?=[^\\.]+$)");
            try {
                if (array[1].equals("macro")) {
                    int n = Integer.valueOf(array[0]);
                    if (n > highest) highest = n;
                    //read file :
                    File f = new File(context.getFilesDir(), "macros/" + file);
                    Log.v("pimp_log", f + "--------" + file);
                    try (ObjectInput oi = new ObjectInputStream(new FileInputStream(f))) {
                        Macro macro;
                        macro = (Macro) oi.readObject();
                        Log.v("pimp_log", macro + "");
                        macrosIDs.put(macro, n);

                    }
                } else {
                    Log.e("pimp_log", "There is a file with a wrong extension in macros folder");
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                Log.e("pimp_log", "There is a file without extension in macros folder");
            } catch (NumberFormatException e) {
                Log.e("pimp_log", "There is a file without name as integer in macro folder");
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                Log.e("pimp_log", "Reading problem");
            }
        }

        currentID = highest + 1;
        Log.v("pimp_log", "The next macro to be save will be called " + currentID);

        return true;
    }

    /**
     * Remove a macro file.
     *
     * @param macro   the macro to delete
     * @param context of the app
     * @return false if something goes wrong.
     */
    public static boolean deleteMacro(Macro macro, Context context) {

        return false;
    }

    /**
     * Add a macro file.
     *
     * @param macro   the macro to save in a file
     * @param context of the app
     * @return false if something goes wrong.
     */
    public static boolean saveMacro(Macro macro, Context context) {

        try {
            File f = new File(context.getFilesDir(), "macros/" + currentID + ".macro");
            if (f.createNewFile()) {
                Log.v("pimp_log", "File created: " + f.getAbsolutePath());
                //write macro in file :
                try (ObjectOutput oo = new ObjectOutputStream(new FileOutputStream(f))) {
                    oo.writeObject(macro);
                }

            } else {
                Log.v("pimp_log", "File already exists.");
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            currentID++;
            return false;
        }
        currentID++;
        return true;
    }

    /**
     * Usually used just after {@link #loadAllMacros(Context)}.
     *
     * @return macros loaded in memory.
     */
    public static ArrayList<Macro> getLoadedMacros() {
        return new ArrayList<>(macrosIDs.keySet());
    }

}

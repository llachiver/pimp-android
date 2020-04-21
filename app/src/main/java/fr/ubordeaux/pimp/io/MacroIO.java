package fr.ubordeaux.pimp.io;

import java.util.ArrayList;
import java.util.HashMap;

import fr.ubordeaux.pimp.fragments.Macro;

/**
 * Class containing several static methods to write and read macros of effects
 */
public class MacroIO {

    private static HashMap<Macro, Integer> macrosIDs = new HashMap<>();


    /**
     * Scan macros folder and load all macros in it.
     *
     * @return false if something goes wrong
     */
    public static boolean loadAllMacros() {

        return false;
    }

    /**
     * Remove a macro file.
     *
     * @param macro the macro to delete
     * @return false if something goes wrong.
     */
    public static boolean deleteMacro(Macro macro) {

        return false;
    }

    /**
     * Add a macro file.
     *
     * @param macro the macro to save in a file
     * @return false if something goes wrong.
     */
    public static boolean saveMacro(Macro macro) {

        return false;
    }

    /**
     * Usually used just before {@link #loadAllMacros()}.
     *
     * @return macros loaded in memory.
     */
    public static ArrayList<Macro> getLoadedMacros() {
        ArrayList<Macro> macros = new ArrayList<>();
        for (Macro key : macrosIDs.keySet()) {
            macros.add(key);
        }
        return macros;
    }

}

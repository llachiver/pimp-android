package fr.ubordeaux.pimp.fragments;


/**
 * Using a card patern, see  {@link MacroAdapter}.
 * But also a class to represent a macro of several effects.
 */
public class Macro {


    private String name;
    private String info;

    /**
     * Constrcutor
     *
     * @param name First line, name of the macro
     * @param info Second line to print
     */
    public Macro(String name, String info) {
        this.name = name;
        this.info = info;
    }


    /**
     * @return Name of the personal effect macro
     */
    String getName() {
        return name;
    }

    /**
     * @return Second line of the personal effect macro
     */
    String getInfoLine() {
        return info;
    }
}

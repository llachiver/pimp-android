package fr.ubordeaux.pimp.fragments;


/**
 * Object used with {@link MacroAdapter}.
 */
public class MacroCard {


    private String name;
    private String info;

    /**
     * Constrcutor
     *
     * @param name First line, name of the macro
     * @param info Second line to print
     */
    public MacroCard(String name, String info) {
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

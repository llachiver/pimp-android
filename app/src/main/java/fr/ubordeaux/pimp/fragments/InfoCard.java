package fr.ubordeaux.pimp.fragments;

/**
 * Object used by {@link InfoAdapter} to show some informations on two lines with an icone.
 */
public class InfoCard {
    private int imageResource;
    private String mainLine;
    private String secondLine;

    /**
     * Constrcutor
     *
     * @param imageResource Resource ID of the icone
     * @param main          First line to print
     * @param second        Second line to print
     */
    public InfoCard(int imageResource, String main, String second) {
        this.imageResource = imageResource;
        this.mainLine = main;
        this.secondLine = second;
    }

    /**
     * @return Resource ID of the icone
     */
    int getImageResource() {
        return imageResource;
    }

    /**
     * @return First line of informations
     */
    String getMainLine() {
        return mainLine;
    }

    /**
     * @return Second line of informations
     */
    String getSecondLine() {
        return secondLine;
    }
}

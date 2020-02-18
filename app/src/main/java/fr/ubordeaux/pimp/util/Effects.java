package fr.ubordeaux.pimp.util;

public enum Effects {
    BRIGHTNESS(new String[]{"Brightness"}, null),
    SATURATION(new String[]{"Saturation"},null),
    CONTRAST(new String[]{"Contrast"},new String[]{"Equalization"}),
    SELECTHUE(new String[]{"Hue","Tolerance"},new String[]{"Keep color", "Change hue"});



    private String[] seekbars = {};
    private String[] buttons = {};

    //Constructeur
    Effects(String[] seekbars, String[] buttons){
        this.seekbars = seekbars;
        this.buttons = buttons;
    }

    public String[] getSeekbars(){
        return seekbars;
    }

    public String[] getButtons(){
        return buttons;
    }
}

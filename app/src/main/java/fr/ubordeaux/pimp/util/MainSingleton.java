package fr.ubordeaux.pimp.util;

import fr.ubordeaux.pimp.activity.MainActivity;

public enum MainSingleton {
    INSTANCE;

    MainActivity context;

    public MainActivity getContext(){
        return context;
    }

    public void setContext(MainActivity context){
        this.context = context;
    }
}

package fr.ubordeaux.pimp.util;

import fr.ubordeaux.pimp.activity.MainActivity;

public final class MainSingleton {
    private static class Holder
    {
        private static final MainSingleton singleton = new MainSingleton();
        private static MainActivity context = null;
    }

    private MainSingleton() {
    }

    public static MainSingleton getInstance()
    {
        return Holder.singleton;
    }

    public static void setContext(MainActivity context)
    {
        Holder.context = context;
    }

    public static MainActivity getContext ()
    {
        return Holder.context;
    }
}

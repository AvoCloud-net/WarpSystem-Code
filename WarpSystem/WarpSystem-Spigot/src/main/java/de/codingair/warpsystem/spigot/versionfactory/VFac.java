package de.codingair.warpsystem.spigot.versionfactory;

import java.lang.reflect.InvocationTargetException;

public class VFac {
    public static final String PATH = "de.codingair.warpsystem.spigot.versionfactory.";
    public static final String OBJECTS = PATH + "objects.";

    public static <A> A build(Class<A> c, VKey key, Object... args) {
        return c.cast(build(key, args));
    }

    public static <A> A build(VKey key, Object... args) {
        return build(key.getPath(), args);
    }

    private static <A> A build(String path, Object... args) {
        try {
            Class<?> c = Class.forName(path);
            return build(c, args);
        } catch(ClassNotFoundException e) {
            throw new IllegalStateException("Could not build an instance with path=\"" + path + "\".");
        }
    }

    private static <A> A build(Class<?> c, Object... args) {
        try {
            Class<?>[] classes = new Class[args.length];
            for(int i = 0; i < args.length; i++) {
                classes[i] = args[i].getClass();
            }

            if(classes.length == 0) return (A) c.newInstance();
            return (A) c.getConstructor(classes).newInstance(args);
        } catch(InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new IllegalStateException("Could not build an instance of " + c.getName() + ".");
        }
    }

    public static boolean isAvailable(String path) {
        try {
            Class.forName(OBJECTS + path);
            return true;
        } catch(ClassNotFoundException e) {
            return false;
        }
    }
}

package de.codingair.warpsystem.spigot.versionfactory;

import de.codingair.codingapi.server.reflections.IReflection;

public class VFac {
    public static final String PATH = "de.codingair.warpsystem.spigot.versionfactory.";
    public static final String OBJECTS = PATH + "objects.";
    public static final String GUI = PATH + "gui.";
    public static final String HANDLERS = PATH + "handlers.";
    public static final String FEATURE_OBJECTS = PATH + "featureobjects.";

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
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Could not build an instance with path=\"" + path + "\".", e);
        }
    }

    public static <A> A buildOr(VKey key, A def, Object... args) {
        return build(key.getPath(), def, args);
    }

    private static <A> A build(String path, A def, Object... args) {
        try {
            Class<?> c = Class.forName(path);
            return build(c, args);
        } catch (ClassNotFoundException e) {
            return def;
        }
    }

    private static <A> A build(Class<?> c, Object... args) {
        try {
            Class<?>[] classes = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                classes[i] = args[i].getClass();
            }

            //noinspection unchecked
            return (A) IReflection.getConstructor(c, classes).newInstance(args);
        } catch (NullPointerException | ClassCastException e) {
            throw new IllegalStateException("Could not build an instance of " + c.getName(), e);
        }
    }

    public static boolean isAvailable(String path) {
        try {
            Class.forName(OBJECTS + path);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}

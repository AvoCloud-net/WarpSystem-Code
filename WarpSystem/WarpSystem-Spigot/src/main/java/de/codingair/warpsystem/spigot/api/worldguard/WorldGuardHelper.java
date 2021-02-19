package de.codingair.warpsystem.spigot.api.worldguard;

import de.codingair.warpsystem.spigot.base.WarpSystem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.stream.Stream;

public class WorldGuardHelper {
    private static final boolean enabled;
    private static WorldGuardAdapter adapter;

    static {
        enabled = Bukkit.getPluginManager().isPluginEnabled("WorldGuard");
        if (enabled) {
            try {
                adapter = new WorldGuardAdapter();
            } catch (ClassNotFoundException | NoClassDefFoundError e) {
                try {
                    adapter = new WorldGuardAdapter_12();
                } catch (ClassNotFoundException | NoClassDefFoundError e1) {
                    WarpSystem.getInstance().getLogger().log(Level.WARNING, "Could not hook into WorldGuard. Please contact the author!");
                }
            }
        }

    }

    public static @Nullable Stream<String> getRegion(@NotNull Location location) {
        if (!enabled) return null;

        try {
            return adapter.getRegion(location);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }
}

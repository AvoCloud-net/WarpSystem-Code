package de.codingair.warpsystem.spigot.api.worldguard;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class WorldGuardHelper {
    private static final boolean enabled = Bukkit.getPluginManager().isPluginEnabled("WorldGuard");

    public static @Nullable Stream<String> getRegion(@NotNull Location location) {
        if (!enabled) return null;

        return WorldGuardAdapter.getRegion(location);
    }
}

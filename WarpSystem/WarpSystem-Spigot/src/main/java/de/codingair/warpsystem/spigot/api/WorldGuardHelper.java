package de.codingair.warpsystem.spigot.api;

import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class WorldGuardHelper {
    private static final boolean enabled = Bukkit.getPluginManager().isPluginEnabled("WorldGuard");

    public static @Nullable String getRegion(@NotNull Location location) {
        if(!enabled) return null;

        World w = location.getWorld();
        if(w == null) throw new IllegalArgumentException("The location '" + location.toString() + "' does not provide a world!");

        RegionManager man = WorldGuard.getInstance().getPlatform().getRegionContainer().get(new BukkitWorld(w));
        if(man == null) return null;

        ApplicableRegionSet set = man.getApplicableRegions(BlockVector3.at(location.getX(), location.getY(), location.getZ()));
        Set<ProtectedRegion> regions = set.getRegions();

        if(regions.isEmpty()) return null;
        ProtectedRegion region = regions.stream().findFirst().orElse(null);
        return region.getId();
    }
}

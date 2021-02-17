package de.codingair.warpsystem.spigot.api.worldguard;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.stream.Stream;

public class WorldGuardAdapter_12 extends WorldGuardAdapter {
    public WorldGuardAdapter_12() throws ClassNotFoundException {
        test();
    }

    protected void test() throws ClassNotFoundException {
        Class.forName("com.sk89q.worldguard.bukkit.WGBukkit");
    }

    public @Nullable Stream<String> getRegion(@NotNull Location location) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        World w = location.getWorld();
        if (w == null) throw new IllegalArgumentException("The location '" + location.toString() + "' does not provide a world!");

        RegionManager man = WGBukkit.getRegionManager(w);
        return getRegions(location, man);
    }

    @Override
    protected @NotNull ApplicableRegionSet getProtectedRegions(@NotNull Location location, RegionManager man) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        //noinspection JavaReflectionMemberAccess
        return (ApplicableRegionSet) man.getClass().getMethod("getApplicableRegions", Vector.class).invoke(man, Vector.toBlockPoint(location.getX(), location.getY(), location.getZ()));
    }
}

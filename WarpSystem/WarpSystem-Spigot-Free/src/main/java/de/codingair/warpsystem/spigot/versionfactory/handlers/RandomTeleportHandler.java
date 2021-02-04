package de.codingair.warpsystem.spigot.versionfactory.handlers;

import de.codingair.codingapi.tools.Callback;
import de.codingair.codingapi.tools.Location;
import de.codingair.warpsystem.spigot.base.utils.teleport.TeleportUtils;
import de.codingair.warpsystem.spigot.features.randomteleports.managers.RandomTeleportManager;
import de.codingair.warpsystem.spigot.features.randomteleports.utils.RandomLocationCalculator;
import org.bukkit.entity.Player;

public class RandomTeleportHandler extends RandomTeleportManager {

    @Override
    public RandomLocationCalculator newCalculator(Player player, org.bukkit.Location location, double minRange, double maxRange, Callback<RandomLocationCalculator> callback) {
        return new Calculator(player, location, minRange, maxRange, callback);
    }

    public static class Calculator extends RandomLocationCalculator {
        public Calculator(Player player, org.bukkit.Location location, double minRange, double maxRange, Callback<RandomLocationCalculator> callback) {
            super(player, location, minRange, maxRange, callback);
        }

        public boolean correct(Location location, boolean safety) {
            if (RandomTeleportManager.getInstance().getBiomeList() != null && !RandomTeleportManager.getInstance().getBiomeList().contains(location.getWorld().getBiome(location.getBlockX(), location.getBlockZ())))
                return false;
            if (RandomTeleportManager.getInstance().isProtectedRegions() && isProtected(location).join()) return false;
            if (safety) {
                Location above = location.clone();
                above.setY(above.getY() + 1);
                Location below = location.clone();
                below.setY(below.getY() - 1);


                return isEnoughSpace(location) && TeleportUtils.isSafe(above.getBlock()) && TeleportUtils.isSafe(location.getBlock()) && TeleportUtils.isSafe(below.getBlock());
            } else return true;
        }
    }
}

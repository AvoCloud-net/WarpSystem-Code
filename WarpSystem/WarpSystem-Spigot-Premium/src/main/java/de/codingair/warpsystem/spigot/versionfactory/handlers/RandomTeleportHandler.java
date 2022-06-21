package de.codingair.warpsystem.spigot.versionfactory.handlers;

import de.codingair.codingapi.files.ConfigFile;
import de.codingair.codingapi.files.loader.UTFConfig;
import de.codingair.codingapi.tools.Area;
import de.codingair.codingapi.tools.Callback;
import de.codingair.codingapi.tools.Location;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.teleport.TeleportUtils;
import de.codingair.warpsystem.spigot.features.randomteleports.managers.RandomTeleportManager;
import de.codingair.warpsystem.spigot.features.randomteleports.utils.RandomLocationCalculator;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;

public class RandomTeleportHandler extends RandomTeleportManager {
    protected boolean worldBorder;

    @Override
    public boolean load(boolean hide) {
        ConfigFile rtpFile = WarpSystem.getInstance().getFileManager().loadFile("RTPConfig", "/");
        UTFConfig config = rtpFile.getConfig();

        this.worldBorder = config.getBoolean("RandomTeleport.Support.WorldBorder", true);
        return super.load(hide);
    }

    @Override
    public RandomLocationCalculator newCalculator(Player player, org.bukkit.Location location, double minRange, double maxRange, Callback<RandomLocationCalculator> callback) {
        return new Calculator(player, location, minRange, maxRange, callback);
    }

    public boolean isWorldBorder() {
        return worldBorder;
    }

    public static class Calculator extends RandomLocationCalculator {
        public Calculator(Player player, org.bukkit.Location location, double minRange, double maxRange, Callback<RandomLocationCalculator> callback) {
            super(player, location, minRange, maxRange, callback);
        }

        public boolean correct(Location location, boolean safety) {
            if (RandomTeleportManager.getInstance().getBiomeBlacklist() != null && RandomTeleportManager.getInstance().getBiomeBlacklist().contains(location.getBlock().getBiome()))
                return false;
            if (RandomTeleportManager.getInstance().isProtectedRegions() && isProtected(location).join()) return false;
            if (((RandomTeleportHandler) RandomTeleportManager.getInstance()).isWorldBorder() && !isInsideOfWorldBorder(location)) return false;
            if (safety) {
                Location above = location.clone();
                above.setY(above.getY() + 1);
                Location below = location.clone();
                below.setY(below.getY() - 1);

                return isEnoughSpace(location) && TeleportUtils.isSafe(above.getBlock()) && TeleportUtils.isSafe(location.getBlock()) && TeleportUtils.isSafe(below.getBlock());
            } else return true;
        }

        private boolean isInsideOfWorldBorder(Location location) {
            assert location.getWorld() != null;
            WorldBorder border = location.getWorld().getWorldBorder();
            return Area.isInArea(location, border.getCenter(), border.getSize() / 2, false, 0);
        }
    }
}

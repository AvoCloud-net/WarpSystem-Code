package de.codingair.warpsystem.spigot.base.utils.teleport;

import de.codingair.codingapi.server.Environment;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class TeleportUtils {
    private TeleportUtils() {
    }

    public static <A extends Location> A prepareLocation(@Nullable A l, Player player) {
        if (l == null || player.isFlying()) return l;

        Location clone = l.clone();

        Material m = clone.getBlock().getType();
        while (!m.isSolid() && !Environment.isWaterFluid(clone.getBlock()) && clone.getY() >= 0) {
            clone.setY(clone.getY() - 1);
            m = clone.getBlock().getType();
        }

        if (clone.getY() < 0) {
            enableFlightIfPossible(player, 2); //> 1 -> trigger
            return l;
        }

        do {
            clone.setY(clone.getY() + 1);
            m = clone.getBlock().getType();
        } while ((m.isSolid() || Environment.isWaterFluid(clone.getBlock())) && clone.getY() < 200);

        if (enableFlightIfPossible(player, l.getY() - clone.getY())) return l;

        l.setY(clone.getY());
        return l;
    }

    private static boolean enableFlightIfPossible(Player player, double diff) {
        if(player.getAllowFlight()) {
            if(diff > 1D) {
                player.setFlying(true);
                return true;
            }
        }

        return false;
    }
}

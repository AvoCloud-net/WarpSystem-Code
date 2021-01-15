package de.codingair.warpsystem.spigot.base.utils.teleport;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class TeleportUtils {
    private TeleportUtils() {
    }

    public static <A extends Location> A prepareLocation(@Nullable A l, Player player) {
        if(l == null || player.isFlying()) return l;
        if(player.getAllowFlight()) {
            player.setFlying(true);
            return l;
        }

        Location clone = l.clone();

        Material m = clone.getBlock().getType();
        while(!m.isSolid() && clone.getY() >= 0) {
            clone.setY(clone.getY() - 1);
            m = clone.getBlock().getType();
        }

        if(clone.getY() < 0) return l;

        do {
            clone.setY(clone.getY() + 1);
            m = clone.getBlock().getType();
        } while(m.isSolid() && clone.getY() < 200);

        l.setY(clone.getY());
        return l;
    }
}

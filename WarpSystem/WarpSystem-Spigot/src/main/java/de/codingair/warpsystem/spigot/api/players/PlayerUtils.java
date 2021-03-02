package de.codingair.warpsystem.spigot.api.players;

import org.bukkit.entity.Player;

public class PlayerUtils {
    private static final double GRAVITY = -0.0784000015258789;

    public static boolean isOnGround(Player player) {
        double currentVelocity = player.getVelocity().getY();
        return Math.abs(currentVelocity - GRAVITY) < 0.005;
    }

}

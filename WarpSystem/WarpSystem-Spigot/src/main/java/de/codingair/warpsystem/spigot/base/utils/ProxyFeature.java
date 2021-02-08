package de.codingair.warpsystem.spigot.base.utils;

import org.bukkit.entity.Player;

public interface ProxyFeature {
    /**
     * Used for every hook up.
     * - A player joins on an empty server
     * - The plugin will be reloaded
     */
    default void onInitiate(Player connection) {

    }

    default void onConnect(Player connection) {

    }

    default void onDisconnect() {

    }
}

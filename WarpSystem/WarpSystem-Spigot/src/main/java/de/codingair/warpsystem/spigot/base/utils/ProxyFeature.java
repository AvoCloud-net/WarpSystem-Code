package de.codingair.warpsystem.spigot.base.utils;

import org.bukkit.entity.Player;

public interface ProxyFeature {
    void onConnect(Player connection);

    void onDisconnect();

    default void onInitiate(Player connection) {

    }
}

package de.codingair.warpsystem.spigot.api.players;

import org.bukkit.entity.Player;

public abstract class PermissionPlayer implements Player{
    Player player;

    public PermissionPlayer(Player player) {
        this.player = player;
    }
}

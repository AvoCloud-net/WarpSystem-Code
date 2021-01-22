package de.codingair.warpsystem.spigot.api.players;

import de.codingair.warpsystem.core.transfer.packets.spigot.MessagePacket;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ProxyPlayer {
    private final Player player;
    private final String name;
    private final String displayName;

    public ProxyPlayer(Player player) {
        this.player = player;
        this.name = player.getName();
        this.displayName = player.getDisplayName();
    }

    public ProxyPlayer(String name, String displayName) {
        this.player = Bukkit.getPlayer(name);
        this.name = name;
        this.displayName = displayName;
    }

    public ProxyPlayer(String name) {
        this(name, name);
    }

    public void sendMessage(String msg) {
        if (player == null) WarpSystem.getDataHandler().send(new MessagePacket(name, msg), null);
        else player.sendMessage(msg);
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean onSpigot() {
        return player != null;
    }

    public Player getSpigotPlayer() {
        return player;
    }
}

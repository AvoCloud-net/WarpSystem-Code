package de.codingair.warpsystem.spigot.api.events;

import de.codingair.warpsystem.core.transfer.utils.PlayerData;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerDataUpdateEvent extends PlayerEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final PlayerData data;

    public PlayerDataUpdateEvent(@NotNull Player who, PlayerData data) {
        super(who);
        this.data = data;
    }

    public PlayerData getData() {
        return data;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}

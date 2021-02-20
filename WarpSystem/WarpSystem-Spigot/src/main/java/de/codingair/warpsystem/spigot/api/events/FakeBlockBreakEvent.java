package de.codingair.warpsystem.spigot.api.events;

import de.codingair.codingapi.server.specification.Version;
import de.codingair.warpsystem.spigot.api.players.PermissionPlayer_v1_8;
import de.codingair.warpsystem.spigot.api.players.PermissionPlayer_v1_9;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.jetbrains.annotations.NotNull;

public class FakeBlockBreakEvent extends BlockBreakEvent {
    public FakeBlockBreakEvent(@NotNull Block theBlock, @NotNull Player player) {
        super(theBlock, player);
    }

    /**
     * The position of the player will be checked for any protection.
     *
     * @param player   The player we use to create our fake player to simulate the BlockBreakEvent. We use fake players to prevent player specific particles from plugins like WorldGuard
     * @return true if the location is protected (the event is cancelled).
     */
    public static boolean tryFake(Player player) {
        return tryFake(player, player.getLocation());
    }

    /**
     * @param player   The player we use to create our fake player to simulate the BlockBreakEvent. We use fake players to prevent player specific particles from plugins like WorldGuard
     * @param location The Location where we try to build.
     * @return true if the location is protected (the event is cancelled).
     */
    public static boolean tryFake(Player player, Location location) {
        return tryWithFake(buildFake(player), location);
    }

    /**
     * @param fakePlayer The player who simulate the BlockBreakEvent. Use fake players to prevent player specific particles from plugins like WorldGuard
     * @param location   The Location where we try to build.
     * @return true if the location is protected (the event is cancelled).
     */
    public static boolean tryWithFake(Player fakePlayer, Location location) {
        FakeBlockBreakEvent event = new FakeBlockBreakEvent(location.getBlock(), fakePlayer);
        Bukkit.getPluginManager().callEvent(event);
        return event.isCancelled();
    }

    public static Player buildFake(Player player) {
        if (Version.get().isBiggerThan(8)) return new PermissionPlayer_v1_9(player);
        else return new PermissionPlayer_v1_8(player);
    }
}

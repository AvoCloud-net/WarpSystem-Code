package de.codingair.warpsystem.spigot.base.utils.teleport.destinations;

import de.codingair.codingapi.tools.Callback;
import de.codingair.codingapi.tools.Location;
import de.codingair.warpsystem.api.Result;
import de.codingair.warpsystem.spigot.base.utils.teleport.SimulatedTeleportResult;
import de.codingair.warpsystem.spigot.base.utils.teleport.TeleportUtils;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.concurrent.CompletableFuture;

public abstract class DestinationAdapter {
    Destination destination;

    public abstract CompletableFuture<Boolean> teleport(Player player, String id, Vector randomOffset, String displayName, boolean checkPermission, String message, boolean silent, double costs, Callback<Result> callback);

    public abstract SimulatedTeleportResult simulate(Player player, String id, boolean checkPermission);

    public abstract double getCosts(String id);

    public abstract Location buildLocation(String id);

    public DestinationAdapter dest(Destination d) {
        destination = d;
        return this;
    }

    public CompletableFuture<org.bukkit.Location> prepare(Player player, org.bukkit.Location location) {
        if (location == null) return CompletableFuture.completedFuture(null);
        if (destination != null) {
            destination.adjustLocation(player, location);
            if (destination.getCustomOptions().isSafeTP()) return TeleportUtils.prepareLocation(location, player);
        }

        return CompletableFuture.completedFuture(location);
    }

    public abstract boolean usesBukkitTeleportation();
}

package de.codingair.warpsystem.spigot.base.utils.teleport.destinations.adapters;

import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.api.Result;
import de.codingair.warpsystem.spigot.base.utils.Lang;
import de.codingair.warpsystem.spigot.base.utils.teleport.SimulatedTeleportResult;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.DestinationAdapter;
import de.codingair.warpsystem.spigot.features.simplewarps.SimpleWarp;
import de.codingair.warpsystem.spigot.features.simplewarps.managers.SimpleWarpManager;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.concurrent.CompletableFuture;

public class SimpleWarpAdapter extends DestinationAdapter {
    @Override
    public CompletableFuture<Boolean> teleport(Player player, String id, Vector randomOffset, String displayName, boolean checkPermission, String message, boolean silent, double costs, Callback<Result> callback) {
        SimpleWarp warp = SimpleWarpManager.getInstance().getWarp(id);

        if (warp == null) {
            player.sendMessage(Lang.getPrefix() + Lang.get("WARP_DOES_NOT_EXISTS"));
            if (callback != null) callback.accept(Result.DESTINATION_DOES_NOT_EXIST);
            return CompletableFuture.completedFuture(false);
        }

        if (warp.getLocation().getWorld() == null) {
            player.sendMessage(Lang.getPrefix() + Lang.get("World_Not_Exists"));
            if (callback != null) callback.accept(Result.WORLD_DOES_NOT_EXIST);
            return CompletableFuture.completedFuture(false);
        } else {
            if (checkPermission && warp.hasPermission() && !player.hasPermission(warp.getPermission())) {
                player.sendMessage(Lang.getPrefix() + Lang.get("Player_Cannot_Use_Warp"));
                if (callback != null) callback.accept(Result.NO_PERMISSION);
                return CompletableFuture.completedFuture(false);
            }

            CompletableFuture<Boolean> future = new CompletableFuture<>();
            prepare(player, warp.getLocation().clone()).whenComplete(LocationAdapter.handleLocation(player, silent, callback, future));
            return future;
        }
    }

    @Override
    public SimulatedTeleportResult simulate(Player player, String id, boolean checkPermission) {
        SimpleWarp warp = SimpleWarpManager.getInstance().getWarp(id);

        if (warp == null) {
            return new SimulatedTeleportResult(Lang.getPrefix() + Lang.get("WARP_DOES_NOT_EXISTS"), Result.DESTINATION_DOES_NOT_EXIST);
        }

        if (warp.getLocation().getWorld() == null) {
            return new SimulatedTeleportResult(Lang.getPrefix() + Lang.get("World_Not_Exists"), Result.WORLD_DOES_NOT_EXIST);
        } else {
            if (checkPermission && warp.hasPermission() && !player.hasPermission(warp.getPermission())) {
                return new SimulatedTeleportResult(Lang.getPrefix() + Lang.get("Player_Cannot_Use_Warp"), Result.NO_PERMISSION);
            }

            return new SimulatedTeleportResult(null, Result.SUCCESS);
        }
    }

    @Override
    public double getCosts(String id) {
        SimpleWarp warp = SimpleWarpManager.getInstance().getWarp(id);
        if (warp == null) return 0;
        else return warp.getCosts();
    }

    @Override
    public de.codingair.codingapi.tools.Location buildLocation(String id) {
        SimpleWarp warp = SimpleWarpManager.getInstance().getWarp(id);
        return warp == null ? null : warp.getLocation().clone();
    }
}

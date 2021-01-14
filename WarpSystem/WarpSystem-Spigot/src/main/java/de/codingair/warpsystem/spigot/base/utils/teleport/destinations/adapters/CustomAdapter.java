package de.codingair.warpsystem.spigot.base.utils.teleport.destinations.adapters;

import de.codingair.codingapi.tools.Callback;
import de.codingair.codingapi.tools.Location;
import de.codingair.warpsystem.api.IDestination;
import de.codingair.warpsystem.api.Result;
import de.codingair.warpsystem.spigot.base.utils.teleport.SimulatedTeleportResult;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.DestinationAdapter;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class CustomAdapter extends DestinationAdapter {
    private final IDestination destination;

    public CustomAdapter(IDestination destination) {
        this.destination = destination;
    }

    @Override
    public boolean teleport(Player player, String id, Vector randomOffset, String displayName, boolean checkPermission, String message, boolean silent, double costs, Callback<Result> callback) {
        this.destination.teleport(player, id, randomOffset, displayName, checkPermission, message, costs).whenComplete((suc, err) -> {
            if (callback == null) return;

            if (suc == null) {
                if (err != null) {
                    err.printStackTrace();
                    callback.accept(Result.ERROR);
                } else throw new IllegalStateException("Completed a teleport with nothing via the API!");
            } else callback.accept(suc);
        });
        return true;
    }

    @Override
    public SimulatedTeleportResult simulate(Player player, String id, boolean checkPermission) {
        return new SimulatedTeleportResult(null, Result.SUCCESS);
    }

    @Override
    public double getCosts(String id) {
        return 0;
    }

    @Override
    public Location buildLocation(String id) {
        return null;
    }
}

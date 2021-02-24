package de.codingair.warpsystem.spigot.base.utils.teleport.destinations.adapters;

import de.codingair.codingapi.tools.Callback;
import de.codingair.codingapi.tools.Location;
import de.codingair.codingapi.tools.io.utils.DataMask;
import de.codingair.codingapi.tools.io.utils.Serializable;
import de.codingair.warpsystem.api.Result;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.Usable;
import de.codingair.warpsystem.spigot.base.utils.teleport.SimulatedTeleportResult;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class VelocityAdapter extends CloneableAdapter implements Serializable, Usable {
    private Vector vector;
    private Double multiplier;

    public VelocityAdapter() {
    }

    public VelocityAdapter(@Nullable Vector vector, @NotNull Double multiplier) {
        this.vector = vector;
        this.multiplier = multiplier;
    }

    @Override
    public CompletableFuture<Boolean> teleport(Player player, String id, Vector randomOffset, String displayName, boolean checkPermission, String message, boolean silent, double costs, Callback<Result> callback) {
        if (vector == null) return CompletableFuture.completedFuture(false);
        player.setVelocity(vector);
        return CompletableFuture.completedFuture(true);
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

    @Override
    public boolean read(DataMask d) throws Exception {
        double x = d.getDouble("velocity.x");
        double y = d.getDouble("velocity.y");
        double z = d.getDouble("velocity.z");
        if (x != 0 || y != 0 || z != 0) this.vector = new Vector(x, y, z);

        double multiplier = d.getDouble("velocity.multiplier");
        if (multiplier != 0) this.multiplier = multiplier;

        return true;
    }

    @Override
    public void write(DataMask d) {
        if (this.vector != null) {
            d.put("velocity.x", this.vector.getX());
            d.put("velocity.y", this.vector.getY());
            d.put("velocity.z", this.vector.getZ());
        }

        if (this.multiplier != null) d.put("velocity.multiplier", this.multiplier);
    }

    @Override
    public boolean usable() {
        return vector != null;
    }

    @Override
    public VelocityAdapter clone() {
        return new VelocityAdapter(vector, multiplier);
    }
}

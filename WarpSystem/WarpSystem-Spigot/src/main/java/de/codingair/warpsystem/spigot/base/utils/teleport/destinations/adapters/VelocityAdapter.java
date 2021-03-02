package de.codingair.warpsystem.spigot.base.utils.teleport.destinations.adapters;

import de.codingair.codingapi.tools.Callback;
import de.codingair.codingapi.tools.Location;
import de.codingair.codingapi.tools.io.utils.DataMask;
import de.codingair.codingapi.tools.io.utils.Serializable;
import de.codingair.warpsystem.api.Result;
import de.codingair.warpsystem.spigot.api.players.PlayerUtils;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.Usable;
import de.codingair.warpsystem.spigot.base.utils.teleport.SimulatedTeleportResult;
import de.codingair.warpsystem.spigot.versionfactory.VFac;
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
        if (vector == null || multiplier == null) return CompletableFuture.completedFuture(false);
        if (callback != null) callback.accept(Result.SUCCESS);

        Vector vector = getVector(player);
        player.setVelocity(vector);

        return CompletableFuture.completedFuture(true);
    }

    private Vector getVector(Player player) {
        Vector vector = getVector();
        vector.multiply(this.multiplier);

        if (!PlayerUtils.isOnGround(player)) vector.multiply(0.68); //decrease velocity when player is not on ground since the velocity is optimized for players on ground
        //vector.add(player.getVelocity()); //add to allow velocity chains
        //Ignore for now. Does not seem to be good for falling players... Maybe as extra option?

        return vector;
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
        if (VFac.isAvailable("Indicator")) return true;

        double x = d.getDouble("velocity.x");
        double y = d.getDouble("velocity.y");
        double z = d.getDouble("velocity.z");
        if (x != 0 || y != 0 || z != 0) this.vector = new Vector(x, y, z);

        this.multiplier = d.getDouble("velocity.multiplier");

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
        return vector != null || this.multiplier != 0;
    }

    @Override
    public VelocityAdapter clone() {
        return new VelocityAdapter(vector, multiplier);
    }

    public Vector getVector() {
        return vector == null ? null : vector.clone();
    }

    public void setVector(Vector vector) {
        this.vector = vector;
    }

    public Double getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(Double multiplier) {
        this.multiplier = multiplier;
        if (this.multiplier != null) {
            this.multiplier = Math.round(this.multiplier * 100D) / 100D;

            if (this.multiplier < 0.1) this.multiplier = 0.1;
            if (this.multiplier > 10) this.multiplier = 10D;
        }
    }

    @Override
    public boolean usesBukkitTeleportation() {
        return false;
    }
}

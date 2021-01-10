package de.codingair.warpsystem.spigot.features.randomteleports.utils;

import de.codingair.codingapi.server.Environment;
import de.codingair.codingapi.server.specification.Version;
import de.codingair.codingapi.tools.Area;
import de.codingair.codingapi.tools.Callback;
import de.codingair.codingapi.tools.Location;
import de.codingair.codingapi.utils.Node;
import de.codingair.codingapi.utils.Value;
import de.codingair.warpsystem.spigot.api.players.PermissionPlayer_v1_8;
import de.codingair.warpsystem.spigot.api.players.PermissionPlayer_v1_9;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.features.randomteleports.managers.RandomTeleportManager;
import io.papermc.lib.PaperLib;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class RandomLocationCalculator implements Runnable {
    private final org.bukkit.Location startLocation;
    private final Player check;
    private final Callback<Location> callback;
    private long lastReaction = 0;
    private final double minRange;
    private final double maxRange;
    private final double diffRange;

    public RandomLocationCalculator(Player player, org.bukkit.Location location, double minRange, double maxRange, Callback<Location> callback) {
        if(Version.get().isBiggerThan(8)) check = new PermissionPlayer_v1_9(player);
        else check = new PermissionPlayer_v1_8(player);

        this.callback = callback;
        this.startLocation = location;
        this.minRange = minRange;
        this.maxRange = maxRange;
        this.diffRange = maxRange - minRange;
    }

    @Override
    public void run() {
        Location location = null;
        try {
            location = calculate();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
        if(location != null) {
            location.setX(location.getBlockX() + 0.5);
            location.setY(location.getBlockY() + 0.5);
            location.setZ(location.getBlockZ() + 0.5);
        }
        callback.accept(location);
    }

    private Location calculate() throws InterruptedException {
        long start = System.currentTimeMillis();
        Location location = new Location(startLocation);

        double x = startLocation.getX();
        double z = startLocation.getZ();

        Random r = new Random();

        long maxTime = (long) ((maxRange - minRange) / 2);
        if(maxTime < 1000) maxTime = 1000;
        if(maxTime > 5000) maxTime = 5000;

        do {
            location.setY(startLocation.getY());
            lastReaction = System.currentTimeMillis();

            Node<Double, Double> offset = getRandomOffset(r);
            location.setX(x + offset.getKey());
            location.setZ(z + offset.getValue());

            PaperLib.getChunkAtAsync(location).join();
            if(start + maxTime < System.currentTimeMillis()) {
                return null;
            }

            if(correct(location, false)) location.setY(calculateYCoord(location));
        } while(!checkY(location) || blockedMaterial(location) || !correct(location, true));
        return location;
    }

    private Node<Double, Double> getRandomOffset(Random r) {
        double degree = r.nextDouble() * 2 * Math.PI - Math.PI;

        double x = -Math.sin(degree) * ((r.nextDouble() * diffRange) + minRange);
        double z = Math.cos(degree) * ((r.nextDouble() * diffRange) + minRange);

        return new Node<>(x, z);
    }

    private boolean blockedMaterial(Location location) {
        Location below = location.clone();
        below.setY(below.getY() - 1);

        List<Material> l = RandomTeleportManager.getInstance().getMaterialBlackList();
        return l.contains(below.getBlock().getType());
    }

    protected boolean isSafeLocation(Location location) {
        return Environment.canBeEntered(location.getBlock().getType())
                && Environment.canBeEntered(location.clone().add(0, 1, 0).getBlock().getType())
                && !Environment.canBeEntered(location.clone().subtract(0, 1, 0).getBlock().getType());
    }

    private boolean checkY(Location location) {
        return location.getY() <= getHighestY(location.getWorld()) && location.getY() > 0;
    }

    private int getHighestY(World w) {
        switch(w.getEnvironment()) {
            case NETHER:
                return RandomTeleportManager.getInstance().getNetherHeight();
            case THE_END:
                return RandomTeleportManager.getInstance().getEndHeight();
            default:
                return 72;
        }
    }

    private int calculateYCoord(Location location) {
        Location loc = location.clone();
        if(location.getWorld().getEnvironment() != World.Environment.NORMAL) loc.setY(getHighestY(loc.getWorld()));

        if(location.getWorld().getEnvironment() == World.Environment.NETHER) {
            int free = 0;
            while(free < 2 && loc.getY() >= 0) {
                if(Environment.canBeEntered(loc.getBlock().getType())) free++;

                loc.setY(loc.getY() - 1);
            }

            while(Environment.canBeEntered(loc.getBlock().getType()) && loc.getBlockY() > 0) {
                loc.setY(loc.getY() - 1);
            }

            loc.setY(loc.getY() + 1);
        } else {
            if(!Environment.canBeEntered(loc.getBlock().getType())) {
                while(!Environment.canBeEntered(loc.getBlock().getType())) {
                    loc.setY(loc.getY() + 4);
                }

                while(Environment.canBeEntered(loc.getBlock().getType())) {
                    loc.setY(loc.getY() - 1);
                }

                loc.setY(loc.getY() + 1);
            } else {
                while(Environment.canBeEntered(loc.getBlock().getType()) && loc.getBlockY() > 0) {
                    loc.setY(loc.getY() - 4);
                }

                if(loc.getBlockY() > 0) {
                    while(!Environment.canBeEntered(loc.getBlock().getType())) {
                        loc.setY(loc.getY() + 1);
                    }
                }
            }
        }

        return loc.getBlockY();
    }

    public abstract boolean correct(Location location, boolean safety) throws InterruptedException;

    protected boolean isSafe(Location location) {
        Block b = location.getBlock();

        List<String> unsafe = new ArrayList<>();

        unsafe.add("VOID");
        unsafe.add("LAVA");
        unsafe.add("FIRE");
        unsafe.add("MAGMA");

        for(String s : unsafe) {
            if(b.getType().name().toUpperCase().contains(s)) return false;
        }

        return true;
    }

    protected boolean isProtected(Location location) throws InterruptedException {
        synchronized(this) {
            Value<BlockBreakEvent> eventValue = new Value<>(null);
            Bukkit.getScheduler().runTask(WarpSystem.getInstance(), () -> {
                BlockBreakEvent event = new BlockBreakEvent(location.getBlock(), this.check); //check is a bukkit/Player instance
                eventValue.setValue(event);
                Bukkit.getPluginManager().callEvent(event);

                synchronized(RandomLocationCalculator.this) {
                    RandomLocationCalculator.this.notify();
                }
            });

            this.wait();
            return eventValue.getValue().isCancelled();
        }
    }

    public long getLastReaction() {
        return lastReaction;
    }
}

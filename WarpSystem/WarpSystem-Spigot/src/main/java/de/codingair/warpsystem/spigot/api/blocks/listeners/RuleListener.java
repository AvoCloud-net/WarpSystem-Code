package de.codingair.warpsystem.spigot.api.blocks.listeners;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.codingair.warpsystem.spigot.api.blocks.utils.Position;
import de.codingair.warpsystem.spigot.api.blocks.utils.StaticBlock;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class RuleListener implements Listener {
    public static HashMap<Position, StaticBlock> BLOCKS = new HashMap<>();
    private static final Cache<UUID, Boolean> NO_DAMAGE = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.SECONDS).build();

    public static void noDamageTo(Entity entity) {
        NO_DAMAGE.put(entity.getUniqueId(), true);
    }

    @EventHandler
    public void onChange(BlockPhysicsEvent e) {
        if (e.getBlock() == null) return;
        if (BLOCKS.containsKey(new Position(e.getBlock().getLocation()))) e.setCancelled(true);
    }

    @EventHandler
    public void onFlow(BlockFromToEvent e) {
        if (e.getBlock() == null) return;
        if (BLOCKS.containsKey(new Position(e.getBlock().getLocation()))) e.setCancelled(true);
    }

    @EventHandler
    public void onBurn(BlockIgniteEvent e) {
        if (e.getIgnitingBlock() == null) return;
        if (BLOCKS.containsKey(new Position(e.getIgnitingBlock().getLocation()))) e.setCancelled(true);
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onHit(EntityCombustEvent e) {
        if (NO_DAMAGE.getIfPresent(e.getEntity().getUniqueId()) != null) {
            e.setCancelled(true);
            e.setDuration(0);
            return;
        }

        if (!touchesKnownStaticBlocks(e.getEntity())) return;

        e.setDuration(0);
        e.getEntity().setFireTicks(-200);
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onHit(EntityDamageEvent e) {
        if (NO_DAMAGE.getIfPresent(e.getEntity().getUniqueId()) != null) {
            e.setCancelled(true);
            return;
        }

        if ((e instanceof EntityDamageByBlockEvent && e.getCause().equals(EntityDamageByBlockEvent.DamageCause.LAVA)) || e.getCause() == EntityDamageEvent.DamageCause.FIRE || e.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK || e.getCause() == EntityDamageEvent.DamageCause.DROWNING) {
            if (!touchesKnownStaticBlocks(e.getEntity())) return;

            e.setCancelled(true);
            e.setDamage(0);
            e.getEntity().setFireTicks(-200);
        }
    }

    private void addTo(List<Location> list, Location origin, Location toAdd) {
        if (!origin.getBlock().equals(toAdd.getBlock())) list.add(toAdd);
    }

    private List<Location> getBlocksAround(Entity e) {
        List<Location> locs = new ArrayList<>();
        double diff = 0.5;

        Location origin;

        for (double i = -diff; i <= diff; i += diff) {
            locs.add(origin = e.getLocation().add(0, i + 0.1, 0));
            addTo(locs, origin, origin.clone().add(new Vector(diff, 0, 0)));
            addTo(locs, origin, origin.clone().add(new Vector(-diff, 0, 0)));
            addTo(locs, origin, origin.clone().add(new Vector(0, 0, diff)));
            addTo(locs, origin, origin.clone().add(new Vector(0, 0, -diff)));
            addTo(locs, origin, origin.clone().add(new Vector(diff, 0, diff)));
            addTo(locs, origin, origin.clone().add(new Vector(-diff, 0, diff)));
            addTo(locs, origin, origin.clone().add(new Vector(diff, 0, -diff)));
            addTo(locs, origin, origin.clone().add(new Vector(-diff, 0, -diff)));
        }

        return locs;
    }

    private boolean touchesKnownStaticBlocks(Entity e) {
        List<Location> locs = getBlocksAround(e);

        if (locs.isEmpty()) return false;

        boolean result = false;

        for (Location l : locs) {
            if (BLOCKS.containsKey(new Position(l))) {
                result = true;
                break;
            }
        }
        locs.clear();

        return result;
    }
}

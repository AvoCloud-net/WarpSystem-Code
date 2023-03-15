package de.codingair.warpsystem.spigot.features.randomteleports.listeners;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.codingair.codingapi.tools.Location;
import de.codingair.warpsystem.spigot.base.utils.Lang;
import de.codingair.warpsystem.spigot.base.utils.Permissions;
import de.codingair.warpsystem.spigot.features.randomteleports.managers.RandomTeleportManager;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class InteractListener implements Listener {
    private final Cache<UUID, Boolean> blocked = CacheBuilder.newBuilder().expireAfterWrite(1, TimeUnit.SECONDS).build();
    private final Cache<UUID, Boolean> addingNewBlock = CacheBuilder.newBuilder().expireAfterWrite(30, TimeUnit.SECONDS).build();

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK || blocked.getIfPresent(e.getPlayer().getUniqueId()) != null) return;
        Block b = e.getClickedBlock();
        if (b == null) return;
        org.bukkit.Location loc = b.getLocation();

        if (addingNewBlock.getIfPresent(e.getPlayer().getUniqueId()) != null) {
            RandomTeleportManager.getInstance().getInteractBlocks().add(new Location(loc));
            e.getPlayer().sendMessage(Lang.getPrefix() + Lang.get("RandomTP_Block_Added"));
            addingNewBlock.invalidate(e.getPlayer().getUniqueId());
            blocked.put(e.getPlayer().getUniqueId(), true);
            return;
        }

        for (Location l : RandomTeleportManager.getInstance().getInteractBlocks()) {
            if (l.equals(loc)) {
                blocked.put(e.getPlayer().getUniqueId(), true);
                RandomTeleportManager.getInstance().tryToTeleport(e.getPlayer());
                break;
            }
        }
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onBreak(BlockBreakEvent e) {
        Block b = e.getBlock();
        org.bukkit.Location loc = b.getLocation();

        Location remove = null;
        for (Location l : RandomTeleportManager.getInstance().getInteractBlocks()) {
            if (l.equals(loc)) {
                remove = l;
                break;
            }
        }

        if (remove != null) {
            if (e.getPlayer().hasPermission(Permissions.PERMISSION_MODIFY_RANDOM_TELEPORTER)) {
                RandomTeleportManager.getInstance().getInteractBlocks().remove(remove);
                e.getPlayer().sendMessage(Lang.getPrefix() + Lang.get("RandomTP_Block_Removed"));
            } else {
                e.getPlayer().sendMessage(Lang.getPrefix() + Lang.get("No_Permission"));
                e.setCancelled(true);
            }
        }
    }

    public void setAsAdding(Player player) {
        this.addingNewBlock.put(player.getUniqueId(), true);
    }
}

package de.codingair.warpsystem.spigot.features.randomteleports.listeners;

import de.codingair.codingapi.tools.Location;
import de.codingair.codingapi.tools.time.TimeSet;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.Lang;
import de.codingair.warpsystem.spigot.features.randomteleports.managers.RandomTeleportManager;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class InteractListener implements Listener {
    private final TimeSet<Player> blocked = new TimeSet<>();
    private final TimeSet<Player> addingNewBlock = new TimeSet<>();

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK || blocked.contains(e.getPlayer())) return;
        Block b = e.getClickedBlock();
        if (b == null) return;
        org.bukkit.Location loc = b.getLocation();

        if (addingNewBlock.contains(e.getPlayer())) {
            RandomTeleportManager.getInstance().getInteractBlocks().add(new Location(loc));
            e.getPlayer().sendMessage(Lang.getPrefix() + Lang.get("RandomTP_Block_Added"));
            addingNewBlock.remove(e.getPlayer());
            blocked.add(e.getPlayer(), 1);
            return;
        }

        for (Location l : RandomTeleportManager.getInstance().getInteractBlocks()) {
            if (l.equals(loc)) {
                blocked.add(e.getPlayer(), 1);
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
            if (e.getPlayer().hasPermission(WarpSystem.PERMISSION_MODIFY_RANDOM_TELEPORTER)) {
                RandomTeleportManager.getInstance().getInteractBlocks().remove(remove);
                e.getPlayer().sendMessage(Lang.getPrefix() + Lang.get("RandomTP_Block_Removed"));
            } else {
                e.getPlayer().sendMessage(Lang.getPrefix() + Lang.get("No_Permission"));
                e.setCancelled(true);
            }
        }
    }

    public TimeSet<Player> getAddingNewBlock() {
        return addingNewBlock;
    }
}

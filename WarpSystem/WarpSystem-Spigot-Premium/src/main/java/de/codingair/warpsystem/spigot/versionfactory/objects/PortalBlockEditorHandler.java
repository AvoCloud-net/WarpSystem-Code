package de.codingair.warpsystem.spigot.versionfactory.objects;

import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.features.portals.guis.subgui.blockeditor.PortalBlockEditor;
import de.codingair.warpsystem.spigot.features.portals.utils.BlockType;
import de.codingair.warpsystem.spigot.features.portals.utils.PortalBlock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PortalBlockEditorHandler {
    public PortalBlockEditorHandler(PortalBlockEditor editor, Block b, Player player) {
        List<Location> locations = new ArrayList<>();
        Location l = b.getLocation();
        locations.add(l);

        if(b.getType().name().toLowerCase().contains("door")) {
            Location other = l.clone().add(0, 1, 0);
            if(!other.getBlock().getType().name().toLowerCase().contains("door")) other = null;

            if(other == null) {
                other = l.clone().subtract(0, 1, 0);
                if(!other.getBlock().getType().name().toLowerCase().contains("door")) other = null;
            }

            if(other != null) locations.add(other.getBlock().getLocation());
        }

        for(Location location : locations) {
            Bukkit.getScheduler().runTaskLater(WarpSystem.getInstance(), () -> {
                if(!editor.getAlignTo().remove(location.getBlock())) {
                    editor.getAlignTo().add(location.getBlock());

                    editor.getPortal().addPortalBlock(new PortalBlock(new de.codingair.codingapi.tools.Location(location), BlockType.CUSTOM));

                    editor.changeToAlignmentBlock(player, location);
                } else {
                    editor.sendBlockChange(player, location.getBlock());
                    editor.getPortal().removePortalBlock(location);
                }
            }, 1);
        }
    }
}

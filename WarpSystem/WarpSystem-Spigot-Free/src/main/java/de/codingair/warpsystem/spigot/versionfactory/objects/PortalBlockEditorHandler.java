package de.codingair.warpsystem.spigot.versionfactory.objects;

import de.codingair.warpsystem.spigot.base.utils.Lang;
import de.codingair.warpsystem.spigot.features.portals.guis.subgui.blockeditor.PortalBlockEditor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class PortalBlockEditorHandler {
    public PortalBlockEditorHandler(PortalBlockEditor editor, Block b, Player player) {
        Lang.PREMIUM_CHAT(player);
    }
}

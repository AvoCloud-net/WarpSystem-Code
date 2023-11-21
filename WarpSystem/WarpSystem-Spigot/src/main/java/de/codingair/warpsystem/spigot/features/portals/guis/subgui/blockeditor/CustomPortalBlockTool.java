package de.codingair.warpsystem.spigot.features.portals.guis.subgui.blockeditor;

import de.codingair.codingapi.player.gui.PlayerItem;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.codingapi.utils.ChatColor;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.Lang;
import de.codingair.warpsystem.spigot.features.portals.utils.BlockType;
import de.codingair.warpsystem.spigot.versionfactory.VFac;
import de.codingair.warpsystem.spigot.versionfactory.VKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;

public class CustomPortalBlockTool extends PlayerItem {
    private final PortalBlockEditor editor;
    private long last = 0;

    public CustomPortalBlockTool(PortalBlockEditor editor, Player player) {
        super(WarpSystem.getInstance(), player, new ItemBuilder(XMaterial.GHAST_TEAR).setName("§7" + ChatColor.stripColor(BlockType.CUSTOM.getName()) + ": §c-" + (VFac.isAvailable("Indicator") ? Lang.PREMIUM_LORE : "")).getItem());
        this.editor = editor;
        setFreezed(true);
    }

    @Override
    public void onInteract(PlayerInteractEvent e) {
        e.setCancelled(true);
        if (System.currentTimeMillis() - last < 50) return;
        else last = System.currentTimeMillis();

        Block b = PortalBlockEditor.getSupportTargetMaterial(getPlayer());
        if (b != null)
            VFac.build(VKey.PortalBlockEditorHandler, editor, b, getPlayer());
    }

    @Override
    public void onHover(PlayerItemHeldEvent e) {
        editor.setAlignBlocks(true);
    }

    @Override
    public void onUnhover(PlayerItemHeldEvent e) {
        editor.setAlignBlocks(false);
    }
}

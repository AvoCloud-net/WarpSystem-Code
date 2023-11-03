package de.codingair.warpsystem.spigot.features.warps.guis;

import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButton;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncButton;
import de.codingair.codingapi.server.sounds.SoundData;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.warpsystem.spigot.features.warps.nextlevel.utils.Icon;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public interface IWarpGUI {
    ItemBuilder getBarrier();

    boolean handleBarrierClick(InventoryClickEvent clickEvent, Player player, ItemButton button, GWarps gui, ItemStack none, int slot);

    void modifyEditingIconBuilder(ItemBuilder iconBuilder, Icon icon);

    boolean onEditingIconClick(InventoryClickEvent e, Player player, SyncButton button, Icon icon, SoundData s, GWarps gui);
}

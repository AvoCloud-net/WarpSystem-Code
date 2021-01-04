package de.codingair.warpsystem.spigot.versionfactory.objects;

import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButton;
import de.codingair.warpsystem.spigot.features.warps.guis.GChooseIconType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class WarpGUIChoosePage extends ItemButton {
    private final GChooseIconType instance;

    public WarpGUIChoosePage(GChooseIconType instance, int slot, ItemStack item) {
        super(slot, item);
        this.instance = instance;
    }

    @Override
    public void onClick(InventoryClickEvent e) {
        instance.setSet(true);
        instance.getPlayer().closeInventory();
        instance.getCallback().accept(true);
    }
}

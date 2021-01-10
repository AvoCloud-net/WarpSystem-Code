package de.codingair.warpsystem.spigot.versionfactory.gui;

import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncButton;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.utils.Lang;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.FeatureObject;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class MessageButton extends SyncButton {
    public MessageButton(int x, int y, FeatureObject object) {
        super(x, y);
    }

    @Override
    public ItemStack craftItem() {
        return new ItemBuilder(XMaterial.BOOK)
                .setName("§6§n" + Lang.get("Message") + Lang.PREMIUM_LORE)
                .addLore("§3" + Lang.get("Current") + ": " + "§c" + Lang.get("Not_Set"))
                .addLore("", "§3" + Lang.get("Leftclick") + ": §a" + Lang.get("Set"))
                .getItem();
    }

    @Override
    public void onClick(InventoryClickEvent inventoryClickEvent, Player player) {
        Lang.PREMIUM_CHAT(player);
    }

    @Override
    public boolean canClick(ClickType click) {
        return click == ClickType.LEFT;
    }
}

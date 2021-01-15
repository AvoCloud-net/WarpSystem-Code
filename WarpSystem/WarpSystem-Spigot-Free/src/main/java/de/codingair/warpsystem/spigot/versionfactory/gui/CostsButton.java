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

public class CostsButton extends SyncButton {
    public CostsButton(int x, int y, FeatureObject object) {
        super(x, y);
    }

    @Override
    public ItemStack craftItem() {
        return new ItemBuilder(XMaterial.GOLD_NUGGET)
                .setName("§6§n" + Lang.get("Costs") + Lang.PREMIUM_LORE)
                .setLore("§3" + Lang.get("Current") + ": " + "§c" + Lang.get("Not_Set"))
                .addLore("", "§3" + Lang.get("Leftclick") + ": §a" + Lang.get("Add"))
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

package de.codingair.warpsystem.spigot.versionfactory.objects;

import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.warps.guis.GWarps;
import de.codingair.warpsystem.spigot.features.warps.nextlevel.utils.Icon;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class GWarpsItemUpdateHandler {
    public GWarpsItemUpdateHandler(Player p, Icon icon, GWarps instance) {
        if(p.getInventory().getItem(p.getInventory().getHeldItemSlot()) == null || p.getInventory().getItem(p.getInventory().getHeldItemSlot()).getType() == Material.AIR
                || icon.getRaw().getType() == p.getInventory().getItem(p.getInventory().getHeldItemSlot()).getType()) {
            p.sendMessage(Lang.getPrefix() + Lang.get("No_Item_In_Hand"));
            return;
        }

        icon.changeItem(p.getInventory().getItem(p.getInventory().getHeldItemSlot()));
        instance.reinitialize();
        GWarps.updateInventory(p);
    }
}

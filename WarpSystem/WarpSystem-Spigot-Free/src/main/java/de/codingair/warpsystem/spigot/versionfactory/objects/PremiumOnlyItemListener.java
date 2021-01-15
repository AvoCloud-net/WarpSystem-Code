package de.codingair.warpsystem.spigot.versionfactory.objects;

import de.codingair.codingapi.player.MessageAPI;
import de.codingair.codingapi.player.gui.hotbar.ClickType;
import de.codingair.codingapi.player.gui.hotbar.HotbarGUI;
import de.codingair.codingapi.player.gui.hotbar.ItemListener;
import de.codingair.codingapi.player.gui.hotbar.components.ItemComponent;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.Lang;
import org.bukkit.entity.Player;

public class PremiumOnlyItemListener implements ItemListener {
    @Override
    public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
        Lang.PREMIUM_CHAT(player);
    }

    @Override
    public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {
        MessageAPI.sendActionBar(player, Lang.PREMIUM_HOTBAR, WarpSystem.getInstance(), Integer.MAX_VALUE);
    }

    @Override
    public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {
        MessageAPI.stopSendingActionBar(player);
    }
}

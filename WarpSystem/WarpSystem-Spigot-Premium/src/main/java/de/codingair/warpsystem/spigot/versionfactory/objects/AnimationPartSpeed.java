package de.codingair.warpsystem.spigot.versionfactory.objects;

import de.codingair.codingapi.player.MessageAPI;
import de.codingair.codingapi.player.gui.hotbar.ClickType;
import de.codingair.codingapi.player.gui.hotbar.HotbarGUI;
import de.codingair.codingapi.player.gui.hotbar.ItemListener;
import de.codingair.codingapi.player.gui.hotbar.components.ItemComponent;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.Lang;
import de.codingair.warpsystem.spigot.features.animations.guis.editor.AnimationPart;
import de.codingair.warpsystem.spigot.features.animations.guis.editor.Menu;
import org.bukkit.entity.Player;

public class AnimationPartSpeed implements ItemListener {
    private final AnimationPart instance;

    public AnimationPartSpeed(AnimationPart instance) {
        this.instance = instance;
    }

    @Override
    public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
        if (clickType == ClickType.LEFT_CLICK) {
            instance.getPart().setSpeed(instance.getPart().getSpeed() - 1);
        } else if (clickType == ClickType.RIGHT_CLICK) {
            instance.getPart().setSpeed(instance.getPart().getSpeed() + 1);
        } else return;

        instance.getMenuGUI().getAnimPlayer().update();
        instance.updateDisplayName(ic, "§7" + Lang.get("Animation_Speed") + ": §e" + instance.getSpeed());
    }

    @Override
    public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {
        MessageAPI.sendActionBar(instance.getPlayer(), Menu.MINUS_PLUS(Lang.get("Animation_Speed")), WarpSystem.getInstance(), Integer.MAX_VALUE);
    }

    @Override
    public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {
        MessageAPI.stopSendingActionBar(instance.getPlayer());
    }
}

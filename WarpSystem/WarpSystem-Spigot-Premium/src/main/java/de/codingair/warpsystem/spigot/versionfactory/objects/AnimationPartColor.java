package de.codingair.warpsystem.spigot.versionfactory.objects;

import de.codingair.codingapi.particles.utils.Color;
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

public class AnimationPartColor implements ItemListener {
    private final AnimationPart instance;

    public AnimationPartColor(AnimationPart instance) {
        this.instance = instance;
    }

    @Override
    public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
        if(!instance.getPart().getParticle().isColorable()) return;

        if(clickType == ClickType.LEFT_CLICK) {
            if(instance.getPart().getColor() == null) instance.getPart().setColor(Color.RED);
            else instance.getPart().setColor(instance.getColor().previous());
        } else if(clickType == ClickType.RIGHT_CLICK) {
            if(instance.getPart().getColor() == null) instance.getPart().setColor(Color.RED);
            else instance.getPart().setColor(instance.getColor().next());
        }

        instance.getMenuGUI().getAnimPlayer().update();
        instance.updateDisplayName(ic, "§7" + Lang.get("Color") + ": §e" + (instance.getColor() == null || !instance.getPart().getParticle().isColorable() ? "§c-" : instance.getColorName()));
    }

    @Override
    public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {
        if(instance.getPart().getParticle().isColorable()) {
            MessageAPI.sendActionBar(instance.getPlayer(), Menu.PREVIOUS_NEXT(Lang.get("Particle_Effect")), WarpSystem.getInstance(), Integer.MAX_VALUE);
        } else {
            MessageAPI.sendActionBar(instance.getPlayer(), "§c" + Lang.get("ParticleType_Doesnt_Support_Colors"), WarpSystem.getInstance(), Integer.MAX_VALUE);
        }
    }

    @Override
    public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {
        MessageAPI.stopSendingActionBar(instance.getPlayer());
    }
}

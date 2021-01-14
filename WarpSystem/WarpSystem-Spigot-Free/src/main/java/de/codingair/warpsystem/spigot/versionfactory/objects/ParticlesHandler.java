package de.codingair.warpsystem.spigot.versionfactory.objects;

import de.codingair.codingapi.particles.Particle;
import de.codingair.codingapi.particles.animations.customanimations.AnimationType;
import de.codingair.codingapi.particles.animations.customanimations.CustomAnimation;
import de.codingair.codingapi.player.MessageAPI;
import de.codingair.codingapi.player.gui.hotbar.ClickType;
import de.codingair.codingapi.player.gui.hotbar.HotbarGUI;
import de.codingair.codingapi.player.gui.hotbar.ItemListener;
import de.codingair.codingapi.player.gui.hotbar.components.ItemComponent;
import de.codingair.codingapi.player.gui.inventory.gui.Skull;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.Lang;
import de.codingair.warpsystem.spigot.features.animations.guis.editor.Menu;
import de.codingair.warpsystem.spigot.features.animations.guis.editor.Particles;
import de.codingair.warpsystem.spigot.features.animations.utils.ParticlePart;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ParticlesHandler {
    private final Particles instance;

    public ParticlesHandler(Particles instance) {
        this.instance = instance;
        initialize();
    }

    public void initialize() {
        instance.setItem(0, new ItemComponent(new ItemBuilder(Skull.ArrowLeft).setName("§7» §c" + Lang.get("Back") + "§7 «").getItem()).setLink(instance.getMenuGUI()), false);
        instance.setItem(1, new ItemComponent(new ItemBuilder(XMaterial.BLACK_STAINED_GLASS_PANE).setHideName(true).getItem()));

        instance.setItem(2, new ItemComponent(new ItemStack(Material.AIR)));
        instance.setItem(3, new ItemComponent(new ItemStack(Material.AIR)));

        instance.setItem(2, new ItemComponent(new ItemBuilder(instance.getParts().size() >= 1 ? XMaterial.NETHER_STAR : XMaterial.BARRIER)
                .setName("§c" + Lang.get("Animation") + " #" + (1))
                .getItem(), new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                if (clickType == ClickType.LEFT_CLICK) {
                    ic.setLink(instance.getAnimations()[0]);

                    if (instance.getMenuGUI().getClone().getParticleParts().size() == 0) {
                        instance.getMenuGUI().getClone().getParticleParts().add(new ParticlePart(AnimationType.CIRCLE, Particle.FLAME, 1, 1, CustomAnimation.MAX_SPEED));
                        instance.getAnimations()[0].initialize();
                        instance.getMenuGUI().getAnimPlayer().update();

                        ic.setItem(new ItemBuilder(instance.getParts().size() >= 1 ? XMaterial.NETHER_STAR : XMaterial.BARRIER)
                                .setName("§c" + Lang.get("Animation") + " #" + (1))
                                .getItem());

                        initialize();
                    } else instance.getAnimations()[0].initialize();
                } else {
                    ic.setLink(null);
                    if (clickType == ClickType.RIGHT_CLICK && instance.getParts().size() >= 1) {
                        instance.getParts().remove(0);
                        instance.getMenuGUI().getAnimPlayer().update();
                        onHover(gui, ic, ic, player);
                        initialize();
                    }
                }
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {
                if (instance.getParts().size() >= 1) {
                    MessageAPI.sendActionBar(instance.getPlayer(), Menu.ACTION_BAR(instance.getParts().get(0).getAnimation().getDisplayName(), "§e" + Lang.get("Edit"), "§c" + Lang.get("Delete")), WarpSystem.getInstance(), Integer.MAX_VALUE);
                } else MessageAPI.sendActionBar(instance.getPlayer(), "§3" + Lang.get("Leftclick") + ": §a" + Lang.get("Add"), WarpSystem.getInstance(), Integer.MAX_VALUE);
            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {
                MessageAPI.stopSendingActionBar(instance.getPlayer());
            }
        }).setLink(this.instance.getAnimations()[0]));

        if (!this.instance.getParts().isEmpty()) {
            instance.setItem(3, new ItemComponent(new ItemBuilder(XMaterial.BARRIER)
                    .setName("§c" + Lang.get("Animation") + " #" + 2)
                    .getItem(), new PremiumOnlyItemListener()));
        }
    }
}

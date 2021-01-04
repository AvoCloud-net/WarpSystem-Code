package de.codingair.warpsystem.spigot.features.animations.guis.editor;

import de.codingair.codingapi.player.gui.hotbar.HotbarGUI;
import de.codingair.codingapi.server.sounds.Sound;
import de.codingair.codingapi.server.sounds.SoundData;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.features.animations.utils.ParticlePart;
import de.codingair.warpsystem.spigot.versionfactory.VFac;
import de.codingair.warpsystem.spigot.versionfactory.VKey;
import org.bukkit.entity.Player;

import java.util.List;

public class Particles extends HotbarGUI {
    private final Menu menu;
    private final List<ParticlePart> parts;
    private final AnimationPart[] animations = new AnimationPart[5];

    public Particles(Player player, Menu menu) {
        super(player, WarpSystem.getInstance(), 2);

        setOpenSound(new SoundData(Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 1F));
        setCloseSound(new SoundData(Sound.ENTITY_PLAYER_LEVELUP, 0.5F, 0.5F));
        setClickSound(new SoundData(Sound.UI_BUTTON_CLICK, 0.5F, 1F));

        this.menu = menu;
        this.parts = menu.getClone().getParticleParts();

        for(int i = 0; i < 5; i++) {
            this.animations[i] = new AnimationPart(player, i, menu);
        }

        initialize();
    }

    public void initialize() {
        VFac.build(VKey.ParticlesHandler, this);
    }

    public Menu getMenuGUI() {
        return menu;
    }

    public AnimationPart[] getAnimations() {
        return animations;
    }

    public List<ParticlePart> getParts() {
        return parts;
    }
}
package de.codingair.warpsystem.spigot.versionfactory.objects;

import de.codingair.codingapi.player.gui.hotbar.HotbarGUI;
import de.codingair.codingapi.player.gui.hotbar.components.ItemComponent;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.language.Lang;

public class RotationItemComponent extends ItemComponent {
    public RotationItemComponent(HotbarGUI rotation) {
        super(new ItemBuilder(XMaterial.BLAZE_ROD).setName("§7» §c" + Lang.get("Rotation") + "§7 «").getItem());
        setLink(rotation);
    }
}

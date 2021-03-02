package de.codingair.warpsystem.spigot.base.guis.editor.hotbar;

import de.codingair.codingapi.player.gui.hotbar.components.ItemComponent;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;

public class PlaceholderItem extends ItemComponent {
    public PlaceholderItem() {
        super(new ItemBuilder(XMaterial.BLACK_STAINED_GLASS_PANE).setHideName(true).getItem());
    }
}

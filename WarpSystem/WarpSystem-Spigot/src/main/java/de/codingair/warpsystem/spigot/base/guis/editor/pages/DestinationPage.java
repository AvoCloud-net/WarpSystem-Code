package de.codingair.warpsystem.spigot.base.guis.editor.pages;

import de.codingair.codingapi.player.gui.inventory.gui.simple.Button;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SimpleGUI;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncButton;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.guis.editor.Editor;
import de.codingair.warpsystem.spigot.base.guis.editor.PageItem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.teleport.Origin;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.versionfactory.VFac;
import de.codingair.warpsystem.spigot.versionfactory.VKey;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class DestinationPage extends PageItem {
    private final Destination destination;
    private final Origin origin;
    private final Button[] extra;
    private boolean showOptions = false;

    public DestinationPage(Player player, String title, Destination destination, Origin origin, Button... extra) {
        super(player, title, null, false);
        this.destination = destination;
        this.origin = origin;
        this.extra = extra;
        initialize(player);
    }

    @Override
    public Button getPageButton() {
        return new SyncButton(0) {
            @Override
            public ItemStack craftItem() {
                ItemBuilder builder = new ItemBuilder(XMaterial.ENDER_PEARL).setName(Editor.ITEM_TITLE_COLOR + Lang.get("Destination") + (showOptions ? "§8 (§7" + Lang.get("Options") + "§8)" : ""));
                builder.addLore("", Editor.ITEM_SUB_TITLE_COLOR + Lang.get("Rightclick") + ": " + (showOptions ? "§c" + Lang.get("Back") : "§7" + Lang.get("Options")));

                return builder.getItem();
            }

            @Override
            public void onClick(InventoryClickEvent e, Player player) {
                if(e.isRightClick()) {
                    toggle(player);
                    update();
                }
            }

            @Override
            public boolean canClick(ClickType click) {
                return click == ClickType.LEFT || click == ClickType.RIGHT;
            }
        }.setLinkTrigger(ClickType.LEFT, ClickType.RIGHT);
    }

    private void toggle(Player player) {
        showOptions = !showOptions;
        for(int i = 1; i < 8; i++) {
            removeButton(i, 2);
        }

        if(getLast() != null && getLast().getCurrent() == this) getLast().updatePage();
        else initialize(player);
    }

    @Override
    public boolean initialize(SimpleGUI gui) {
        boolean result = super.initialize(gui);
        updateDestinationButtons();
        return result;
    }

    @Override
    public void initialize(Player p) {
        VFac.build(VKey.DestinationPageHandler, this, p, showOptions);
    }

    public void updateDestinationButtons() {
        if(destination.getId() == null && destination.getAdapter() == null && destination.getType() == null) {
            getLast().updateShowIcon();
        }

        for(int i = 1; i < 8; i++) {
            Button button = getButton(i, 2);
            if(button instanceof SyncButton) {
                ((SyncButton) button).update();
            }
        }
    }

    public Destination getDestination() {
        return destination;
    }

    public Origin getOrigin() {
        return origin;
    }

    public Button[] getExtra() {
        return extra;
    }
}

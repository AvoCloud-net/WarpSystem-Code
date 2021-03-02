package de.codingair.warpsystem.spigot.base.guis.editor.hotbar;

import de.codingair.codingapi.player.gui.hotbar.ClickType;
import de.codingair.codingapi.player.gui.hotbar.HotbarGUI;
import de.codingair.codingapi.player.gui.hotbar.ItemListener;
import de.codingair.codingapi.player.gui.hotbar.components.ItemComponent;
import de.codingair.codingapi.player.gui.inventory.gui.Skull;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.warpsystem.spigot.base.guis.editor.Editor;
import de.codingair.warpsystem.spigot.base.guis.editor.PageItem;
import de.codingair.warpsystem.spigot.base.utils.Lang;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class BackItem extends ItemComponent {
    public BackItem(Editor<?> fallback) {
        this(fallback, null, null);
    }

    public BackItem(PageItem fallback) {
        this(null, fallback, null);
    }

    public BackItem(Editor<?> fallback, @Nullable Consumer<Player> runnable) {
        this(fallback, null, runnable);
    }

    private BackItem(Editor<?> fallback, PageItem fallbackPage, @Nullable Consumer<Player> runnable) {
        super(new ItemBuilder(Skull.ArrowLeft).setName("§7» §c" + Lang.get("Back") + "§7 «").getItem(), new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                gui.close(false);

                if (runnable != null) runnable.accept(player);

                if (fallbackPage != null) {
                    fallbackPage.getLast().updatePage();
                    fallbackPage.getLast().open();
                } else {
                    fallback.updatePage();
                    fallback.open();
                }
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {

            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {

            }
        });
    }

    public BackItem(HotbarGUI fallback) {
        super(new ItemBuilder(Skull.ArrowLeft).setName("§7» §c" + Lang.get("Back") + "§7 «").getItem(), new ItemListener() {
            @Override
            public void onClick(HotbarGUI gui, ItemComponent ic, Player player, ClickType clickType) {
                ic.getLink().setStartSlot(-1);
            }

            @Override
            public void onHover(HotbarGUI gui, ItemComponent old, ItemComponent current, Player player) {

            }

            @Override
            public void onUnhover(HotbarGUI gui, ItemComponent current, ItemComponent newItem, Player player) {

            }
        });

        setLink(fallback);
    }
}

package de.codingair.warpsystem.spigot.versionfactory.handlers;

import de.codingair.codingapi.player.gui.anvil.*;
import de.codingair.codingapi.player.gui.inventory.gui.itembutton.ItemButton;
import de.codingair.codingapi.player.gui.inventory.gui.simple.SyncButton;
import de.codingair.codingapi.server.sounds.Sound;
import de.codingair.codingapi.server.sounds.SoundData;
import de.codingair.codingapi.tools.Callback;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.utils.TextAlignment;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.Lang;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.Action;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.types.BoundAction;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.types.CommandAction;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.types.CostsAction;
import de.codingair.warpsystem.spigot.base.utils.money.Bank;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.warps.guis.GChooseIconType;
import de.codingair.warpsystem.spigot.features.warps.guis.GWarps;
import de.codingair.warpsystem.spigot.features.warps.guis.IWarpGUI;
import de.codingair.warpsystem.spigot.features.warps.guis.editor.GEditor;
import de.codingair.warpsystem.spigot.features.warps.managers.IconManager;
import de.codingair.warpsystem.spigot.features.warps.nextlevel.utils.Icon;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class WarpGUI implements IWarpGUI {
    private final IconManager manager = WarpSystem.getInstance().getDataManager().getManager(FeatureType.WARP_GUI);
    private BukkitRunnable runnable;

    @Override
    public ItemBuilder getBarrier() {
        return new ItemBuilder(Material.BARRIER).setHideStandardLore(true)
                .setName("§3" + Lang.get("Leftclick") + ": §b" + Lang.get("Set_Icon"))
                .setLore("§3" + Lang.get("Rightclick") + ": §b" + Lang.get("Fast_Delete"));
    }

    @Override
    public void handleBarrierClick(InventoryClickEvent clickEvent, Player player, ItemButton button, GWarps gui, ItemStack none, int slot) {
        if (gui.isCloning()) {
            if (gui.getCursorIcon() == null) {
                gui.setCloning(false);
                clickEvent.getView().setCursor(new ItemStack(Material.AIR));
                return;
            }

            if (clickEvent.isLeftClick()) {
                IconManager.getInstance().getIcons().add(gui.getCursorIcon());
                gui.getCursorIcon().setPage(gui.getPage());
                gui.getCursorIcon().setSlot(clickEvent.getSlot());
                clickEvent.getView().setCursor(new ItemStack(Material.AIR));

                gui.setOldSlot(-999);
                gui.setCursor(null);
                gui.setCursorIcon(null);
                gui.setCloning(false);

                gui.reinitialize();
            } else if (clickEvent.isRightClick()) {
                IconManager.getInstance().getIcons().add(gui.getCursorIcon());
                gui.getCursorIcon().setPage(gui.getPage());
                gui.getCursorIcon().setSlot(clickEvent.getSlot());

                gui.getCursor().setAmount(gui.getCursor().getAmount() - 1);

                if (gui.getCursor().getAmount() == 0) {
                    gui.setOldSlot(-999);
                    gui.setCursor(null);
                    gui.setCursorIcon(null);
                    gui.setCloning(false);
                } else {
                    gui.setCursorIcon(gui.getCursorIcon().clone());
                    gui.getCursorIcon().setName(getCopiedName(gui.getCursorIcon().getName()));
                }

                clickEvent.getView().setCursor(gui.getCursor() == null ? new ItemStack(Material.AIR) : gui.getCursor());
                gui.reinitialize();
            }

            return;
        } else if (gui.isMoving()) {
            if (clickEvent.isLeftClick()) {
                gui.getCursorIcon().setPage(gui.getPage());
                gui.getCursorIcon().setSlot(clickEvent.getSlot());
                clickEvent.getView().setCursor(new ItemStack(Material.AIR));
                gui.setMoving(false, clickEvent.getSlot());
            }

            return;
        }

        if (clickEvent.isRightClick()) {
            clickEvent.getView().setCursor(none.clone());
            gui.setCloning(true);
        }

        if (!clickEvent.isLeftClick()) return;

        ItemStack item = player.getInventory().getItem(player.getInventory().getHeldItemSlot());
        if (item == null || item.getType().equals(Material.AIR)) {
            player.sendMessage(Lang.getPrefix() + Lang.get("No_Item_In_Hand"));
            return;
        }

        Callback<Boolean> callback = new Callback<Boolean>() {
            @Override
            public void accept(Boolean category) {
                if (category == null) {
                    Bukkit.getScheduler().runTask(WarpSystem.getInstance(), new Runnable() {
                        @Override
                        public void run() {
                            gui.open();
                        }
                    });
                    return;
                }

                AnvilGUI.openAnvil(WarpSystem.getInstance(), player, new AnvilListener() {
                    private String input;

                    @Override
                    public void onClick(AnvilClickEvent e) {
                        e.setCancelled(true);
                        e.setClose(false);

                        if (e.getSlot().equals(AnvilSlot.OUTPUT)) {
                            input = e.getInput();
                            button.playSound(e.getClickType(), player);

                            if (input == null) {
                                player.sendMessage(Lang.getPrefix() + Lang.get("Enter_Name"));
                                return;
                            }

                            if (input.contains("@")) {
                                player.sendMessage(Lang.getPrefix() + Lang.get("Enter_Correct_Name"));
                                return;
                            }

                            input = ChatColor.translateAlternateColorCodes('&', input);

                            if (clickEvent.isRightClick()) {
                                StringBuilder builder = new StringBuilder();

                                boolean color = false;
                                for (char c : input.toCharArray()) {
                                    builder.append(c);

                                    if (c == '§') color = true;
                                    else if (color) {
                                        builder.append("§n");
                                        color = false;
                                    }
                                }

                                input = builder.toString();
                            }

                            if (category) {
                                if (manager.existsPage(input)) {
                                    player.sendMessage(Lang.getPrefix() + Lang.get("Name_Already_Exists"));
                                    return;
                                }
                            } else {
                                if (manager.existsIcon(input)) {
                                    player.sendMessage(Lang.getPrefix() + Lang.get("Name_Already_Exists"));
                                    return;
                                }
                            }

                            input = input.replace("§", "&");

                            e.setClose(true);
                        }
                    }

                    @Override
                    public void onClose(AnvilCloseEvent e) {
                        if (e.isSubmitted())
                            e.setPost(() -> {
                                Icon icon = new Icon(input, item, gui.getPage(), slot, null);
                                if (gui.getWorld() != null) icon.addAction(new BoundAction(gui.getWorld()));

                                icon.setPage(category);
                                new GEditor(player, icon).setFallbackGUI(gui).setUseFallbackGUI(true).open();
                            });
                        else {
                            Sound.ENTITY_ITEM_BREAK.playSound(player);
                            e.setPost(() -> new GWarps(player, gui.getPage(), gui.isEditing()).open());
                        }
                    }
                }, new ItemBuilder(Material.PAPER).setName(Lang.get("Name") + "...").getItem());
            }
        };

        new GChooseIconType(player, gui.getPage(), callback).open();
    }

    @Override
    public void modifyEditingIconBuilder(ItemBuilder iconBuilder, Icon icon) {
        List<String> commands = icon.hasAction(Action.COMMAND) ? icon.getAction(CommandAction.class).getValue() : null;
        List<String> commandInfo = new ArrayList<>();

        if (commands != null) {
            for (String command : commands) {
                commandInfo.add("§7- '" + command + "'");
            }
        }

        String permission = icon.getPermission() == null ? "-" : icon.getPermission();
        String costs = (icon.getAction(Action.COSTS) == null ? "0" : icon.getAction(CostsAction.class).getValue()) + " " + Lang.get("Coins");

        if (icon.isDisabled()) {
            iconBuilder.addText("§8------------");
            iconBuilder.addText(Lang.get("Icon_Is_Disabled"));
        }

        iconBuilder.addText("§8------------");

        iconBuilder.addText("§7" + Lang.get("Commands") + ": " + (commandInfo.isEmpty() ? "-" : ""));
        iconBuilder.addText(commandInfo);
        iconBuilder.addText("§7" + Lang.get("Permission") + ": " + permission);
        if (Bank.isReady()) iconBuilder.addText("§7" + Lang.get("Costs") + ": " + costs);
        iconBuilder.addText("§8------------");
        iconBuilder.addText("§3" + Lang.get("Leftclick") + ": §7" + Lang.get("Edit"));
        iconBuilder.addText("§3" + Lang.get("Shift_Leftclick") + ": §7" + Lang.get("Move"));
        iconBuilder.addText("§3" + Lang.get("Rightclick") + ": §7" + ChatColor.stripColor(Lang.get("Change_Item")));
        iconBuilder.addText("§3" + Lang.get("Shift_Rightclick") + ": §7" + (runnable != null ? "§4" : "§7") + ChatColor.stripColor(Lang.get("Delete")) + (runnable != null ? " §7(§c" + ChatColor.stripColor(Lang.get("Confirm")) + "§7)" : ""));
        iconBuilder.addText("§3" + Lang.get("Pick_Block_Click") + ": §7" + ChatColor.stripColor(Lang.get("Copy")));

        if (!icon.isPage()) {
            iconBuilder.addText("§8------------");

            List<String> list = TextAlignment.lineBreak(Lang.get("Move_Help"), 80);
            iconBuilder.addText(list);
        }

        commandInfo.clear();
    }

    @Override
    public void onEditingIconClick(InventoryClickEvent e, Player player, SyncButton button, Icon icon, SoundData s, GWarps gui) {
        s.play(player);

        if (gui.isCloning() && gui.getCursorIcon() == null) {
            //fast deleting
            IconManager.getInstance().remove(icon);
            gui.reinitialize();

            List<Icon> icons = IconManager.getInstance().getIcons(gui.getPage());
            if (icons.isEmpty()) {
                gui.setCloning(false);
                e.getView().setCursor(new ItemStack(Material.AIR));
            }
            icons.clear();
            return;
        }

        if ((e.getClick() == ClickType.UNKNOWN || e.getClick() == ClickType.MIDDLE) && gui.getEmptySlots() > 0) {
            if (!gui.isMoving() && gui.getCursorIcon() == null && gui.getCursor() == null) {
                gui.setCloning(true);
                gui.setCursorIcon(icon.clone());

                gui.getCursorIcon().setName(getCopiedName(gui.getCursorIcon().getName()));
                gui.setCursor(e.getCurrentItem().clone());
                gui.getCursor().setAmount(gui.getEmptySlots());

                e.getView().setCursor(gui.getCursor().clone());
            }
        } else if (e.isLeftClick()) {
            if (gui.isCloning()) {
                gui.setCloning(false);
                gui.setOldSlot(-999);
                gui.setCursor(null);
                gui.setCursorIcon(null);

                e.getView().setCursor(new ItemStack(Material.AIR));
            } else if (gui.isMoving()) {
                if (icon.isPage() && icon.getPage() != gui.getCursorIcon().getPage()) return;
                Icon otherCat = null;
                if (!gui.getCursorIcon().isPage()) {
                    otherCat = gui.getCursorIcon().getPage();
                    gui.getCursorIcon().setPage(gui.getPage());
                }

                icon.setSlot(gui.getOldSlot());
                icon.setPage(otherCat);
                gui.getCursorIcon().setSlot(e.getSlot());
                e.getView().setCursor(new ItemStack(Material.AIR));
                gui.setMoving(false, e.getSlot());
            } else {
                if (e.isShiftClick()) {
                    gui.setCursorIcon(icon);
                    gui.setCursor(e.getCurrentItem().clone());
                    e.getView().setCursor(gui.getCursor().clone());
                    e.setCurrentItem(new ItemStack(Material.AIR));
                    gui.setMoving(true, e.getSlot());
                } else {
                    gui.changeGUI(new GEditor(player, icon), true);
                }
            }
        } else if (e.isRightClick()) {
            if (gui.isCloning()) return;
            if (gui.isMoving()) {
                if (icon.isPage() && !gui.getCursorIcon().isPage()) {
                    gui.setPage(icon);
                    gui.reinitialize();
                    gui.setTitle(GWarps.getTitle(gui.getPage(), player));
                }
            } else {
                if (e.isShiftClick()) {
                    if (runnable != null) {
                        //delete
                        manager.remove(icon);
                        player.sendMessage(Lang.getPrefix() + Lang.get("Icon_Deleted"));

                        runnable.cancel();
                        runnable = null;
                        gui.reinitialize();
                    } else {
                        runnable = new BukkitRunnable() {
                            @Override
                            public void run() {
                                runnable = null;
                                button.update();
                            }
                        };

                        runnable.runTaskLater(WarpSystem.getInstance(), 20);
                        button.update();
                    }
                } else {
                    if (player.getInventory().getItem(player.getInventory().getHeldItemSlot()) == null || player.getInventory().getItem(player.getInventory().getHeldItemSlot()).getType() == Material.AIR
                            || icon.getRaw().getType() == player.getInventory().getItem(player.getInventory().getHeldItemSlot()).getType()) {
                        player.sendMessage(Lang.getPrefix() + Lang.get("No_Item_In_Hand"));
                        return;
                    }

                    icon.changeItem(player.getInventory().getItem(player.getInventory().getHeldItemSlot()));
                    gui.reinitialize();
                    GWarps.updateInventory(player);
                }
            }
        }
    }

    private String getCopiedName(String name) {
        int num = 1;

        name = name.replaceAll("\\p{Blank}\\([0-9]{1,5}?\\)\\z", "");
        name += " (" + num++ + ")";

        while (IconManager.getInstance().getIcon(name) != null) {
            name = name.replaceAll("\\p{Blank}\\([0-9]{1,5}?\\)\\z", "");
            name += " (" + num++ + ")";
        }

        return name;
    }
}

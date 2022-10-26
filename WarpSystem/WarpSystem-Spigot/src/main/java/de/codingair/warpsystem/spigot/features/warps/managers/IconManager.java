package de.codingair.warpsystem.spigot.features.warps.managers;

import de.codingair.codingapi.files.ConfigFile;
import de.codingair.codingapi.tools.io.ConfigMask;
import de.codingair.codingapi.tools.io.JSON.JSON;
import de.codingair.codingapi.tools.io.JSON.JSONParser;
import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.codingapi.utils.ChatColor;
import de.codingair.warpsystem.core.utils.Manager;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.setupassistant.annotations.AvailableForSetupAssistant;
import de.codingair.warpsystem.spigot.base.setupassistant.annotations.Function;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.types.WarpAction;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.DestinationType;
import de.codingair.warpsystem.spigot.features.FeatureType;
import de.codingair.warpsystem.spigot.features.simplewarps.SimpleWarp;
import de.codingair.warpsystem.spigot.features.simplewarps.commands.CWarp;
import de.codingair.warpsystem.spigot.features.simplewarps.managers.SimpleWarpManager;
import de.codingair.warpsystem.spigot.features.warps.commands.CWarps;
import de.codingair.warpsystem.spigot.features.warps.importfilter.PageData;
import de.codingair.warpsystem.spigot.features.warps.importfilter.WarpData;
import de.codingair.warpsystem.spigot.features.warps.nextlevel.exceptions.IconReadException;
import de.codingair.warpsystem.spigot.features.warps.nextlevel.utils.Icon;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Stream;

@AvailableForSetupAssistant (type = "WarpGUI", config = "Config")
@Function (name = "Enabled", defaultValue = "true", configPath = "WarpSystem.Functions.WarpGUI", clazz = Boolean.class)
@Function (name = "Teleport message", defaultValue = "true", configPath = "WarpSystem.Send.Teleport_Message.WarpGUI", clazz = Boolean.class)
@Function (name = "Different GUI for each world", defaultValue = "false", configPath = "WarpSystem.GUI.Bound_to_world", clazz = Boolean.class)
@Function (name = "Use /warp for WarpGUI", defaultValue = "false", configPath = "WarpSystem.Commands.Warp.GUI", clazz = Boolean.class)
@Function (name = "Show_Without_Permission", since = "v5.0.0", description = "§7If true players will §esee all icons §7even when they don't have their permissions §8(they still §ccan't use them§8)§7.", defaultValue = "false", configPath = "WarpSystem.GUI.Show_Without_Permission", clazz = Boolean.class)
public class IconManager implements Manager {
    private final Set<Icon> icons = new HashSet<>();
    private ItemStack background = null;
    private boolean showWithoutPermission = false;

    private static ItemBuilder STANDARD_ITEM() {
        return new ItemBuilder(Material.GRASS);
    }

    public static IconManager getInstance() {
        return WarpSystem.getInstance().getDataManager().getManager(FeatureType.WARP_GUI);
    }

    public boolean load(boolean loader) {
        if (WarpSystem.getInstance().getFileManager().getFile("ActionIcons") == null) WarpSystem.getInstance().getFileManager().loadFile("ActionIcons", "/Memory/");

        //Load
        boolean success = true;

        WarpSystem.log("  > Loading Icons");

        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("ActionIcons");
        FileConfiguration config = file.getConfig();

        WarpSystem.log("    > Loading background");
        Object data = config.get("Background_Item", null);

        if (data != null) {
            if (data instanceof String) {
                this.background = ItemBuilder.getFromJSON((String) data).getItem();
            } else this.background = new ConfigMask(file).getItemStack("Background_Item");
        }

        if (this.background == null) {
            WarpSystem.log("      ...no background available > create standard");
            this.background = new ItemBuilder(XMaterial.BLACK_STAINED_GLASS_PANE).setHideName(true).getItem();
        } else WarpSystem.log("      ...got 1 background");

        WarpSystem.log("    > Loading Icons");
        this.icons.clear();

        List<?> l = file.getConfig().getList("Pages");
        if (l != null) {
            for (Object o : l) {
                if (o instanceof Map) {
                    success = buildIcon(success, (Map<?, ?>) o);
                }
            }
        }

        l = file.getConfig().getList("Icons");
        if (l != null)
            for (Object s : l) {
                if (s instanceof Map) {
                    success = buildIcon(success, (Map<?, ?>) s);
                } else if (s instanceof String) {
                    try {
                        JSON json = (JSON) new JSONParser().parse((String) s);

                        Icon icon = new Icon();
                        try {
                            icon.read(json);
                            icons.add(icon);
                        } catch (IconReadException e) {
                            e.printStackTrace();
                            success = false;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        new CWarps().register();

        ConfigFile configfile = WarpSystem.getInstance().getFileManager().getFile("Config");

        if (configfile.getConfig().getBoolean("WarpSystem.Commands.Warp.GUI", false) && !FeatureType.SIMPLE_WARPS.isActive()){
            new CWarp().register();
        }

        this.showWithoutPermission = configfile.getConfig().getBoolean("WarpSystem.GUI.Show_Without_Permission", false);

        int icons = this.icons.size();
        clean(null);
        if (icons > this.icons.size()) {
            WarpSystem.log("      ...cleaned a total of " + (icons - this.icons.size()) + " icon(s)");
        }

        WarpSystem.log("      ...got " + this.icons.size() + " " + (this.icons.size() == 1 ? "Icon" : "Icons"));
        return success;
    }

    private boolean buildIcon(boolean success, Map<?, ?> o) {
        JSON json = new JSON(o);

        Icon icon = new Icon();
        try {
            icon.read(json);
            icons.add(icon);
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        }

        return success;
    }

    private Stream<Icon> sortPages(Set<Icon> pages) {
        return pages.stream().filter(Icon::isPage).sorted(Comparator.comparingInt(Icon::getDepth));
    }

    private void clean(Icon page) {
        if (page != null && !page.isPage()) throw new IllegalArgumentException("Given icon is not a category!");
        Icon[] iconList = new Icon[54];

        for (Icon icon : getIcons(page)) {
            Icon other = iconList[icon.getSlot()];

            if (other == null) iconList[icon.getSlot()] = icon;
            else if (other.isPage() && icon.isPage()) {
                List<Icon> l0 = getIcons(icon);
                List<Icon> l1 = getIcons(other);

                if (l0.size() >= l1.size()) {
                    remove(other);
                    iconList[icon.getSlot()] = icon;
                } else {
                    remove(icon);
                }

                l0.clear();
                l1.clear();
            } else if (other.isPage() && !icon.isPage()) {
                remove(icon);
            } else if (!other.isPage() && icon.isPage()) {
                remove(other);
                iconList[icon.getSlot()] = icon;
            } else {
                remove(icon);
            }
        }

        List<Icon> pages = getPages(page);

        for (Icon icon : pages) {
            clean(icon);
        }

        pages.clear();
    }

    public void save(boolean saver) {
        if (!saver) WarpSystem.log("  > Saving Icons");

        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("ActionIcons");
        ConfigMask mask = new ConfigMask(file);

        if (!saver) WarpSystem.log("    > Saving background");
        mask.put("Background_Item", this.background);
        if (!saver) WarpSystem.log("      ...saved 1 background");

        if (!saver) WarpSystem.log("    > Saving Icons");

        //pages
        List<JSON> pages = new ArrayList<>();
        sortPages(this.icons).forEach(i -> {
            JSON json = new JSON();
            i.write(json);
            pages.add(json);
        });
        mask.put("Pages", pages);

        //icons
        List<JSON> icons = new ArrayList<>();
        this.icons.stream().filter(i -> !i.isPage()).forEach(i -> {
            JSON json = new JSON();
            i.write(json);
            icons.add(json);
        });
        mask.put("Icons", icons);


        if (!saver) WarpSystem.log("      ...saved " + (icons.size() + pages.size()) + " Icon(s)");

        file.saveConfig();
    }

    @Override
    public void destroy() {
        this.icons.clear();
    }

    public List<Icon> getPages() {
        List<Icon> icons = new ArrayList<>();

        for (Icon icon : this.icons) {
            if (icon.isPage()) icons.add(icon);
        }

        return icons;
    }

    public List<Icon> getPages(Icon page) {
        if (page != null && !page.isPage()) throw new IllegalArgumentException("Given icon is not a category!");
        List<Icon> icons = new ArrayList<>();

        for (Icon icon : this.icons) {
            if (icon.isPage() && Objects.equals(page, icon.getPage())) icons.add(icon);
        }

        return icons;
    }

    public boolean boundToWorld() {
        return WarpSystem.getInstance().getFileManager().getFile("Config").getConfig().getBoolean("WarpSystem.GUI.Bound_to_world", false);
    }

    private int getNextFreeSlot(Icon page) {
        int slot = 0;

        boolean available;

        do {
            available = true;

            if (slot > 53) {
                available = false;
                break;
            }

            if (page == null) {
                for (Icon c : getPages()) {
                    if (c.getSlot() == slot) {
                        slot++;
                        available = false;
                        break;
                    }
                }
            }

            for (Icon warp : getIcons(page)) {
                if (warp.getSlot() == slot) {
                    slot++;
                    available = false;
                    break;
                }
            }
        } while (!available);

        if (available) return slot;
        else return -999;
    }

    public boolean importPageData(PageData pageData) {
        if (this.existsPage(pageData.getName())) return false;

        int slot = getNextFreeSlot(null);
        if (slot == -999) return false;

        Icon icon = new Icon(pageData.getName(), STANDARD_ITEM().setName(pageData.getName()).getItem(), null, slot, pageData.getPermission());
        icon.setPage(true);
        this.icons.add(icon);

        boolean result = true;

        for (WarpData warpData : pageData.getWarps()) {
            if (!importWarpData(warpData)) result = false;
        }

        return result;
    }

    public boolean importWarpData(WarpData warpData) {
        if (SimpleWarpManager.getInstance().existsWarp(warpData.getName())) return false;
        if (warpData.getPage() != null && !existsPage(warpData.getPage())) return false;
        if (this.existsIcon(warpData.getName())) return false;

        Icon page = warpData.getPage() == null ? null : getPage(warpData.getPage());

        int slot = getNextFreeSlot(page);
        if (slot == -999) return false;

        SimpleWarpManager.getInstance().addWarp(new SimpleWarp(warpData));

        Icon icon = new Icon(warpData.getName(), STANDARD_ITEM().setName(warpData.getName()).getItem(), page, slot, warpData.getPermission(), new WarpAction(new Destination(warpData.getName(), DestinationType.SimpleWarp)));
        this.icons.add(icon);
        return true;
    }

    public boolean existsIcon(String name) {
        if (name == null) return false;
        return getIcon(name) != null;
    }

    public boolean existsPage(String name) {
        if (name == null) return false;
        return getPage(name) != null;
    }

    public Icon getPage(String name) {
        if (name == null) return null;
        name = ChatColor.stripColor(ChatColor.translateAll('&', name));

        for (Icon icon : this.icons) {
            if (!icon.isPage()) continue;
            if (icon.getNameWithoutColor().equalsIgnoreCase(name)) return icon;
        }

        return null;
    }

    public Icon getIcon(String name) {
        if (name == null) return null;
        name = ChatColor.stripColor(ChatColor.translateAll('&', name));

        for (Icon icon : this.icons) {
            if (icon.isPage() || icon.getName() == null) continue;
            if (icon.getNameWithoutColor().equalsIgnoreCase(name)) return icon;
        }

        return null;
    }

    public List<Icon> getIcons(Icon page) {
        if (page != null && !page.isPage()) throw new IllegalArgumentException("Given icon is not a page!");
        List<Icon> icons = new ArrayList<>();

        for (Icon icon : this.icons) {
            if (Objects.equals(page, icon.getPage())) icons.add(icon);
        }

        return icons;
    }

    public void remove(Icon icon) {
        if (icon.isPage()) {
            List<Icon> warps = getIcons(icon);

            for (Icon warp : warps) {
                remove(warp);
            }
        }

        this.icons.remove(icon);
    }

    public Set<Icon> getIcons() {
        return icons;
    }

    public ItemStack getBackground() {
        return background;
    }

    public void setBackground(ItemStack background) {
        if (background == null) background = new ItemStack(Material.AIR);
        new ItemBuilder(background).removeLore().setName(null).setHideName(true).setHideStandardLore(true).setHideEnchantments(true);
        this.background = background;
    }

    public boolean isShowWithoutPermission() {
        return showWithoutPermission;
    }
}

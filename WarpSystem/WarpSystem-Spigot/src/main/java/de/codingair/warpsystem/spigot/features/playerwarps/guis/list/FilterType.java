package de.codingair.warpsystem.spigot.features.playerwarps.guis.list;

import de.codingair.codingapi.player.gui.inventory.gui.simple.Button;
import de.codingair.codingapi.utils.Node;
import de.codingair.warpsystem.spigot.base.utils.Lang;
import de.codingair.warpsystem.spigot.base.utils.Permissions;
import de.codingair.warpsystem.spigot.features.playerwarps.guis.list.filters.*;
import de.codingair.warpsystem.spigot.features.playerwarps.managers.PlayerWarpManager;
import de.codingair.warpsystem.spigot.versionfactory.VFac;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.function.Supplier;

public enum FilterType {
    OWN_WARPS(OwnWarpFilter.class, Lang.get("Filter_Own_Warps"), 0, () -> true),
    ALL_WARPS(AllWarps.class, Lang.get("Filter_All_Warps"), 1, () -> PlayerWarpManager.getManager().isAllowPublicWarps() || PlayerWarpManager.getManager().isAllowTrustedMembers()),
    ALL_PLAYERS(AllPlayers.class, Lang.get("Filter_All_Players"), 2, () -> PlayerWarpManager.getManager().isAllowPublicWarps() || PlayerWarpManager.getManager().isAllowTrustedMembers()),
    CLASSES(ClassesFilter.class, Lang.get("Filter_Classes"), 3, () -> PlayerWarpManager.getManager().isClasses());

    private final String filterName;
    private final int id;
    private final Supplier<Boolean> enabled;
    private Filter instance;

    FilterType(Class<? extends Filter> clazz, String filterName, int id, Supplier<Boolean> enabled) {
        try {
            this.instance = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        this.filterName = filterName;
        this.id = id;
        this.enabled = enabled;
    }

    public static int active() {
        int i = 0;
        for (FilterType value : values()) {
            if (value.isEnabled()) i++;
        }
        return i;
    }

    public static FilterType checkName(String name) {
        try {
            return valueOf(name);
        } catch (Exception ex) {
            return OWN_WARPS;
        }
    }

    public Node<List<Button>, Integer> getListItems(int maxSize, int page, Player player, String search, Object... extra) {
        return instance.getListItems(maxSize, page, player, search, extra);
    }

    public FilterType next(Player player) {
        int next = id + 1;
        if (next >= values().length) next = 0;

        boolean b = VFac.isAvailable("Indicator");
        while (!values()[next].isEnabled() || (b && values()[next] == CLASSES && !player.hasPermission(Permissions.PERMISSION_MODIFY_PLAYER_WARPS))) {
            next++;
            if (next >= values().length) next = 0;
        }

        return values()[next];
    }

    public FilterType previous(Player player) {
        int previous = id - 1;
        if (previous < 0) previous = values().length - 1;

        boolean b = VFac.isAvailable("Indicator");
        while (!values()[previous].isEnabled() || (b && values()[previous] == CLASSES && !player.hasPermission(Permissions.PERMISSION_MODIFY_PLAYER_WARPS))) {
            previous--;
            if (previous < 0) previous = values().length - 1;
        }

        return values()[previous];
    }

    public String getFilterName() {
        return filterName;
    }

    public boolean deleteExtraBeforeChangeFilter() {
        return instance.deleteExtraBeforeChangeFilter();
    }

    public PWPage.FilterButton getControllButton(PWPage page, int warps) {
        return instance.getControllButton(page, warps);
    }

    public Object[] getStandardExtra(PWList list) {
        return instance.getStandardExtra(list);
    }

    public boolean createButtonInList() {
        return instance.createButtonInList();
    }

    public boolean searchable(PWPage page) {
        return instance.searchable(page);
    }

    public boolean isEnabled() {
        Boolean b = enabled.get();
        return b != null && b;
    }
}

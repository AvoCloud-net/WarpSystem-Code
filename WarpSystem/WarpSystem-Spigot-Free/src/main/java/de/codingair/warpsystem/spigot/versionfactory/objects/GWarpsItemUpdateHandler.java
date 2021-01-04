package de.codingair.warpsystem.spigot.versionfactory.objects;

import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.features.warps.guis.GWarps;
import de.codingair.warpsystem.spigot.features.warps.nextlevel.utils.Icon;
import org.bukkit.entity.Player;

public class GWarpsItemUpdateHandler {
    public GWarpsItemUpdateHandler(Player p, Icon icon, GWarps instance) {
        Lang.PREMIUM_CHAT(p);
    }
}

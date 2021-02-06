package de.codingair.warpsystem.spigot.api.placeholders;

import de.codingair.warpsystem.spigot.api.placeholders.custom.PlayerWarpPlaceholderExpansion;
import de.codingair.warpsystem.spigot.api.placeholders.custom.ServerPlaceholderExpansion;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class WarpSystemPlaceholderExpansion extends PlaceholderExpansion {
    private final IPlaceholder[] placeholders = {
            new ServerPlaceholderExpansion(),
            new PlayerWarpPlaceholderExpansion()
    };

    @Override
    public boolean persist() {
        return false;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "warpsystem";
    }

    @Override
    public @NotNull String getAuthor() {
        return WarpSystem.getInstance().getDescription().getAuthors().toString();
    }

    @Override
    public @NotNull String getVersion() {
        return WarpSystem.getInstance().getDescription().getVersion();
    }

    private IPlaceholder getPlaceholder(String id) {
        for (IPlaceholder p : placeholders) {
            if (p.getIdentifier().equals(id)) return p;
        }

        return null;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String s) {
        if (player == null) return null;
        s = s.toLowerCase();

        int idx = s.indexOf('_');
        if (idx == -1 || s.length() <= idx + 1) return null;

        String id = s.substring(0, idx);
        IPlaceholder p = getPlaceholder(id);
        if (p == null) return null;

        return p.onRequest(player, s.substring(idx + 1).split("_", -1));
    }
}

package de.codingair.warpsystem.spigot.base.utils.placeholderapi;

import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.features.FeatureType;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class WarpSystemPlaceholderExpansion extends PlaceholderExpansion {
    private final FeatureType type;

    public WarpSystemPlaceholderExpansion(@Nullable FeatureType type) {
        this.type = type;
    }

    @Override
    public boolean persist() {
        return false;
    }

    @Override
    public boolean canRegister() {
        return type == null || type.isActive();
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

    public abstract String onRequest(Player player, String id);

    @Override
    public String onPlaceholderRequest(Player p, @NotNull String params) {
        return onRequest(p, params);
    }
}

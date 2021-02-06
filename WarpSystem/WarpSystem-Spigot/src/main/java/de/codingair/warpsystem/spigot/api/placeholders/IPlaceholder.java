package de.codingair.warpsystem.spigot.api.placeholders;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public interface IPlaceholder {
    String getIdentifier();
    String onRequest(@NotNull Player player, @NotNull String[] args);
}

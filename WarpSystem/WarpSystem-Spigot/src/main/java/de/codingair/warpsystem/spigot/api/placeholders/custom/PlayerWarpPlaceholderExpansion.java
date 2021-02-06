package de.codingair.warpsystem.spigot.api.placeholders.custom;

import de.codingair.warpsystem.spigot.api.placeholders.IPlaceholder;
import de.codingair.warpsystem.spigot.features.playerwarps.managers.PlayerWarpManager;
import de.codingair.warpsystem.spigot.features.playerwarps.utils.PlayerWarp;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PlayerWarpPlaceholderExpansion implements IPlaceholder {
    @Override
    public String getIdentifier() {
        return "playerwarps";
    }

    @Override
    public String onRequest(@NotNull Player player, @NotNull String @NotNull [] args) {
        List<PlayerWarp> warps = PlayerWarpManager.getManager().getOwnWarps(player);

        switch (args[0]) {
            case "count":
                return warps.size() + "";
            case "max":
                return PlayerWarpManager.getManager().getMaxAmount(player) + "";
        }

        return null;
    }
}

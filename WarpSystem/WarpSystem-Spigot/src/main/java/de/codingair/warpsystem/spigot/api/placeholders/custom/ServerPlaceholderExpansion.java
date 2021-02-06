package de.codingair.warpsystem.spigot.api.placeholders.custom;

import de.codingair.codingapi.utils.ChatColor;
import de.codingair.warpsystem.core.transfer.packets.spigot.utils.ServerPing;
import de.codingair.warpsystem.spigot.api.placeholders.IPlaceholder;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ServerPlaceholderExpansion implements IPlaceholder {
    @Override
    public String getIdentifier() {
        return "s";
    }

    @Override
    public String onRequest(@NotNull Player player, @NotNull String @NotNull [] args) {
        if (args.length == 2) {
            String server = args[0];

            ServerPing ping = WarpSystem.getInstance().getServerManager().getProperties(server);

            String property = args[1];
            switch (property.toLowerCase()) {
                case "p":
                    if (ping == null) return "0";
                    else return ping.getPlayers() + "";
                case "mp":
                    if (ping == null) return "0";
                    else return ping.getMaxPlayers() + "";
                case "s":
                    return WarpSystem.opt().getStatus(ping);
                case "m":
                    if (ping == null) return "";
                    else return ping.getMotd() == null ? "" : ChatColor.translateAll('&', ping.getMotd());
                case "ci":
                    return WarpSystem.opt().getPlaceholderCountInfo(ping);
            }
        }

        return null;
    }
}

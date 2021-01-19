package de.codingair.warpsystem.spigot.api.placeholders;

import de.codingair.codingapi.utils.ChatColor;
import de.codingair.warpsystem.core.transfer.packets.spigot.utils.ServerPing;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.Lang;
import de.codingair.warpsystem.spigot.base.utils.placeholderapi.WarpSystemPlaceholderExpansion;
import org.bukkit.entity.Player;

class ServerPlaceholderExpansion extends WarpSystemPlaceholderExpansion {
    ServerPlaceholderExpansion() {
        super(null);
    }

    @Override
    public String onRequest(Player player, String id) {
        if (player == null) return null;
        id = id.toLowerCase();
        if (!id.startsWith("s_")) return null;
        id = id.replace("s_", "");

        String[] args = id.split("_", -1);

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

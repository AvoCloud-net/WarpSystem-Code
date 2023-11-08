package de.codingair.warpsystem.spigot.versionfactory.objects;

import de.codingair.warpsystem.spigot.base.utils.Lang;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RTP_Go_Command_Handler {
    public RTP_Go_Command_Handler(String player, String targetServer, String targetWorld, CommandSender sender, Player p) {
        Lang.PREMIUM_CHAT(sender);
    }
}

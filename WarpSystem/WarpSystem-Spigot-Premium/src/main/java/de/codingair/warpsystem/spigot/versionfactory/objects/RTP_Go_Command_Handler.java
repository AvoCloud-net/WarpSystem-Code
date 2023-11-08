package de.codingair.warpsystem.spigot.versionfactory.objects;

import de.codingair.warpsystem.core.transfer.packets.spigot.RandomTPPacket;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.Lang;
import org.bukkit.command.CommandSender;

public class RTP_Go_Command_Handler {
    public RTP_Go_Command_Handler(String player, String targetServer, String targetWorld, CommandSender sender) {
        WarpSystem.getDataHandler().send(new RandomTPPacket(player, targetServer, targetWorld, !player.equalsIgnoreCase(sender.getName())), null).thenAccept(packet -> {
            if (packet.getBoolean()) {
                if (!player.equalsIgnoreCase(sender.getName())) sender.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_Teleported_Other").replace("%PLAYER%", player));
            } else sender.sendMessage(Lang.getPrefix() + Lang.get("Server_Is_Not_Online"));
        });
    }
}

package de.codingair.warpsystem.spigot.versionfactory.objects;

import de.codingair.warpsystem.base.transfer.packets.spigot.RandomTPPacket;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.Lang;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RTP_Go_Command_Handler {
    public RTP_Go_Command_Handler(String player, String targetServer, String targetWorld, String finalPlayer, CommandSender sender, Player p) {
        WarpSystem.getDataHandler().send(new RandomTPPacket(player, targetServer, targetWorld, !finalPlayer.equalsIgnoreCase(sender.getName())), p).thenAccept(packet -> {
            if (packet.getBoolean()) {
                if (!finalPlayer.equalsIgnoreCase(sender.getName())) sender.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_Teleported_Other").replace("%PLAYER%", finalPlayer));
            } else sender.sendMessage(Lang.getPrefix() + Lang.get("Server_Is_Not_Online"));
        });
    }
}

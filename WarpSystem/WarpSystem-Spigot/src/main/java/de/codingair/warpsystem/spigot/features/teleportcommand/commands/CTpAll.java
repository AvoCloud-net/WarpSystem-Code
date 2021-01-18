package de.codingair.warpsystem.spigot.features.teleportcommand.commands;

import de.codingair.codingapi.server.commands.builder.BaseComponent;
import de.codingair.codingapi.server.commands.builder.CommandComponent;
import de.codingair.warpsystem.spigot.api.WSCommandBuilder;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.Lang;
import de.codingair.warpsystem.spigot.base.utils.Permissions;
import de.codingair.warpsystem.spigot.base.utils.teleport.Origin;
import de.codingair.warpsystem.spigot.base.utils.teleport.TeleportOptions;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.adapters.LocationAdapter;
import de.codingair.warpsystem.spigot.features.teleportcommand.TeleportCommandManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CTpAll extends WSCommandBuilder {
    public CTpAll() {
        super("TpAll", new BaseComponent(Permissions.PERMISSION_USE_TELEPORT_COMMAND_TPALL) {
            @Override
            public void noPermission(CommandSender sender, String label, CommandComponent child) {
                sender.sendMessage(Lang.getPrefix() + Lang.get("No_Permission"));
            }

            @Override
            public void onlyFor(boolean player, CommandSender sender, String label, CommandComponent child) {
                sender.sendMessage(Lang.getPrefix() + Lang.get("Only_For_Players"));
            }

            @Override
            public void unknownSubCommand(CommandSender sender, String label, String[] args) {
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                Player p = (Player) sender;
                int iHandled = 0;
                int iSent = 0;

                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getName().equals(sender.getName())) continue;
                    iHandled++;
                    if (TeleportCommandManager.getInstance().deniesForceTps(player)) continue;

                    TeleportOptions options = new TeleportOptions(new Destination(new LocationAdapter(((Player) sender).getLocation())), sender.getName(), Origin.TeleportRequest);
                    options.setSkip(true);

                    WarpSystem.getInstance().getTeleportManager().teleport(player, options);
                    iSent++;
                }

                TeleportCommandManager.handler().tpAll(p, iHandled, iSent);
                return false;
            }
        }.setOnlyPlayers(true));
    }
}

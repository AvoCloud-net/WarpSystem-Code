package de.codingair.warpsystem.spigot.features.teleportcommand.commands;

import de.codingair.codingapi.server.commands.builder.BaseComponent;
import de.codingair.codingapi.server.commands.builder.CommandComponent;
import de.codingair.codingapi.server.commands.builder.special.MultiCommandComponent;
import de.codingair.warpsystem.spigot.api.WSCommandBuilder;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.Lang;
import de.codingair.warpsystem.spigot.base.utils.Permissions;
import de.codingair.warpsystem.spigot.features.teleportcommand.TeleportCommandManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CTpHere extends WSCommandBuilder {
    public CTpHere() {
        super("TpHere", new BaseComponent() {
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
                sender.sendMessage(Lang.getPrefix() + WarpSystem.opt().cmdSug() + Lang.get("Use") + ": /tpHere <" + WarpSystem.opt().cmdArg() + "player" + WarpSystem.opt().cmdSug() + ">");
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                if (Permissions.hasPermission(sender, Permissions.PERMISSION_USE_TELEPORT_COMMAND_TP) || Permissions.hasPermission(sender, Permissions.PERMISSION_USE_TELEPORT_COMMAND_TPHERE)) {
                    sender.sendMessage(Lang.getPrefix() + WarpSystem.opt().cmdSug() + Lang.get("Use") + ": /tpHere <" + WarpSystem.opt().cmdArg() + "player" + WarpSystem.opt().cmdSug() + ">");
                } else noPermission(sender, label, this);
                return true;
            }
        }.setOnlyPlayers(true));

        getBaseComponent().addChild(new MultiCommandComponent() {
            @Override
            public void addArguments(CommandSender sender, String[] args, List<String> suggestions) {
                if (Permissions.hasPermission(sender, Permissions.PERMISSION_USE_TELEPORT_COMMAND_TP) || Permissions.hasPermission(sender, Permissions.PERMISSION_USE_TELEPORT_COMMAND_TPHERE)) {
                    TeleportCommandManager.handler().suggestTpHere(sender, args, suggestions);
                }
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String argument, String[] args) {
                if (Permissions.hasPermission(sender, Permissions.PERMISSION_USE_TELEPORT_COMMAND_TP) || Permissions.hasPermission(sender, Permissions.PERMISSION_USE_TELEPORT_COMMAND_TPHERE)) {
                    Player p = (Player) sender;
                    TeleportCommandManager.handler().tp(p, WarpSystem.getInstance().getPlayerDataManager().getCache(argument), WarpSystem.getInstance().getPlayerDataManager().getCache(p));
                } else getBaseComponent().noPermission(sender, label, this);
                return true;
            }
        });
    }
}

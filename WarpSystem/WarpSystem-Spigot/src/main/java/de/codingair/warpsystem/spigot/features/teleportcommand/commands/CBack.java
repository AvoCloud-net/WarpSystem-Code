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

public class CBack extends WSCommandBuilder {
    public CBack() {
        super("Back", new BaseComponent(Permissions.PERMISSION_USE_TELEPORT_COMMAND_BACK) {
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
                TeleportCommandManager.handler().back((Player) sender);
                return true;
            }
        }.setOnlyPlayers(true));

        getBaseComponent().addChild(new MultiCommandComponent(Permissions.PERMISSION_USE_TELEPORT_COMMAND_BACK_OTHER) {
            @Override
            public void addArguments(CommandSender sender, String[] args, List<String> suggestions) {
                TeleportCommandManager.handler().suggestBack(args, suggestions);
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String argument, String[] args) {
                TeleportCommandManager.handler().back(sender, argument);
                return true;
            }
        });
    }
}

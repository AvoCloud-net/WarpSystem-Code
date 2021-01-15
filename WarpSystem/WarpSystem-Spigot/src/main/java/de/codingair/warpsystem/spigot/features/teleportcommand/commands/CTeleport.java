package de.codingair.warpsystem.spigot.features.teleportcommand.commands;

import de.codingair.codingapi.server.commands.builder.BaseComponent;
import de.codingair.codingapi.server.commands.builder.CommandComponent;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.api.WSCommandBuilder;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.Lang;
import de.codingair.warpsystem.spigot.features.teleportcommand.TeleportCommandManager;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class CTeleport extends WSCommandBuilder {
    public CTeleport() {
        super("Teleport", new BaseComponent(WarpSystem.PERMISSION_USE_TELEPORT_COMMAND_TP) {
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
                sender.sendMessage(Lang.getPrefix() + WarpSystem.opt().cmdSug() + Lang.get("Use") + ": /tp <" + WarpSystem.opt().cmdArg() + "player" + WarpSystem.opt().cmdSug() + "> [" + WarpSystem.opt().cmdArg() + "player" + WarpSystem.opt().cmdSug() + "] §c" + Lang.get("Or") + " " + WarpSystem.opt().cmdSug() + "/tp [" + WarpSystem.opt().cmdArg() + "player" + WarpSystem.opt().cmdSug() + "] <" + WarpSystem.opt().cmdArg() + "x" + WarpSystem.opt().cmdSug() + "> <" + WarpSystem.opt().cmdArg() + "y" + WarpSystem.opt().cmdSug() + "> <" + WarpSystem.opt().cmdArg() + "z" + WarpSystem.opt().cmdSug() + ">");
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                if (!(sender instanceof Player)) return false;

                Player p = (Player) sender;

                try {
                    if (args.length == 1 && args[0].equals("/" + label)) {
                        //HELP
                        p.sendMessage(Lang.getPrefix() + WarpSystem.opt().cmdSug() + Lang.get("Use") + ": /tp <" + WarpSystem.opt().cmdArg() + "player" + WarpSystem.opt().cmdSug() + "> [" + WarpSystem.opt().cmdArg() + "player" + WarpSystem.opt().cmdSug() + "] §c" + Lang.get("Or") + " " + WarpSystem.opt().cmdSug() + "/tp [" + WarpSystem.opt().cmdArg() + "player" + WarpSystem.opt().cmdSug() + "] <" + WarpSystem.opt().cmdArg() + "x" + WarpSystem.opt().cmdSug() + "> <" + WarpSystem.opt().cmdArg() + "y" + WarpSystem.opt().cmdSug() + "> <" + WarpSystem.opt().cmdArg() + "z" + WarpSystem.opt().cmdSug() + ">");
                    } else if ((args.length == 1 && !args[0].isEmpty()) || (args.length == 2 && !args[1].isEmpty())) {
                        //player [to player]
                        if (args.length == 1) {
                            //Teleport sender to 0
                            TeleportCommandManager.handler().tp(p, p.getName(), args[0]);
                        } else {
                            //Teleport 0 to 1
                            TeleportCommandManager.handler().tp(p, args[0], args[1]);
                        }
                    } else if ((args.length == 3 && !args[2].isEmpty()) || (args.length == 4 && !args[3].isEmpty())) {
                        //player to coords

                        double x = 0;
                        double y = 0;
                        double z = 0;

                        if (args.length == 3) {
                            //Teleport sender to coords
                            args[0] = args[0].replace(",", ".");
                            args[1] = args[1].replace(",", ".");
                            args[2] = args[2].replace(",", ".");

                            if (args[0].contains("~")) {
                                x = ((Player) sender).getLocation().getX();
                                args[0] = args[0].replace("~", "");
                            }

                            if (args[1].contains("~")) {
                                y = ((Player) sender).getLocation().getY();
                                args[1] = args[1].replace("~", "");
                            }

                            if (args[2].contains("~")) {
                                z = ((Player) sender).getLocation().getZ();
                                args[2] = args[2].replace("~", "");
                            }

                            if (!args[0].isEmpty()) x += args[0].contains(".") ? Double.parseDouble(args[0]) : Integer.parseInt(args[0]);
                            if (!args[1].isEmpty()) y += args[1].contains(".") ? Double.parseDouble(args[1]) : Integer.parseInt(args[1]);
                            if (!args[2].isEmpty()) z += args[2].contains(".") ? Double.parseDouble(args[2]) : Integer.parseInt(args[2]);

                            TeleportCommandManager.handler().tp(p, p.getName(), x, y, z);
                        } else {
                            //Teleport 0 to coords
                            args[1] = args[1].replace(",", ".");
                            args[2] = args[2].replace(",", ".");
                            args[3] = args[3].replace(",", ".");

                            if (args[1].contains("~")) {
                                x = ((Player) sender).getLocation().getX();
                                args[1] = args[1].replace(",", ".").replace("~", "");
                            }

                            if (args[2].contains("~")) {
                                y = ((Player) sender).getLocation().getY();
                                args[2] = args[2].replace(",", ".").replace("~", "");
                            }

                            if (args[3].contains("~")) {
                                z = ((Player) sender).getLocation().getZ();
                                args[3] = args[3].replace(",", ".").replace("~", "");
                            }

                            if (!args[1].isEmpty()) x += args[1].contains(".") ? Double.parseDouble(args[1]) : Integer.parseInt(args[1]);
                            if (!args[2].isEmpty()) y += args[2].contains(".") ? Double.parseDouble(args[2]) : Integer.parseInt(args[2]);
                            if (!args[3].isEmpty()) z += args[3].contains(".") ? Double.parseDouble(args[3]) : Integer.parseInt(args[3]);

                            TeleportCommandManager.handler().tp(p, args[0], x, y, z);
                        }
                    } else {
                        //HELP
                        p.sendMessage(Lang.getPrefix() + WarpSystem.opt().cmdSug() + Lang.get("Use") + ": /tp <" + WarpSystem.opt().cmdArg() + "player" + WarpSystem.opt().cmdSug() + "> [" + WarpSystem.opt().cmdArg() + "player" + WarpSystem.opt().cmdSug() + "] §c" + Lang.get("Or") + " " + WarpSystem.opt().cmdSug() + "/tp [" + WarpSystem.opt().cmdArg() + "player" + WarpSystem.opt().cmdSug() + "] <" + WarpSystem.opt().cmdArg() + "x" + WarpSystem.opt().cmdSug() + "> <" + WarpSystem.opt().cmdArg() + "y" + WarpSystem.opt().cmdSug() + "> <" + WarpSystem.opt().cmdArg() + "z" + WarpSystem.opt().cmdSug() + ">");
                    }
                } catch (NumberFormatException ex) {
                    //HELP
                    p.sendMessage(Lang.getPrefix() + WarpSystem.opt().cmdSug() + Lang.get("Use") + ": /tp <" + WarpSystem.opt().cmdArg() + "player" + WarpSystem.opt().cmdSug() + "> [" + WarpSystem.opt().cmdArg() + "player" + WarpSystem.opt().cmdSug() + "] §c" + Lang.get("Or") + " " + WarpSystem.opt().cmdSug() + "/tp [" + WarpSystem.opt().cmdArg() + "player" + WarpSystem.opt().cmdSug() + "] <" + WarpSystem.opt().cmdArg() + "x" + WarpSystem.opt().cmdSug() + "> <" + WarpSystem.opt().cmdArg() + "y" + WarpSystem.opt().cmdSug() + "> <" + WarpSystem.opt().cmdArg() + "z" + WarpSystem.opt().cmdSug() + ">");
                }

                return false;
            }
        }.setOnlyPlayers(true), true);

        setOwnTabCompleter((commandSender, command, s, args) -> {
            if (!WarpSystem.hasPermission(commandSender, WarpSystem.PERMISSION_USE_TELEPORT_COMMAND_TP)) {
                return new ArrayList<>();
            }

            if (commandSender instanceof Player) {
                Player p = (Player) commandSender;
                Block b = p.getTargetBlock(null, 10);
                if (b.getType() == XMaterial.COMMAND_BLOCK.parseMaterial()) {
                    return new ArrayList<>();
                }
            }

            return TeleportCommandManager.handler().suggestTp(args, new ArrayList<>());
        });
    }
}

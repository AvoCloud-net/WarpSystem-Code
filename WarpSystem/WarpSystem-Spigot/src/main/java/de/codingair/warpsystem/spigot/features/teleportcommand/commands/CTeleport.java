package de.codingair.warpsystem.spigot.features.teleportcommand.commands;

import de.codingair.codingapi.server.commands.builder.BaseComponent;
import de.codingair.codingapi.server.commands.builder.CommandComponent;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.core.transfer.utils.PlayerData;
import de.codingair.warpsystem.spigot.api.WSCommandBuilder;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.Lang;
import de.codingair.warpsystem.spigot.base.utils.Permissions;
import de.codingair.warpsystem.spigot.features.teleportcommand.TeleportCommandManager;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class CTeleport extends WSCommandBuilder {
    public CTeleport() {
        super("Teleport", new BaseComponent(Permissions.PERMISSION_USE_TELEPORT_COMMAND_TP) {
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

                if (!process(p, args)) {
                    String bracket = WarpSystem.opt().cmdSug();
                    String arg = WarpSystem.opt().cmdArg();

                    // /tp [player] [<player> | [<x> <y> <z>] [<yaw> <pitch>] [<server> [world] | <world>]

                    TextComponent help = new TextComponent(Lang.getPrefix() + WarpSystem.opt().cmdSug() + Lang.get("Use") + ": " + bracket + "/tp <");

                    TextComponent add = new TextComponent(bracket + "[" + arg + "player" + bracket + "]");
                    add.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new net.md_5.bungee.api.chat.BaseComponent[] {
                            new TextComponent(bracket + "/tp <" + arg + "player" + bracket + ">")
                    }));

                    help.addExtra(add);
                    help.addExtra(bracket + " [");

                    add = new TextComponent(bracket + "<" + arg + "player" + bracket + ">");
                    add.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new net.md_5.bungee.api.chat.BaseComponent[] {
                            new TextComponent(bracket + "/tp <" + arg + "player" + bracket + "> <" + arg + "player" + bracket + ">")
                    }));

                    help.addExtra(add);
                    help.addExtra(bracket + " | [");

                    add = new TextComponent(bracket + "<" + arg + "x" + bracket + "> <" + arg + "y" + bracket + "> <" + arg + "z" + bracket + ">");
                    add.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new net.md_5.bungee.api.chat.BaseComponent[] {
                            new TextComponent(bracket + "/tp [" + arg + "player" + bracket + "] <" + arg + "x" + bracket + "> <" + arg + "y" + bracket + "> <" + arg + "z" + bracket + ">")
                    }));

                    help.addExtra(add);
                    help.addExtra(bracket + "] [");

                    add = new TextComponent(bracket + "<" + arg + "yaw" + bracket + "> <" + arg + "pitch" + bracket + ">");
                    add.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new net.md_5.bungee.api.chat.BaseComponent[] {
                            new TextComponent(bracket + "/tp [" + arg + "player" + bracket + "] [<" + arg + "x" + bracket + "> <" + arg + "y" + bracket + "> <" + arg + "z" + bracket + ">] <" + arg + "yaw" + bracket + "> <" + arg + "pitch" + bracket + ">")
                    }));

                    help.addExtra(add);
                    help.addExtra(bracket + "] [");

                    add = new TextComponent(bracket + "<" + arg + "server" + bracket + "> [" + arg + "world" + bracket + "]");
                    add.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new net.md_5.bungee.api.chat.BaseComponent[] {
                            new TextComponent(bracket + "/tp [" + arg + "player" + bracket + "] [<" + arg + "x" + bracket + "> <" + arg + "y" + bracket + "> <" + arg + "z" + bracket + ">] [<" + arg + "yaw" + bracket + "> <" + arg + "pitch" + bracket + ">] <" + arg + "server" + bracket + "> [" + arg + "world" + bracket + "]")
                    }));

                    help.addExtra(add);
                    help.addExtra(bracket + " | ");

                    add = new TextComponent(bracket + "<" + arg + "world" + bracket + ">");
                    add.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new net.md_5.bungee.api.chat.BaseComponent[] {
                            new TextComponent(bracket + "/tp [" + arg + "player" + bracket + "] [<" + arg + "x" + bracket + "> <" + arg + "y" + bracket + "> <" + arg + "z" + bracket + ">] [<" + arg + "yaw" + bracket + "> <" + arg + "pitch" + bracket + ">] <" + arg + "world" + bracket + ">")
                    }));

                    help.addExtra(add);
                    help.addExtra(bracket + "]]");

                    p.spigot().sendMessage(help);
                }
                return true;
            }
        }.setOnlyPlayers(true), true);

        setMergeSpaceArguments(false);
        setOwnTabCompleter((commandSender, command, s, args) -> {
            if (!Permissions.hasPermission(commandSender, Permissions.PERMISSION_USE_TELEPORT_COMMAND_TP)) {
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

    private static boolean process(Player p, String[] args) {
        if (args.length == 0) return false;

        String name = args[0].replace("~", "");
        PlayerData data = null;

        if (!name.isEmpty() && !isNumeric(args[0])) {
            data = WarpSystem.getInstance().getPlayerDataManager().getCache(name);
        }

        if (args.length == 1 && data != null) {
            TeleportCommandManager.handler().tp(p, WarpSystem.getInstance().getPlayerDataManager().getCache(p), data);
            return true;
        }

        if (args.length - 1 >= 1) {
            String other = args[1].replace("~", "");
            if (other.isEmpty() || isNumeric(args[1])) {
                //coordinate
                other = null;
            }

            PlayerData otherData = WarpSystem.getInstance().getPlayerDataManager().getCache(other);

            if (otherData != null) {
                TeleportCommandManager.handler().tp(p, data, otherData);
                return true;
            }
        }

        return process(p, data, args);
    }

    private static boolean process(Player p, PlayerData other, String[] args) {
        int i = 0;
        if (other != null) i++;

        Double x = null, y = null, z = null;

        if (args.length - 1 >= i) {
            //x
            if (args[i].contains("~")) {
                x = p.getLocation().getX();
                args[i] = args[i].replace("~", "");
            }

            if (isNumeric(args[i])) {
                if (x == null) x = parse(args[i]);
                else x += parse(args[i]);
            }

            if (x != null) {
                //y
                if (args.length - 1 >= i + 1) {
                    if (args[i + 1].contains("~")) {
                        y = p.getLocation().getY();
                        args[i + 1] = args[i + 1].replace("~", "");
                    }

                    if (isNumeric(args[i + 1])) {
                        if (y == null) y = parse(args[i + 1]);
                        else y += parse(args[i + 1]);
                    } else if (!args[i + 1].isEmpty()) return false;
                } else return false;

                //z
                if (args.length - 1 >= i + 2) {
                    if (args[i + 2].contains("~")) {
                        z = p.getLocation().getZ();
                        args[i + 2] = args[i + 2].replace("~", "");
                    }

                    if (isNumeric(args[i + 2])) {
                        if (z == null) z = parse(args[i + 2]);
                        else z += parse(args[i + 2]);
                    } else if (!args[i + 2].isEmpty()) {
                        //x and y might be yaw and pitch
                        x = null;
                        y = null;
                    }
                } else {
                    //x and y might be yaw and pitch
                    x = null;
                    y = null;
                }
            }
        }

        return process(p, other, x, y, z, args);
    }

    private static boolean process(Player p, PlayerData other, Double x, Double y, Double z, String[] args) {
        int i = 0;
        if (other != null) i++;
        if (x != null) i += 3;

        Float yaw = null, pitch = null;

        //yaw
        if (args.length - 1 >= i) {
            if (args[i].contains("~")) {
                yaw = p.getLocation().getYaw();
                args[i] = args[i].replace("~", "");
            }

            if (isNumeric(args[i])) {
                if (yaw == null) yaw = (float) parse(args[i]);
                else yaw += (float) parse(args[i]);

                if(yaw > 180) yaw = 180F;
                else if(yaw < -180) yaw = -180F;
            }

            if (yaw != null) {
                //pitch
                if (args.length - 1 >= i + 1) {
                    if (args[i + 1].contains("~")) {
                        pitch = p.getLocation().getPitch();
                        args[i + 1] = args[i + 1].replace("~", "");
                    }

                    if (isNumeric(args[i + 1])) {
                        if (pitch == null) pitch = (float) parse(args[i + 1]);
                        else pitch += (float) parse(args[i + 1]);

                        if(pitch > 90) pitch = 90F;
                        else if(pitch < -90) pitch = -90F;
                    } else if (!args[i + 1].isEmpty()) return false;
                } else return false;
            }
        }

        return process(p, other, x, y, z, yaw, pitch, args);
    }

    private static boolean process(Player p, PlayerData other, Double x, Double y, Double z, Float yaw, Float pitch, String[] args) {
        int i = 0;
        if (other != null) i++;
        if (x != null) i += 3;
        if (yaw != null) i += 2;

        String world = null, server = null;

        if (args.length > i + 2) return false;

        if (args.length - 1 >= i + 1) {
            server = args[i];
            world = args[i + 1];

            if (WarpSystem.getInstance().getServerManager().getProperties(server) == null) {
                p.sendMessage(Lang.getPrefix() + Lang.get("Server_Is_Not_Online"));
                return true;
            } else if (!WarpSystem.getInstance().getServerManager().getWorlds(server).contains(world)) {
                p.sendMessage(Lang.getPrefix() + Lang.get("World_Not_Exists"));
                return true;
            }
        } else if (args.length - 1 >= i) {
            String s = args[i];

            if (WarpSystem.getInstance().getServerManager().getProperties(s) != null) server = s;
            else {
                World w = Bukkit.getWorld(s);
                if (w != null) world = w.getName();
                else {
                    p.sendMessage(Lang.getPrefix() + Lang.get("Player_Server_Or_World_Not_Available"));
                    return true;
                }
            }
        }

        if (other == null) other = WarpSystem.getInstance().getPlayerDataManager().getCache(p);
        return TeleportCommandManager.handler().tp(p, other, x, y, z, yaw, pitch, server, world);
    }

    private static boolean isNumeric(String s) {
        return Pattern.matches("[-+]?\\d+([.,]\\d+)?", s);
    }

    private static double parse(String s) {
        if (s.isEmpty()) return 0;
        s = s.replace(",", ".");

        try {
            if (s.contains(".")) return Double.parseDouble(s);
            else return Integer.parseInt(s);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }
}

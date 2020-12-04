package de.codingair.warpsystem.spigot.features.teleportcommand.commands;

import de.codingair.codingapi.server.commands.builder.BaseComponent;
import de.codingair.codingapi.server.commands.builder.CommandComponent;
import de.codingair.codingapi.server.commands.builder.special.MultiCommandComponent;
import de.codingair.codingapi.tools.Callback;
import de.codingair.codingapi.utils.ChatColor;
import de.codingair.warpsystem.base.features.TeleportTabCompleteKeys;
import de.codingair.warpsystem.base.transfer.packets.spigot.RequestFullNamePacket;
import de.codingair.warpsystem.spigot.api.WSCommandBuilder;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.language.Lang;
import de.codingair.warpsystem.spigot.base.utils.teleport.Origin;
import de.codingair.warpsystem.spigot.features.teleportcommand.TeleportCommandManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CTpa extends WSCommandBuilder {
    public CTpa() {
        super("Tpa", new BaseComponent(WarpSystem.PERMISSION_USE_TELEPORT_COMMAND_TPA) {
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
                sender.sendMessage(Lang.getPrefix() + WarpSystem.opt().cmdSug() + Lang.get("Use") + ": /tpa <" + WarpSystem.opt().cmdArg() + "player" + WarpSystem.opt().cmdSug() + ">");
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage(Lang.getPrefix() + WarpSystem.opt().cmdSug() + Lang.get("Use") + ": /tpa <" + WarpSystem.opt().cmdArg() + "player" + WarpSystem.opt().cmdSug() + ">");
                return false;
            }
        }.setOnlyPlayers(true));

        getBaseComponent().addChild(new MultiCommandComponent() {
            @Override
            public boolean matchTabComplete(CommandSender sender, String suggestion, String argument) {
                return WarpSystem.getInstance().isOnBungeeCord() || super.matchTabComplete(sender, suggestion, argument);
            }

            @Override
            public void addArguments(CommandSender sender, String[] args, List<String> suggestions) {
                Player p = (Player) sender;
                if(WarpSystem.getInstance().isOnBungeeCord()) {
                    suggestions.add(TeleportTabCompleteKeys.ID_TPA);

                    StringBuilder builder = new StringBuilder("tpa");
                    for(String arg : args) {
                        builder.append(" ").append(arg);
                    }
                    suggestions.add(builder.toString());
                } else {
                    for(Player player : Bukkit.getOnlinePlayers()) {
                        if(player.getName().equals(sender.getName()) || !p.canSee(player)) continue;
                        suggestions.add(ChatColor.stripColor(player.getName()));
                    }
                }
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String argument, String[] args) {
                Player other = Bukkit.getPlayer(argument);
                if(other != null && !((Player) sender).canSee(other)) {
                    sender.sendMessage(Lang.getPrefix() + Lang.get("Player_is_not_online"));
                    return false;
                }

                if(argument.equalsIgnoreCase(sender.getName())) {
                    sender.sendMessage(Lang.getPrefix() + Lang.get("TeleportRequest_Cant_Teleport_Yourself"));
                    return false;
                }

                if(WarpSystem.cooldown().checkPlayer((Player) sender, Origin.TeleportRequest)) return false;

                //get original name
                Callback<String> callback = new Callback<String>() {
                    @Override
                    public void accept(String name) {
                        if(name == null) {
                            sender.sendMessage(Lang.getPrefix() + Lang.get("Player_is_not_online"));
                            return;
                        }

                        TeleportCommandManager.getInstance().invite(sender.getName(), false, new Callback<Long>() {
                            @Override
                            public void accept(Long result) {
                                int handled = (int) (result >> 32);
                                int sent = result.intValue();

                                if(handled == 0) sender.sendMessage(Lang.getPrefix() + Lang.get("Player_is_not_online"));
                                else if(handled == -1) sender.sendMessage(Lang.getPrefix() + Lang.get("TeleportRequest_denied_sender").replace("%PLAYER%", ChatColor.stripColor(name)));
                                else if(sent == 0) sender.sendMessage(Lang.getPrefix() + Lang.get("TeleportRequest_already_sent"));
                                else sender.sendMessage(Lang.getPrefix() + Lang.get("TeleportRequest_sent").replace("%PLAYER%", ChatColor.stripColor(name)));
                            }
                        }, name);
                    }
                };

                if(!WarpSystem.getInstance().isOnBungeeCord() || !TeleportCommandManager.getInstance().isBungeeCord() || other != null) {
                    callback.accept(other == null ? argument : other.getName());
                    return false;
                }

                WarpSystem.getInstance().getDataHandler().send(new RequestFullNamePacket(callback, argument));
                return false;
            }
        });
    }
}

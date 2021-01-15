package de.codingair.warpsystem.spigot.features.randomteleports.commands;

import de.codingair.codingapi.player.chat.ChatButton;
import de.codingair.codingapi.player.chat.SimpleMessage;
import de.codingair.codingapi.server.commands.builder.BaseComponent;
import de.codingair.codingapi.server.commands.builder.CommandComponent;
import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.spigot.api.WSCommandBuilder;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.Lang;
import de.codingair.warpsystem.spigot.base.utils.Permissions;
import de.codingair.warpsystem.spigot.base.utils.money.Bank;
import de.codingair.warpsystem.spigot.base.utils.teleport.Origin;
import de.codingair.warpsystem.spigot.features.randomteleports.managers.RandomTeleportManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class CRandomTp extends WSCommandBuilder {
    public CRandomTp() {
        super("RandomTp", new BaseComponent(Permissions.PERMISSION_USE_RANDOM_TELEPORTER) {
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
                runCommand(sender, label, args);
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                String goInfo = "";
                if (Permissions.hasPermission(sender, Permissions.PERMISSION_USE_RANDOM_TELEPORTER_GO)) goInfo = ", go";

                if (sender.hasPermission(Permissions.PERMISSION_MODIFY_RANDOM_TELEPORTER)) {
                    if (RandomTeleportManager.getInstance().isBuyable())
                        sender.sendMessage(Lang.getPrefix() + WarpSystem.opt().cmdSug() + Lang.get("Use") + ": /" + label + " " + WarpSystem.opt().cmdArg() + "<buy, blocks, info" + goInfo + ">");
                    else
                        sender.sendMessage(Lang.getPrefix() + WarpSystem.opt().cmdSug() + Lang.get("Use") + ": /" + label + " " + WarpSystem.opt().cmdArg() + "<blocks, info" + goInfo + ">");
                } else {
                    if (RandomTeleportManager.getInstance().isBuyable())
                        sender.sendMessage(Lang.getPrefix() + WarpSystem.opt().cmdSug() + Lang.get("Use") + ": /" + label + " " + WarpSystem.opt().cmdArg() + "<buy, info" + goInfo + ">");
                    else {
                        sender.sendMessage(Lang.getPrefix() + WarpSystem.opt().cmdSug() + Lang.get("Use") + ": /" + label + " " + WarpSystem.opt().cmdArg() + "<info" + goInfo + ">");
                    }
                }
                return false;
            }
        }.setOnlyPlayers(true));

        if (RandomTeleportManager.getInstance().isBuyable()) {
            getBaseComponent().addChild(new CommandComponent("buy") {
                @Override
                public boolean runCommand(CommandSender sender, String label, String[] args) {
                    //TextComponent

                    if (!RandomTeleportManager.getInstance().canBuy((Player) sender)) {
                        sender.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_Bought_Too_Much").replace("%AMOUNT%", RandomTeleportManager.getInstance().getMaxTeleportAmount((Player) sender) + ""));
                        return false;
                    } else if (RandomTeleportManager.getInstance().getFreeTeleportAmount((Player) sender) == -1) {
                        sender.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_Info_Unlimited"));
                        return false;
                    }

                    double bank = Bank.adapter().getMoney((Player) sender);
                    double costs = RandomTeleportManager.getInstance().getCosts();

                    if (bank >= costs) {
                        SimpleMessage sm = new SimpleMessage(Lang.getPrefix() + Lang.get("RandomTP_Buy").replace("%AMOUNT%", fancyCosts(costs)), WarpSystem.getInstance());

                        sm.replace("%YES%", new ChatButton(Lang.get("RandomTP_Buy_Yes")) {
                            @Override
                            public void onClick(Player player) {
                                sm.destroy();

                                double bank = Bank.adapter().getMoney((Player) sender);

                                if (bank >= costs) {
                                    Bank.adapter().withdraw(player, costs);
                                    UUID u = WarpSystem.getInstance().getPlayerDataManager().get((Player) sender);
                                    RandomTeleportManager.getInstance().setBoughtTeleports(u, RandomTeleportManager.getInstance().getBoughtTeleports(u) + 1);
                                    sender.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_Buy_Finished").replace("%AMOUNT%", fancyCosts(costs)));
                                } else {
                                    sender.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_Not_Enough_Money").replace("%AMOUNT%", fancyCosts(costs)));
                                }
                            }
                        }.setHover(Lang.get("Click_Hover")));

                        sm.replace("%NO%", new ChatButton(Lang.get("RandomTP_Buy_No")) {
                            @Override
                            public void onClick(Player player) {
                                sm.destroy();
                                sender.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_Buy_Cancelled"));
                            }
                        }.setHover(Lang.get("Click_Hover")));

                        sm.send((Player) sender);
                    } else {
                        sender.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_Not_Enough_Money").replace("%AMOUNT%", fancyCosts(costs)));
                    }
                    return false;
                }
            });
        }

        getBaseComponent().addChild(new CommandComponent("blocks", Permissions.PERMISSION_MODIFY_RANDOM_TELEPORTER) {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage(Lang.getPrefix() + WarpSystem.opt().cmdSug() + Lang.get("Use") + ": /" + label + " blocks " + WarpSystem.opt().cmdArg() + "<add>");
                return false;
            }
        });

        getComponent("blocks").addChild(new CommandComponent("add") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                RandomTeleportManager.getInstance().getListener().getAddingNewBlock().add((Player) sender, 30);
                sender.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_Adding_New_Block"));
                return false;
            }
        });

        getComponent("blocks").addChild(new CommandComponent("remove") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_Block_Destroy_Info"));
                return false;
            }
        });

        getBaseComponent().addChild(new CommandComponent("info") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                int free = RandomTeleportManager.getInstance().getFreeTeleportAmount((Player) sender);
                int bought = RandomTeleportManager.getInstance().getBoughtTeleports((Player) sender);
                int teleports = RandomTeleportManager.getInstance().getTeleports((Player) sender);
                int max = RandomTeleportManager.getInstance().getMaxTeleportAmount((Player) sender);

                if (free == -1) {
                    sender.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_Info_Unlimited"));
                } else {
                    sender.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_Info")
                            .replace("%LEFT%", Math.max(free + bought - teleports, 0) + "")
                            .replace("%ALL%", (free + bought) + ""));

                    if (RandomTeleportManager.getInstance().isBuyable()) {
                        if (max == -1) {
                            sender.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_Info_Buyable_Unlimited"));
                        } else {
                            sender.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_Info_Buyable")
                                    .replace("%LEFT%", Math.max(max - free - bought, 0) + "")
                                    .replace("%ALL%", (max - free) + ""));
                        }
                    }
                }
                return false;
            }
        });

        getBaseComponent().addChild(new CommandComponent("go", Permissions.PERMISSION_USE_RANDOM_TELEPORTER_GO) {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(Lang.getPrefix() + WarpSystem.opt().cmdSug() + Lang.get("Use") + ": /" + label + " go " + WarpSystem.opt().cmdArg() + "[server-1, server-2, ...; world-1, world-2, ...] <player>");
                    return false;
                }

                if (WarpSystem.cooldown().checkPlayer((Player) sender, Origin.RandomTP)) return false;

                Player p = (Player) sender;
                RandomTeleportManager.getInstance().tryToTeleport(p.getName(), p.getWorld(), false, new Callback<Integer>() {
                    @Override
                    public void accept(Integer result) {
                        if (result == 0) {
                            WarpSystem.cooldown().register((Player) sender, Origin.RandomTP);
                        }
                    }
                });
                return false;
            }
        }.setOnlyPlayers(false).addChild(new RTP_Go_Command(Permissions.PERMISSION_RANDOM_TELEPORT_SELECTION_SELF)));
    }

    @NotNull
    private String fancyCosts(double costs) {
        return (costs + "").endsWith(".0") ? (costs + "").substring(0, (costs + "").length() - 2) : (costs + "");
    }
}

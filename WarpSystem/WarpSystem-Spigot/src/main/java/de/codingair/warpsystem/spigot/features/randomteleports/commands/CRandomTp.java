package de.codingair.warpsystem.spigot.features.randomteleports.commands;

import de.codingair.codingapi.player.chat.ChatButton;
import de.codingair.codingapi.player.chat.SimpleMessage;
import de.codingair.codingapi.server.commands.builder.BaseComponent;
import de.codingair.codingapi.server.commands.builder.CommandComponent;
import de.codingair.codingapi.server.commands.builder.special.MultiCommandComponent;
import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.spigot.api.WSCommandBuilder;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.Lang;
import de.codingair.warpsystem.spigot.base.utils.Permissions;
import de.codingair.warpsystem.spigot.base.utils.money.Bank;
import de.codingair.warpsystem.spigot.base.utils.teleport.Origin;
import de.codingair.warpsystem.spigot.features.randomteleports.commands.adapters.RtpWorldAdapter;
import de.codingair.warpsystem.spigot.features.randomteleports.managers.RandomTeleportManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Consumer;

public class CRandomTp extends WSCommandBuilder {
    private static final int MAX_OPTIONS_IN_GO_COMMAND = 20;
    private static final RtpGoAdapter[] ADAPTERS = {new RtpWorldAdapter()};

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
                String additional = "";
                if (Permissions.hasPermission(sender, Permissions.PERMISSION_USE_RANDOM_TELEPORTER_GO)) additional += ", go";
                if (Permissions.hasPermission(sender, Permissions.PERMISSION_RANDOM_TELEPORT_SELECTION_OTHER)) additional += ", teleport";

                if (sender.hasPermission(Permissions.PERMISSION_MODIFY_RANDOM_TELEPORTER)) {
                    if (RandomTeleportManager.getInstance().isBuyable())
                        sender.sendMessage(Lang.getPrefix() + WarpSystem.opt().cmdSug() + Lang.get("Use") + ": /" + label + " " + WarpSystem.opt().cmdArg() + "<buy, blocks, info" + additional + ">");
                    else
                        sender.sendMessage(Lang.getPrefix() + WarpSystem.opt().cmdSug() + Lang.get("Use") + ": /" + label + " " + WarpSystem.opt().cmdArg() + "<blocks, info" + additional + ">");
                } else {
                    if (RandomTeleportManager.getInstance().isBuyable())
                        sender.sendMessage(Lang.getPrefix() + WarpSystem.opt().cmdSug() + Lang.get("Use") + ": /" + label + " " + WarpSystem.opt().cmdArg() + "<buy, info" + additional + ">");
                    else {
                        sender.sendMessage(Lang.getPrefix() + WarpSystem.opt().cmdSug() + Lang.get("Use") + ": /" + label + " " + WarpSystem.opt().cmdArg() + "<info" + additional + ">");
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

            getComponent("buy").addChild(new CommandComponent("confirm") {
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
                        Bank.adapter().withdraw((Player) sender, costs);
                        UUID u = WarpSystem.getInstance().getPlayerDataManager().get((Player) sender);
                        RandomTeleportManager.getInstance().setBoughtTeleports(u, RandomTeleportManager.getInstance().getBoughtTeleports(u) + 1);
                        sender.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_Buy_Finished").replace("%AMOUNT%", fancyCosts(costs)));
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
                RandomTeleportManager.getInstance().getListener().setAsAdding((Player) sender);
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

        // general go command (worlds category)
        getBaseComponent().addChild(new CommandComponent("go", Permissions.PERMISSION_USE_RANDOM_TELEPORTER_GO) {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                Player p = (Player) sender;
                if (WarpSystem.cooldown().checkPlayer(p, Origin.RandomTP)) return false;

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
        }.setOnlyPlayers(true));

        // teleport command to rtp others (worlds category)
        getBaseComponent().addChild(new CommandComponent("teleport", Permissions.PERMISSION_RANDOM_TELEPORT_SELECTION_OTHER) {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage(Lang.getPrefix() + WarpSystem.opt().cmdSug() + Lang.get("Use") + ": /" + label + " teleport " + WarpSystem.opt().cmdArg() + "<player> [category]");
                return false;
            }
        }.setOnlyPlayers(false));

        getComponent("teleport").addChild(new MultiCommandComponent() {
            @Override
            public void addArguments(CommandSender commandSender, String[] strings, List<String> list) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    list.add(player.getName());
                }
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String argument, String[] args) {
                Player player = Bukkit.getPlayer(argument);
                if (player == null) {
                    sender.sendMessage(Lang.getPrefix() + Lang.get("Player_is_not_online"));
                    return false;
                }

                RandomTeleportManager.getInstance().tryToTeleport(player.getName(), player.getWorld(), true, new Callback<Integer>() {
                    @Override
                    public void accept(Integer result) {
                        if (!player.getName().equalsIgnoreCase(sender.getName())) {
                            if (result == 0)
                                sender.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_Teleported_Other").replace("%PLAYER%", player.getName()));
                            else if (result == 1)
                                sender.sendMessage(Lang.getPrefix() + Lang.get("Player_is_not_online"));
                            else if (result == 2)
                                sender.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_No_Location_Found"));
                            else if (result == 4)
                                sender.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_Other_No_Teleports_Left").replace("%PLAYER%", player.getName()));
                        } else if (result == 0 && sender instanceof Player) {
                            WarpSystem.cooldown().register((Player) sender, Origin.RandomTP);
                        }

                        if (result == 3) sender.sendMessage(Lang.getPrefix() + Lang.get("Server_Is_Not_Online"));
                    }
                });
                return false;
            }
        });

        for (RtpGoAdapter adapter : ADAPTERS) {
            addAdapter("go", adapter);
            addAdapter("teleport", adapter);
        }
    }

    private void addAdapter(String command, RtpGoAdapter adapter) {
        if (!command.equals("go") && !command.equals("teleport")) {
            throw new IllegalArgumentException("Unknown command: " + command);
        }

        String[] path;
        if (command.equals("teleport")) path = new String[]{command, null};
        else path = new String[]{command};

        // add general
        getComponent(path).addChild(new CommandComponent(adapter.getCategory().toLowerCase(), adapter.getPermission(command)) {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                checkForCooldownAndGetTargetPlayer(sender, label, command, args, targetPlayer -> {
                    List<String> options = adapter.getSortedOptions(sender);
                    int selectedIndex = (int) (Math.random() * options.size());
                    String selected = options.get(selectedIndex);

                    adapter.process(sender, targetPlayer, label, selected);
                });
                return false;
            }
        });

        // add options
        for (int i = 0; i < MAX_OPTIONS_IN_GO_COMMAND; i++) {
            String[] args;
            if (command.equals("go")) {
                // /rtp go
                args = new String[i + 2];
                args[0] = command;
                args[1] = adapter.getCategory().toLowerCase();
            } else {
                // /rtp teleport
                args = new String[i + 3];
                args[0] = command;
                args[1] = null;
                args[2] = adapter.getCategory().toLowerCase();
            }

            CommandComponent go = getComponent(args);
            go.addChild(new MultiCommandComponent() {
                @Override
                public void addArguments(CommandSender commandSender, String[] args, List<String> list) {
                    Set<String> mentioned = new HashSet<>(Arrays.asList(args));
                    List<String> options = adapter.getSortedOptions(commandSender);
                    options.removeAll(mentioned);
                    list.addAll(options);
                }

                @Override
                public boolean runCommand(CommandSender sender, String label, String arg, String[] args) {
                    checkForCooldownAndGetTargetPlayer(sender, label, command, args, targetPlayer -> {
                        // respect "go" and category arguments before actual options
                        int selectedIndex = (int) (Math.random() * (args.length - 2 - (command.equals("teleport") ? 1 : 0)));
                        String selected = args[selectedIndex + 2 + (command.equals("teleport") ? 1 : 0)];

                        adapter.process(sender, targetPlayer, label, selected);
                    });
                    return false;
                }
            });
        }
    }

    @NotNull
    private String fancyCosts(double costs) {
        return (costs + "").endsWith(".0") ? (costs + "").substring(0, (costs + "").length() - 2) : (costs + "");
    }

    private void checkForCooldownAndGetTargetPlayer(CommandSender sender, String label, String command, String[] args, Consumer<String> targetPlayerConsumer) {
        String targetPlayer = null;

        // either use teleport to rtp self or use go
        if (command.equals("go")) {
            if (!(sender instanceof Player)) {
                getBaseComponent().onlyFor(true, sender, label, null);
                return;
            }

            Player player = (Player) sender;

            if (WarpSystem.cooldown().checkPlayer(player, Origin.RandomTP)) return;

            if (!RandomTeleportManager.getInstance().canTeleport(player)) {
                player.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_No_Teleports_Left"));
                return;
            }
        } else targetPlayer = args[1];

        targetPlayerConsumer.accept(targetPlayer);
    }
}

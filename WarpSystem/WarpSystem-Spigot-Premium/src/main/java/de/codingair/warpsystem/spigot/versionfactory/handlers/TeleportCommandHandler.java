package de.codingair.warpsystem.spigot.versionfactory.handlers;

import de.codingair.codingapi.tools.Callback;
import de.codingair.codingapi.utils.ChatColor;
import de.codingair.packetmanagement.packets.impl.LongPacket;
import de.codingair.warpsystem.core.transfer.packets.general.TeleportBackPacket;
import de.codingair.warpsystem.core.transfer.packets.spigot.PrepareTeleportPacket;
import de.codingair.warpsystem.core.transfer.utils.PlayerData;
import de.codingair.warpsystem.core.transfer.utils.TeleportCommandOptions;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.Lang;
import de.codingair.warpsystem.spigot.base.utils.teleport.Origin;
import de.codingair.warpsystem.spigot.base.utils.teleport.TeleportOptions;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.adapters.LocationAdapter;
import de.codingair.warpsystem.spigot.features.teleportcommand.TeleportCommandManager;
import de.codingair.warpsystem.spigot.features.teleportcommand.commands.ITeleportCommandHandler;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class TeleportCommandHandler implements ITeleportCommandHandler {
    public static Number cut(double n) {
        double d = Double.parseDouble(new DecimalFormat("#.##").format(n).replace(",", "."));
        if (d == (int) d) return (int) d;
        else return d;
    }

    @Override
    public void back(Player player) {
        if (WarpSystem.cooldown().checkPlayer(player, Origin.TeleportCommand)) return;
        if (!TeleportCommandManager.getInstance().teleportToLastBackLocation(player)) player.sendMessage(Lang.getPrefix() + Lang.get("No_last_position_found"));
        else WarpSystem.cooldown().register(player, Origin.TeleportCommand);
    }

    @Override
    public void back(CommandSender sender, String player) {
        PlayerData playerData = WarpSystem.getInstance().getPlayerDataManager().getCache(player);
        if (checkStatusBack(sender, playerData)) return;

        if (playerData.getName().equals(sender.getName()) && sender instanceof Player) {
            back((Player) sender);
            return;
        }

        Player p = Bukkit.getPlayerExact(playerData.getName());

        if (p == null) {
            //Proxy
            WarpSystem.getDataHandler().send(new TeleportBackPacket(playerData.getName(), true), null).whenComplete((success, err) -> {
                if (err != null) err.printStackTrace();
                else if (success.getBoolean())
                    sender.sendMessage(Lang.getPrefix() + Lang.get("Teleported_Player_Info").replace("%player%", playerData.getName()).replace("%warp%", Lang.get("Last_Position")));
                else sender.sendMessage(Lang.getPrefix() + Lang.get("No_last_position_found"));
            });
        } else {
            if (!TeleportCommandManager.getInstance().teleportToLastBackLocation(p)) sender.sendMessage(Lang.getPrefix() + Lang.get("No_last_position_found"));
            else {
                WarpSystem.cooldown().register(p, Origin.TeleportCommand);
                sender.sendMessage(Lang.getPrefix() + Lang.get("Teleported_Player_Info").replace("%player%", playerData.getName()).replace("%warp%", Lang.get("Last_Position")));
            }
        }
    }

    private boolean checkStatusBack(CommandSender gate, PlayerData data) {
        if (data == null || (data.getServer() != null && (!TeleportCommandManager.getInstance().isServerAccessible(data.getServer()) || !TeleportCommandManager.getInstance().getServerOptions(data.getServer()).isBack()))) {
            //offline
            gate.sendMessage(Lang.getPrefix() + Lang.get("Player_is_not_online"));
            return true;
        } else return false;
    }

    @Override
    public void suggestBack(String[] args, List<String> suggestions) {
        WarpSystem.getInstance().getPlayerDataManager().getCached().filter(d -> {
            if (Bukkit.getPlayer(d.getName()) != null) return true;
            if (d.getServer() == null) return false;
            TeleportCommandOptions options = TeleportCommandManager.getInstance().getServerOptions(d.getServer());
            return options != null && options.isBack();
        }).forEach(p -> suggestions.add(p.getName()));
    }

    private boolean allNull(Object... o) {
        for (Object o1 : o) {
            if(o1 != null) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean tp(Player gate, PlayerData player, @Nullable Double x, @Nullable Double y, @Nullable Double z, @Nullable Float yaw, @Nullable Float pitch, @Nullable String world, @Nullable String server) {
        if (checkStatusTp(gate, player)) return true;
        Player p = Bukkit.getPlayer(player.getName());

        if(allNull(x, y, z, yaw, pitch, world, server)) return false; //trigger usage

        boolean move = server != null && !server.equalsIgnoreCase(WarpSystem.getInstance().getCurrentServer());

        if (p == null || move) {
            //proxy
            WarpSystem.getDataHandler().send(new PrepareTeleportPacket(gate.getName(), player.getName(), x, y, z, yaw, pitch, server, world), gate).thenAccept(processTeleportResponse(gate, player.getName()));
            return true;
        }

        if (gate != p && TeleportCommandManager.getInstance().deniesForceTps(p)) {
            gate.sendMessage(Lang.getPrefix() + Lang.get("Teleport_denied").replace("%PLAYER%", p.getName()));
            return true;
        }

        StringBuilder destination = new StringBuilder();

        World w;
        if (world != null) {
            w = Bukkit.getWorld(world);

            if (w == null) {
                gate.sendMessage(Lang.getPrefix() + Lang.get("World_Not_Exists"));
                return true;
            }

            destination.append(w.getName());
        } else w = gate.getWorld();

        Location l;
        if (x == null || y == null || z == null) l = w.getSpawnLocation();
        else {
            l = new Location(w, x, y, z);

            if (destination.length() > 0) destination.append(", ");
            destination.append("x: ").append(cut(x)).append(", y: ").append(cut(y)).append(", z: ").append(cut(z));
        }

        if (yaw != null && pitch != null) {
            l.setYaw(yaw);
            l.setPitch(pitch);

            if (destination.length() > 0) destination.append(", ");
            destination.append("yaw: ").append(cut(yaw)).append(", pitch: ").append(cut(pitch));
        }

        if (gate != p) gate.sendMessage(Lang.getPrefix() + Lang.get("Teleported_Player_Info").replace("%player%", p.getName()).replace("%warp%", destination.toString()));
        TeleportOptions options = new TeleportOptions(new Destination(new LocationAdapter(l)), destination.toString(), Origin.TeleportCommand);
        options.setSkip(true);
        options.setMessage(Lang.getPrefix() + (gate == p ? Lang.get("Teleported_To") : Lang.get("Teleported_To_By").replace("%gate%", gate.getName())));

        WarpSystem.getInstance().getTeleportManager().teleport(p, options);
        return true;
    }

    @Override
    public void tp(Player gate, PlayerData player, PlayerData target) {
        if (checkStatusTp(gate, player)) return;
        if (checkStatusTp(gate, target)) return;

        Player playerP = Bukkit.getPlayer(player.getName());
        Player targetP = Bukkit.getPlayer(target.getName());

        if (playerP == null || targetP == null) {
            //try on proxy
            if (WarpSystem.getInstance().isOnProxy() && TeleportCommandManager.getInstance().isProxy()) {
                WarpSystem.getDataHandler().send(new PrepareTeleportPacket(gate.getName(), player.getName(), target.getName()), gate).thenAccept(processTeleportResponse(gate, player.getName()));
            } else gate.sendMessage(Lang.getPrefix() + Lang.get("Player_is_not_online"));
            return;
        }

        if (gate != playerP && TeleportCommandManager.getInstance().deniesForceTps(playerP)) {
            gate.sendMessage(Lang.getPrefix() + Lang.get("Teleport_denied").replace("%PLAYER%", playerP.getName()));
            return;
        }

        if (gate != playerP) gate.sendMessage(Lang.getPrefix() + Lang.get("Teleported_Player_Info").replace("%player%", playerP.getName()).replace("%warp%", targetP.getName()));

        TeleportOptions options = new TeleportOptions(new Destination(new LocationAdapter(targetP.getLocation())), targetP.getName(), Origin.TeleportCommand);
        options.setSkip(true);
        options.setMessage(Lang.getPrefix() + (gate == playerP ? Lang.get("Teleported_To") : Lang.get("Teleported_To_By").replace("%gate%", gate.getName())));

        WarpSystem.getInstance().getTeleportManager().teleport(playerP, options);
    }

    @NotNull
    private Consumer<LongPacket> processTeleportResponse(Player gate, String player) {
        return packet -> {
            PrepareTeleportPacket.Result r = PrepareTeleportPacket.Result.values()[(int) packet.a()];

            if (r == PrepareTeleportPacket.Result.PLAYER_NOT_ONLINE) gate.sendMessage(Lang.getPrefix() + Lang.get("Player_is_not_online"));
            else if (r == PrepareTeleportPacket.Result.TELEPORT_DENIED) gate.sendMessage(Lang.getPrefix() + Lang.get("Teleport_denied").replace("%PLAYER%", player));
            else if (r == PrepareTeleportPacket.Result.SERVER_NOT_ONLINE) gate.sendMessage(Lang.getPrefix() + Lang.get("Server_Is_Not_Online"));
        };
    }

    private boolean checkStatusTp(Player gate, PlayerData data) {
        if (data == null || data.getServer() != null && (!TeleportCommandManager.getInstance().isServerAccessible(data.getServer()) || !TeleportCommandManager.getInstance().getServerOptions(data.getServer()).isTp())) {
            //offline
            gate.sendMessage(Lang.getPrefix() + Lang.get("Player_is_not_online"));
            return true;
        } else return false;
    }

    @Override
    public List<String> suggestTp(String[] args, List<String> suggestions) {
        int deep = args.length - 1;
        if (deep < 0) return suggestions;

        String last = args[deep].toLowerCase().trim();
        int name = isNumeric(args[0]) || !WarpSystem.getInstance().getPlayerDataManager().isPresent(args[0]) ? 0 : 1;

        int server;
        if (name == 0 && !isNumeric(args[0])) server = 0;
        else if (args.length >= 2 && !isNumeric(args[1]) && !WarpSystem.getInstance().getPlayerDataManager().isPresent(args[1])) server = 1;
        else server = -1;

        if (server == -1) {
            if (deep <= 4 + name && (isNumeric(last) || last.isEmpty())) {
                //[<x> <y> <z>] or [<yaw> <pitch>]

                if (deep < 1 + name || isNumeric(args[name])) {
                    if (name == 0 || WarpSystem.getInstance().getPlayerDataManager().isPresent(args[0])) {
                        boolean clean = true;
                        for (int i = name; i < 5 + name && i < args.length; i++) {
                            if (!isNumeric(args[i]) && !args[i].isEmpty()) {
                                clean = false;
                                break;
                            }
                        }

                        if (clean) {
                            if (deep < 4 + name || deep == 4 + name && last.isEmpty()) suggestions.add(suggest(last, "~"));
                            if (deep < 3 + name || deep == 3 + name && last.isEmpty()) suggestions.add(suggest(last, "~ ~"));
                            if (deep < 2 + name || deep == 2 + name && last.isEmpty()) suggestions.add(suggest(last, "~ ~ ~"));
                            if (deep < 1 + name || deep == 1 + name && last.isEmpty()) suggestions.add(suggest(last, "~ ~ ~ ~"));
                            if (deep == name && last.isEmpty()) suggestions.add("~ ~ ~ ~ ~");
                        }
                    }
                }
            }

            if (deep == 0 || deep == 1 && !isNumeric(args[0]) && WarpSystem.getInstance().getPlayerDataManager().isPresent(args[0])) {
                WarpSystem.getInstance().getPlayerDataManager().getCached().filter(suggestTpPredicate()).forEach(e -> suggestions.add(e.getName()));
            }
        }

        if (server == -1 || server == deep) {
            if (deep == 0
                    || deep == 1 && !isNumeric(args[0]) && WarpSystem.getInstance().getPlayerDataManager().isPresent(args[0])
                    || deep == 2 + name && isNumeric(args[name]) && isNumeric(args[1 + name])
                    || deep == 3 + name && isNumeric(args[name]) && isNumeric(args[1 + name]) && isNumeric(args[2 + name])
                    || deep == 5 + name && isNumeric(args[name]) && isNumeric(args[1 + name]) && isNumeric(args[2 + name]) && isNumeric(args[3 + name]) && isNumeric(args[4 + name])) {
                suggestions.addAll(WarpSystem.getInstance().getServerManager().getWorlds().keySet());
                Bukkit.getWorlds().forEach(w -> suggestions.add(w.getName()));
            }
        }

        if (deep == 1 + name) suggestions.addAll(WarpSystem.getInstance().getServerManager().getWorlds(args[name]));
        else if (deep == 4 + name) suggestions.addAll(WarpSystem.getInstance().getServerManager().getWorlds(args[3 + name]));
        else if (deep == 6 + name) suggestions.addAll(WarpSystem.getInstance().getServerManager().getWorlds(args[5 + name]));

        suggestions.removeIf(s -> !s.toLowerCase().startsWith(last));
        return suggestions;
    }

    private String suggest(String last, String s) {
        if (last.isEmpty()) return s;
        else return last + " " + s;
    }

    private boolean isNumeric(String s) {
        if (s.isEmpty()) return false;
        s = s.replace("~", "");
        if (s.isEmpty()) return true;
        return Pattern.matches("[-+]?\\d+[.,]?(\\d+)?", s);
    }

    @NotNull
    private Predicate<PlayerData> suggestTpPredicate() {
        return d -> {
            if (Bukkit.getPlayer(d.getName()) != null) return true;
            if (d.getServer() == null) return false;
            TeleportCommandOptions options = TeleportCommandManager.getInstance().getServerOptions(d.getServer());
            return options != null && options.isTp();
        };
    }

    @Override
    public List<String> suggestTpHere(CommandSender sender, String[] args, List<String> suggestions) {
        WarpSystem.getInstance().getPlayerDataManager().getCached().filter(suggestTpPredicate()).forEach(e -> suggestions.add(e.getName()));
        return suggestions;
    }

    @Override
    public void tpAll(Player player, int alreadyHandled, int alreadySent) {
        if (WarpSystem.getInstance().isOnProxy() && TeleportCommandManager.getInstance().isProxy()) {
            WarpSystem.getDataHandler().send(new PrepareTeleportPacket(player.getName(), null, player.getName()), player).thenAccept(packet -> {
                long result = packet.a();
                int handled = (int) (result >> 32);
                int sent = (int) result;

                player.sendMessage(Lang.getPrefix() + Lang.get("Teleport_all").replace("%AMOUNT%", (alreadySent + sent) + "").replace("%MAX%", (alreadyHandled + handled) + ""));
            });
        } else player.sendMessage(Lang.getPrefix() + Lang.get("Teleport_all").replace("%AMOUNT%", alreadySent + "").replace("%MAX%", alreadyHandled + ""));
    }

    @Override
    public void tpa(Player player, String argument, Player other, boolean tpToSender) {
        PlayerData data = WarpSystem.getInstance().getPlayerDataManager().getCache(argument);

        if (data == null || data.isVanished()) {
            player.sendMessage(Lang.getPrefix() + Lang.get("Player_is_not_online"));
            return;
        }

        TeleportCommandManager.getInstance().invite(player.getName(), tpToSender, new Callback<Long>() {
            @Override
            public void accept(Long result) {
                int handled = (int) (result >> 32);
                int sent = result.intValue();

                if (handled == 0) player.sendMessage(Lang.getPrefix() + Lang.get("Player_is_not_online"));
                else if (handled == -1) player.sendMessage(Lang.getPrefix() + Lang.get("TeleportRequest_denied_sender").replace("%PLAYER%", ChatColor.stripColor(data.getName())));
                else if (sent == 0) player.sendMessage(Lang.getPrefix() + Lang.get("TeleportRequest_already_sent"));
                else player.sendMessage(Lang.getPrefix() + Lang.get("TeleportRequest_sent").replace("%PLAYER%", ChatColor.stripColor(data.getName())));
            }
        }, data.getName());
    }

    @Override
    public void suggestTpa(Player player, String[] args, List<String> suggestions, boolean tpToSender) {
        WarpSystem.getInstance().getPlayerDataManager().getCached().filter(tpToSender ? suggestTpaHerePredicate() : suggestTpaPredicate()).filter(d -> !d.getName().equals(player.getName()) && !d.isVanished()).filter(d -> {
            Player other = Bukkit.getPlayer(d.getName());

            return other == null || player.canSee(other);
        }).forEach(d -> suggestions.add(ChatColor.stripColor(d.getName())));
    }

    @NotNull
    private Predicate<PlayerData> suggestTpaPredicate() {
        return d -> {
            if (d.isVanished()) return false;
            if (Bukkit.getPlayer(d.getName()) != null) return true;
            if (d.getServer() == null) return false;
            TeleportCommandOptions options = TeleportCommandManager.getInstance().getServerOptions(d.getServer());
            return options != null && options.isTpa();
        };
    }

    @NotNull
    private Predicate<PlayerData> suggestTpaHerePredicate() {
        return d -> {
            if (d.isVanished()) return false;
            if (Bukkit.getPlayer(d.getName()) != null) return true;
            if (d.getServer() == null) return false;
            TeleportCommandOptions options = TeleportCommandManager.getInstance().getServerOptions(d.getServer());
            return options != null && options.isTpaHere();
        };
    }

    @Override
    public void tpaAll(Player player) {
        TeleportCommandManager.getInstance().invite(player.getName(), true, new Callback<Long>() {
            @Override
            public void accept(Long result) {
                int handled = (int) (result >> 32);
                int sent = result.intValue();
                player.sendMessage(Lang.getPrefix() + Lang.get("TeleportRequest_All").replace("%RECEIVED%", sent + "").replace("%MAX%", handled + ""));
            }
        }, null);
    }
}

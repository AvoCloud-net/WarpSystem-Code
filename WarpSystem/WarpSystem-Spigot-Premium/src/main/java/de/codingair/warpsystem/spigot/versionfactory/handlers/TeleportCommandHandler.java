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
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

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
            WarpSystem.getDataHandler().send(new TeleportBackPacket(playerData.getName(), true)).whenComplete((success, err) -> {
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
    public List<String> suggestBack(String[] args, List<String> suggestions) {
        WarpSystem.getInstance().getPlayerDataManager().getCached().filter(d -> {
            if (Bukkit.getPlayer(d.getName()) != null) return true;
            if (d.getServer() == null) return false;
            TeleportCommandOptions options = TeleportCommandManager.getInstance().getServerOptions(d.getServer());
            return options != null && options.isBack();
        }).forEach(p -> suggestions.add(p.getName()));
        return suggestions;
    }

    @Override
    public void tp(Player gate, String player, double x, double y, double z) {
        if (checkStatusTp(gate, player)) return;

        Player playerP = Bukkit.getPlayer(player);

        String destination = "x=" + cut(x) + ", y=" + cut(y) + ", z=" + cut(z);

        if (playerP == null) {
            //try on proxy
            if (WarpSystem.getInstance().isOnProxy() && TeleportCommandManager.getInstance().isProxy()) {
                WarpSystem.getDataHandler().send(new PrepareTeleportPacket(gate.getName(), player, x, y, z), gate).thenAccept(processTeleportResponse(gate, player));
            } else gate.sendMessage(Lang.getPrefix() + Lang.get("Player_is_not_online"));
            return;
        }

        if (gate != playerP && TeleportCommandManager.getInstance().deniesForceTps(playerP)) {
            gate.sendMessage(Lang.getPrefix() + Lang.get("Teleport_denied").replace("%PLAYER%", playerP.getName()));
            return;
        }

        if (gate != playerP) gate.sendMessage(Lang.getPrefix() + Lang.get("Teleported_Player_Info").replace("%player%", playerP.getName()).replace("%warp%", destination));

        Location location = playerP.getLocation();
        location.setX(x);
        location.setY(y);
        location.setZ(z);
        location.setYaw(0);
        location.setPitch(0);

        TeleportOptions options = new TeleportOptions(new Destination(new LocationAdapter(location)), destination, Origin.TeleportCommand);
        options.setSkip(true);
        options.setMessage(Lang.getPrefix() + (gate == playerP ? Lang.get("Teleported_To") : Lang.get("Teleported_To_By").replace("%gate%", gate.getName())));

        WarpSystem.getInstance().getTeleportManager().teleport(playerP, options);
    }

    @Override
    public void tp(Player gate, String player, String target) {
        if (checkStatusTp(gate, player)) return;
        if (checkStatusTp(gate, target)) return;

        Player playerP = Bukkit.getPlayer(player);
        Player targetP = Bukkit.getPlayer(target);

        if (playerP == null || targetP == null) {
            //try on proxy
            if (WarpSystem.getInstance().isOnProxy() && TeleportCommandManager.getInstance().isProxy()) {
                WarpSystem.getDataHandler().send(new PrepareTeleportPacket(gate.getName(), player, target), gate).thenAccept(processTeleportResponse(gate, player));
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
            long result = packet.a();
            int handled = (int) (result >> 32);
            int sent = (int) result;

            if (handled == 0) gate.sendMessage(Lang.getPrefix() + Lang.get("Player_is_not_online"));
            else if (sent == 0) gate.sendMessage(Lang.getPrefix() + Lang.get("Teleport_denied").replace("%PLAYER%", player));
        };
    }

    private boolean checkStatusTp(Player gate, String player) {
        PlayerData data = WarpSystem.getInstance().getPlayerDataManager().getCache(player);
        if (data == null || (data.getServer() != null && (!TeleportCommandManager.getInstance().isServerAccessible(data.getServer()) || !TeleportCommandManager.getInstance().getServerOptions(data.getServer()).isTp()))) {
            //offline
            gate.sendMessage(Lang.getPrefix() + Lang.get("Player_is_not_online"));
            return true;
        } else return false;
    }

    @Override
    public List<String> suggestTp(String[] args, List<String> suggestions) {
        int deep = args.length - 1;

        if (args[deep].isEmpty()) {
            if (deep == 1 && Character.isDigit(args[0].charAt(0)) && WarpSystem.getInstance().getPlayerDataManager().getCache(args[0]) == null) return suggestions;
            else if (deep == 0 || deep == 1) WarpSystem.getInstance().getPlayerDataManager().getCached().filter(suggestTpPredicate()).forEach(e -> suggestions.add(e.getName()));
        } else {
            if (deep == 0 || deep == 1) {
                String last = args[deep];
                WarpSystem.getInstance().getPlayerDataManager().getCached().filter(suggestTpPredicate()).filter(e -> e.getName().toLowerCase().startsWith(last.toLowerCase())).forEach(e -> suggestions.add(e.getName()));
            }
        }

        return suggestions;
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

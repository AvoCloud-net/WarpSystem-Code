package de.codingair.warpsystem.spigot.versionfactory.handlers;

import de.codingair.codingapi.tools.Callback;
import de.codingair.codingapi.utils.ChatColor;
import de.codingair.warpsystem.base.transfer.utils.PlayerData;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.Lang;
import de.codingair.warpsystem.spigot.base.utils.teleport.Origin;
import de.codingair.warpsystem.spigot.base.utils.teleport.TeleportOptions;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.Destination;
import de.codingair.warpsystem.spigot.base.utils.teleport.destinations.adapters.LocationAdapter;
import de.codingair.warpsystem.spigot.features.teleportcommand.TeleportCommandManager;
import de.codingair.warpsystem.spigot.features.teleportcommand.commands.ITeleportCommandHandler;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.List;

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
    public void tp(Player gate, String player, double x, double y, double z) {
        if (checkStatusTp(gate, player)) return;

        Player playerP = Bukkit.getPlayer(player);

        String destination = "x=" + cut(x) + ", y=" + cut(y) + ", z=" + cut(z);

        if (playerP == null) {
            gate.sendMessage(Lang.getPrefix() + Lang.get("Player_is_not_online"));
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

        TeleportOptions options = new TeleportOptions(new Destination(new LocationAdapter(location)), destination);
        options.setOrigin(Origin.TeleportCommand);
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
            gate.sendMessage(Lang.getPrefix() + Lang.get("Player_is_not_online"));
            return;
        }

        if (gate != playerP && TeleportCommandManager.getInstance().deniesForceTps(playerP)) {
            gate.sendMessage(Lang.getPrefix() + Lang.get("Teleport_denied").replace("%PLAYER%", playerP.getName()));
            return;
        }

        if (gate != playerP) gate.sendMessage(Lang.getPrefix() + Lang.get("Teleported_Player_Info").replace("%player%", playerP.getName()).replace("%warp%", targetP.getName()));

        TeleportOptions options = new TeleportOptions(new Destination(new LocationAdapter(targetP.getLocation())), targetP.getName());
        options.setOrigin(Origin.TeleportCommand);
        options.setSkip(true);
        options.setMessage(Lang.getPrefix() + (gate == playerP ? Lang.get("Teleported_To") : Lang.get("Teleported_To_By").replace("%gate%", gate.getName())));

        WarpSystem.getInstance().getTeleportManager().teleport(playerP, options);
    }

    private boolean checkStatusTp(Player gate, String player) {
        PlayerData data = WarpSystem.getInstance().getPlayerDataManager().getCache(player);
        if (data == null) {
            //offline
            gate.sendMessage(Lang.getPrefix() + Lang.get("Player_is_not_online"));
            return true;
        } else if (Bukkit.getPlayer(player) == null) {
            TextComponent tc = new TextComponent(Lang.getPrefix() + "§7Teleporting on your entire BungeeCord is a §6premium feature§7!");
            tc.setColor(net.md_5.bungee.api.ChatColor.GRAY);
            Lang.PREMIUM_CHAT(tc, gate, true);
            return true;
        } else return false;
    }

    @Override
    public List<String> suggestTp(String[] args, List<String> suggestions) {
        int deep = args.length - 1;

        if (args[deep].isEmpty()) {
            if (deep == 1 && Character.isDigit(args[0].charAt(0)) && Bukkit.getPlayer(args[0]) == null) return suggestions;
            else if (deep == 0 || deep == 1) Bukkit.getOnlinePlayers().forEach(p -> suggestions.add(p.getName()));
        } else {
            if (deep == 0 || deep == 1) {
                String last = args[deep];
                Bukkit.getOnlinePlayers().stream().filter(e -> e.getName().toLowerCase().startsWith(last.toLowerCase())).forEach(p -> suggestions.add(p.getName()));
            }
        }

        return suggestions;
    }

    @Override
    public List<String> suggestTpHere(CommandSender sender, String[] args, List<String> suggestions) {
        Bukkit.getOnlinePlayers().forEach(p -> suggestions.add(p.getName()));
        return suggestions;
    }

    @Override
    public void tpAll(Player player, int alreadyHandled, int alreadySent) {
        player.sendMessage(Lang.getPrefix() + Lang.get("Teleport_all").replace("%AMOUNT%", alreadySent + "").replace("%MAX%", alreadyHandled + ""));
    }

    @Override
    public void tpa(Player player, String argument, Player other, boolean tpToSender) {
        if (other == null) {
            if (WarpSystem.hasPermission(player, WarpSystem.PERMISSION_USE_TELEPORT_COMMAND_TP) && WarpSystem.getInstance().getPlayerDataManager().getCache(argument) != null) {
                TextComponent tc = new TextComponent(Lang.getPrefix() + "§7Teleporting on your entire BungeeCord is a §6premium feature§7!");
                tc.setColor(net.md_5.bungee.api.ChatColor.GRAY);
                Lang.PREMIUM_CHAT(tc, player, true);
                return;
            }

            player.sendMessage(Lang.getPrefix() + Lang.get("Player_is_not_online"));
            return;
        }

        TeleportCommandManager.getInstance().invite(player.getName(), tpToSender, new Callback<Long>() {
            @Override
            public void accept(Long result) {
                int handled = (int) (result >> 32);
                int sent = result.intValue();

                if (handled == 0) player.sendMessage(Lang.getPrefix() + Lang.get("Player_is_not_online"));
                else if (handled == -1) player.sendMessage(Lang.getPrefix() + Lang.get("TeleportRequest_denied_sender").replace("%PLAYER%", ChatColor.stripColor(other.getName())));
                else if (sent == 0) player.sendMessage(Lang.getPrefix() + Lang.get("TeleportRequest_already_sent"));
                else player.sendMessage(Lang.getPrefix() + Lang.get("TeleportRequest_sent").replace("%PLAYER%", ChatColor.stripColor(other.getName())));
            }
        }, other.getName());
    }

    @Override
    public void suggestTpa(Player player, String[] args, List<String> suggestions, boolean tpToSender) {
        if (WarpSystem.hasPermission(player, WarpSystem.PERMISSION_USE_TELEPORT_COMMAND_TP)) {
            WarpSystem.getInstance().getPlayerDataManager().getCached().filter(d -> !d.getName().equals(player.getName()) && !d.isVanished()).filter(d -> {
                Player other = Bukkit.getPlayer(d.getName());

                return other == null || player.canSee(other);
            }).forEach(d -> suggestions.add(ChatColor.stripColor(d.getName())));
        } else {
            for (Player other : Bukkit.getOnlinePlayers()) {
                if (other.getName().equals(player.getName()) || !player.canSee(other)) continue;
                suggestions.add(ChatColor.stripColor(other.getName()));
            }
        }
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
        }, null, true);
    }
}

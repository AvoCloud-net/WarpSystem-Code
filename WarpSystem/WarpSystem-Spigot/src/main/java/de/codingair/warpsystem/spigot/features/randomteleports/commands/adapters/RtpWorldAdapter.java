package de.codingair.warpsystem.spigot.features.randomteleports.commands.adapters;

import de.codingair.codingapi.tools.Callback;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.Lang;
import de.codingair.warpsystem.spigot.base.utils.Permissions;
import de.codingair.warpsystem.spigot.base.utils.teleport.Origin;
import de.codingair.warpsystem.spigot.features.randomteleports.commands.RtpGoAdapter;
import de.codingair.warpsystem.spigot.features.randomteleports.managers.RandomTeleportManager;
import de.codingair.warpsystem.spigot.versionfactory.VFac;
import de.codingair.warpsystem.spigot.versionfactory.VKey;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class RtpWorldAdapter implements RtpGoAdapter {
    @Override
    public @NotNull String getCategory() {
        return "worlds";
    }

    @Override
    public @Nullable String getPermission(@NotNull String command) {
        if (command.equals("go")) return Permissions.PERMISSION_RANDOM_TELEPORT_SELECTION_SELF;
        else return Permissions.PERMISSION_RANDOM_TELEPORT_SELECTION_OTHER;
    }

    @Override
    public @NotNull Set<String> getOptions(@NotNull CommandSender commandSender) {
        Set<String> options = new HashSet<>();

        if (WarpSystem.getInstance().isProxyConnected()) {
            RandomTeleportManager man = RandomTeleportManager.getInstance();
            for (String server : man.getServer()) {
                if (server.equalsIgnoreCase(WarpSystem.getInstance().getCurrentServer())) continue;

                for (String world : man.getWorlds(server)) {
                    String s = server + "@" + world;
                    options.add(s);
                }
            }
        }

        for (World world : Bukkit.getWorlds()) {
            options.add(world.getName());
        }

        return options;
    }

    @Override
    public void process(@NotNull CommandSender sender, @Nullable String player, @NotNull String label, @NotNull String option) {
        String server = null;
        String world;
        if (option.contains("@")) {
            server = option.substring(0, option.indexOf("@"));

            if (option.indexOf("@") + 1 == option.length()) world = null;
            else world = option.substring(option.indexOf("@") + 1);
        } else world = option;

        if (server != null) {
            if (!WarpSystem.getInstance().isProxyConnected()) {
                sender.sendMessage(Lang.getPrefix() + WarpSystem.opt().cmdSug() + Lang.get("Use") + ": /" + label + " go worlds " + WarpSystem.opt().cmdArg() + "<world> [world2, ...]");
                return;
            }

            if (!server.equalsIgnoreCase(WarpSystem.getInstance().getCurrentServer())) {
                VFac.build(VKey.RTP_Go_Command_Handler, player == null ? sender.getName() : player, server, world, sender);
                return;
            }
        }

        World target = world == null ? null : Bukkit.getWorld(world);

        if (target == null) {
            sender.sendMessage(Lang.getPrefix() + Lang.get("World_Not_Exists"));
            return;
        }

        RandomTeleportManager.getInstance().tryToTeleport(player == null ? sender.getName() : player, target, player != null && !sender.getName().equalsIgnoreCase(player), new Callback<Integer>() {
            @Override
            public void accept(Integer result) {
                if (player != null && !player.equalsIgnoreCase(sender.getName())) {
                    if (result == 0)
                        sender.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_Teleported_Other").replace("%PLAYER%", player));
                    else if (result == 1) sender.sendMessage(Lang.getPrefix() + Lang.get("Player_is_not_online"));
                    else if (result == 2) sender.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_No_Location_Found"));
                    else if (result == 4)
                        sender.sendMessage(Lang.getPrefix() + Lang.get("RandomTP_Other_No_Teleports_Left").replace("%PLAYER%", player));
                } else if (result == 0 && sender instanceof Player) {
                    WarpSystem.cooldown().register((Player) sender, Origin.RandomTP);
                }

                if (result == 3) sender.sendMessage(Lang.getPrefix() + Lang.get("Server_Is_Not_Online"));
            }
        });
    }
}

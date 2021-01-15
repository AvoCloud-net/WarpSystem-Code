package de.codingair.warpsystem.spigot.features.teleportcommand.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public interface ITeleportCommandHandler {
    void back(Player player);

    void tp(Player gate, String player, double x, double y, double z);

    void tp(Player gate, String player, String target);

    List<String> suggestTp(String[] args, List<String> suggestions);

    List<String> suggestTpHere(CommandSender sender, String[] args, List<String> suggestions);

    void tpAll(Player player, int alreadyHandled, int alreadySent);

    void tpa(Player player, String argument, Player other, boolean tpToSender);

    void suggestTpa(Player player, String[] args, List<String> suggestions, boolean tpToSender);

    void tpaAll(Player player);
}

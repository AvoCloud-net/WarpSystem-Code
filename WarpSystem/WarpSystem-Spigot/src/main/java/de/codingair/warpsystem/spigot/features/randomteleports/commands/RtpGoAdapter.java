package de.codingair.warpsystem.spigot.features.randomteleports.commands;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public interface RtpGoAdapter {

    @NotNull String getCategory();

    @Nullable String getPermission(@NotNull String command);

    @NotNull Set<String> getOptions(@NotNull CommandSender commandSender);

    @NotNull
    default List<String> getSortedOptions(@NotNull CommandSender commandSender) {
        List<String> list = new ArrayList<>(getOptions(commandSender));
        list.sort(Comparator.naturalOrder());
        return list;
    }

    void process(@NotNull CommandSender commandSender, @Nullable String player, @NotNull String label, @NotNull String option);

}

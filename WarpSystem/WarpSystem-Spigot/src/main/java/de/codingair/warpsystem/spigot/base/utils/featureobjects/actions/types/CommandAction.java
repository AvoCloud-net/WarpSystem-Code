package de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.types;

import de.codingair.codingapi.server.commands.builder.CommandBuilder;
import de.codingair.codingapi.tools.io.utils.DataMask;
import de.codingair.warpsystem.base.transfer.packets.spigot.PerformCommandOnBungeePacket;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.utils.Lang;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.Action;
import de.codingair.warpsystem.spigot.base.utils.featureobjects.actions.ActionObject;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandAction extends ActionObject<List<String>> {
    public CommandAction(String... command) {
        super(Action.COMMAND, new ArrayList<>(Arrays.asList(command)));
    }

    public CommandAction(List<String> commands) {
        super(Action.COMMAND, commands);
    }

    public CommandAction() {
        super(Action.COMMAND, null);
    }

    @Override
    public void read(String s) {
        try {
            JSONArray json = (JSONArray) new JSONParser().parse(s);
            setValue(json);
        } catch (ParseException e) {
            List<String> commands = new ArrayList<>();
            commands.add(s);
            setValue(commands);
        }
    }

    @Override
    public boolean perform(Player player) {
        for (String command : getValue()) {
            if (command.startsWith("/")) command = command.substring(1);

            String tag = command.contains(" ") ? command.split(" ")[0] : command;

            Command cmd = CommandBuilder.getCommand(tag);

            if (WarpSystem.getInstance().isOnProxy() && cmd == null) {
                WarpSystem.getDataHandler().send(new PerformCommandOnBungeePacket(player.getName(), command), player).thenAccept(packet -> {
                    if (!packet.getBoolean()) player.sendMessage(Lang.getPrefix() + Lang.get("Unknown_Command"));
                });
            } else {
                if (command.contains("%player%"))
                    Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), command.replace("%player%", player.getName()));
                else player.performCommand(command);
            }
        }

        return true;
    }

    @Override
    public boolean read(DataMask d) {
        setValue(d.getList("commands"));
        return true;
    }

    @Override
    public void write(DataMask d) {
        d.put("commands", getValue());
    }

    @Override
    public boolean usable() {
        return getValue() != null;
    }

    @Override
    public CommandAction clone() {
        return new CommandAction(new ArrayList<>(getValue()));
    }
}

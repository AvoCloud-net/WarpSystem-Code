package de.codingair.warpsystem.spigot.versionfactory.objects;

import de.codingair.codingapi.server.commands.builder.BaseComponent;
import de.codingair.codingapi.server.commands.builder.CommandComponent;
import de.codingair.warpsystem.spigot.base.language.Lang;
import org.bukkit.command.CommandSender;

public class CWarpSystem {
    public CWarpSystem(BaseComponent base) {
        base.addChild(new CommandComponent("upgrade") {
            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                Lang.PREMIUM_CHAT_UPGRADE(sender);
                return false;
            }
        });
    }
}

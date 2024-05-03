package de.codingair.warpsystem.spigot.features.spawn.guis;

import de.codingair.codingapi.tools.items.ItemBuilder;
import de.codingair.codingapi.tools.items.XMaterial;
import de.codingair.warpsystem.spigot.base.WarpSystem;
import de.codingair.warpsystem.spigot.base.guis.editor.Backup;
import de.codingair.warpsystem.spigot.base.guis.editor.Editor;
import de.codingair.warpsystem.spigot.base.utils.Lang;
import de.codingair.warpsystem.spigot.features.spawn.guis.pages.POptions;
import de.codingair.warpsystem.spigot.features.spawn.managers.SpawnManager;
import de.codingair.warpsystem.spigot.features.spawn.utils.Spawn;
import org.bukkit.entity.Player;

public class SpawnEditor extends Editor<Spawn> {
    public SpawnEditor(Player p, Spawn spawn) {
        this(p, spawn, spawn.clone());
    }

    private SpawnEditor(Player p, Spawn spawn, Spawn clone) {
        super(p, clone, new Backup<Spawn>(spawn) {
            @Override
            public void applyTo(Spawn clone) {
                if (WarpSystem.getInstance().isProxyConnected() && clone.getUsage().getDisplay().contains("/spawn")) {
                    String server = SpawnManager.getInstance().getSpawnServerCommand();

                    if (server != null && !WarpSystem.getInstance().getCurrentServer().equals(server)) {
                        clone.setUsage(clone.getUsage().getWithoutSpawnCommand());
                    }
                }

                spawn.apply(clone);

                if (WarpSystem.getInstance().isProxyConnected()) {
                    String s = WarpSystem.getInstance().getCurrentServer();

                    String spawnServer = SpawnManager.getInstance().getSpawnServerCommand();
                    String respawnServer = SpawnManager.getInstance().getRespawnServerCommand();


                    if (spawn.getUsage().isBungee()) spawnServer = s;
                    else if (s.equals(spawnServer)) spawnServer = null;

                    if (spawn.getRespawnUsage().isBungee()) respawnServer = s;
                    else if (s.equals(respawnServer)) respawnServer = null;

                    SpawnManager.getInstance().updateGlobalOptions(spawn.getUsage() == Spawn.Usage.GLOBAL_EVERY_PROXY_JOIN, spawnServer, respawnServer, p);
                }
            }

            @Override
            public void cancel(Spawn clone) {
                clone.destroy();
            }
        }, () -> new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE).setHideName(true).getItem(), new POptions(p, clone));
    }

    public static String getMainTitle() {
        return TITLE_COLOR + Lang.get("Spawn");
    }
}

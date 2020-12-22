package de.codingair.warpsystem.bungee.features.spawn.managers;

import de.codingair.codingapi.bungeecord.files.ConfigFile;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.warpsystem.base.transfer.packets.general.SendGlobalSpawnOptionsPacket;
import de.codingair.warpsystem.base.utils.Manager;
import de.codingair.warpsystem.bungee.base.WarpSystem;
import de.codingair.warpsystem.bungee.features.FeatureType;
import de.codingair.warpsystem.bungee.features.spawn.listeners.ServerListener;
import net.md_5.bungee.api.config.ServerInfo;

import java.util.Objects;

public class SpawnManager implements Manager {
    private String spawn, respawn;

    public static SpawnManager getInstance() {
        return WarpSystem.getInstance().getDataManager().getManager(FeatureType.SPAWN);
    }

    @Override
    public boolean load(boolean loader) {
        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("Config");

        this.spawn = file.getConfig().getString("WarpSystem.GlobalSpawnOptions.Spawn", null);
        this.respawn = file.getConfig().getString("WarpSystem.GlobalSpawnOptions.Respawn", null);

        WarpSystem.proxy().getPluginManager().registerListener(WarpSystem.getInstance(), new ServerListener());
        return true;
    }

    @Override
    public void save(boolean saver) {
        ConfigFile file = WarpSystem.getInstance().getFileManager().getFile("Config");

        file.getConfig().set("WarpSystem.GlobalSpawnOptions.Spawn", this.spawn);
        file.getConfig().set("WarpSystem.GlobalSpawnOptions.Respawn", this.respawn);

        file.save();
    }

    @Override
    public void destroy() {
    }

    public void update(ServerInfo sender, String spawn, String respawn) {
        if(!Objects.equals(this.spawn, spawn) || !Objects.equals(this.respawn, respawn)) {
            this.spawn = spawn;
            this.respawn = respawn;
            synchronize(sender);
        }
    }

    public void synchronize(ServerInfo except) {
        WarpSystem.getInstance().getServerManager().getOnlineServer().forEach(serverInfo ->  {
            if(serverInfo.equals(except)) return;
            WarpSystem.getDataHandler().send(getInfoPacket(), serverInfo, Direction.DOWN);
        });
    }

    public SendGlobalSpawnOptionsPacket getInfoPacket() {
        return new SendGlobalSpawnOptionsPacket(this.spawn, this.respawn);
    }

    public String getSpawn() {
        return spawn;
    }

    public String getRespawn() {
        return respawn;
    }
}

package de.codingair.warpsystem.core.proxy.features;

import de.codingair.codingapi.tools.io.utils.DataMask;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.warpsystem.core.proxy.Core;
import de.codingair.warpsystem.core.proxy.utils.Server;
import de.codingair.warpsystem.core.transfer.packets.general.SendGlobalSpawnOptionsPacket;
import de.codingair.warpsystem.core.utils.Manager;

import java.util.Objects;

public abstract class SpawnHandler implements Manager {
    protected boolean spawnServerProxy;
    protected String spawnServerCommand, respawnServerCommand;

    public boolean load(boolean loader, DataMask mask) {
        this.spawnServerProxy = mask.getBoolean("WarpSystem.GlobalSpawnOptions.ProxySpawn", false);
        this.spawnServerCommand = mask.getString("WarpSystem.GlobalSpawnOptions.Spawn", null);
        this.respawnServerCommand = mask.getString("WarpSystem.GlobalSpawnOptions.Respawn", null);
        return true;
    }

    public void save(boolean saver, DataMask mask) {
        mask.put("WarpSystem.GlobalSpawnOptions.ProxySpawn", this.spawnServerProxy);
        mask.put("WarpSystem.GlobalSpawnOptions.Spawn", this.spawnServerCommand);
        mask.put("WarpSystem.GlobalSpawnOptions.Respawn", this.respawnServerCommand);
    }

    @Override
    public void destroy() {
    }

    public void update(Server sender, boolean spawnServerProxy, String spawn, String respawn) {
        if (!Objects.equals(this.spawnServerCommand, spawn) || this.spawnServerProxy != spawnServerProxy || !Objects.equals(this.respawnServerCommand, respawn)) {
            this.spawnServerProxy = spawnServerProxy;
            this.spawnServerCommand = spawn;
            this.respawnServerCommand = respawn;
            synchronize(sender);
        }
    }

    public void synchronize(Server except) {
        Core.getServerManager().getOnlineServer().forEach(serverInfo -> {
            if (serverInfo.equals(except)) return;
            Core.getPlugin().dataHandler().send(getInfoPacket(), serverInfo, Direction.DOWN);
        });
    }

    public SendGlobalSpawnOptionsPacket getInfoPacket() {
        return new SendGlobalSpawnOptionsPacket(this.spawnServerProxy, this.spawnServerCommand, this.respawnServerCommand);
    }

    public boolean isSpawnServerProxy() {
        return spawnServerProxy;
    }

    public String getSpawnServerCommand() {
        return spawnServerCommand;
    }

    public String getRespawnServerCommand() {
        return respawnServerCommand;
    }
}

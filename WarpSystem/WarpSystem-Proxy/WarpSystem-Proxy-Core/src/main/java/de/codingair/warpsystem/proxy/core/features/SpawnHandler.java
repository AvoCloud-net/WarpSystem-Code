package de.codingair.warpsystem.proxy.core.features;

import de.codingair.codingapi.tools.io.utils.DataMask;
import de.codingair.packetmanagement.utils.Direction;
import de.codingair.warpsystem.base.transfer.packets.general.SendGlobalSpawnOptionsPacket;
import de.codingair.warpsystem.base.utils.Manager;
import de.codingair.warpsystem.proxy.core.Core;
import de.codingair.warpsystem.proxy.core.utils.Server;

import java.util.Objects;

public abstract class SpawnHandler implements Manager {
    protected String spawn, respawn;

    public boolean load(boolean loader, DataMask mask) {
        this.spawn = mask.getString("WarpSystem.GlobalSpawnOptions.Spawn", null);
        this.respawn = mask.getString("WarpSystem.GlobalSpawnOptions.Respawn", null);
        return true;
    }

    public void save(boolean saver, DataMask mask) {
        mask.put("WarpSystem.GlobalSpawnOptions.Spawn", this.spawn);
        mask.put("WarpSystem.GlobalSpawnOptions.Respawn", this.respawn);
    }

    @Override
    public void destroy() {
    }

    public void update(Server sender, String spawn, String respawn) {
        if (!Objects.equals(this.spawn, spawn) || !Objects.equals(this.respawn, respawn)) {
            this.spawn = spawn;
            this.respawn = respawn;
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
        return new SendGlobalSpawnOptionsPacket(this.spawn, this.respawn);
    }

    public String getSpawn() {
        return spawn;
    }

    public String getRespawn() {
        return respawn;
    }
}

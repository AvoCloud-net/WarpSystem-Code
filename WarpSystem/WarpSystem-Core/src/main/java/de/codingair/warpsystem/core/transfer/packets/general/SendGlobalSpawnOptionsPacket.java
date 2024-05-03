package de.codingair.warpsystem.core.transfer.packets.general;

import de.codingair.packetmanagement.packets.Packet;
import de.codingair.packetmanagement.utils.ByteMask;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SendGlobalSpawnOptionsPacket implements Packet {
    private Boolean spawnServerProxy;
    private String spawnServerCommand, respawnServerCommand;

    public SendGlobalSpawnOptionsPacket() {
        this.spawnServerProxy = null;
        this.spawnServerCommand = null;
        this.respawnServerCommand = null;
    }

    public SendGlobalSpawnOptionsPacket(boolean spawnServerProxy, String spawnServerCommand, String respawnServerCommand) {
        this.spawnServerProxy = spawnServerProxy;
        this.spawnServerCommand = spawnServerCommand;
        this.respawnServerCommand = respawnServerCommand;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        ByteMask mask = new ByteMask();
        mask.setBit(0, spawnServerProxy != null && spawnServerProxy);
        mask.setBit(1, spawnServerCommand != null);
        mask.setBit(2, respawnServerCommand != null);
        mask.write(out);

        if (this.spawnServerCommand != null) out.writeUTF(this.spawnServerCommand);
        if (this.respawnServerCommand != null) out.writeUTF(this.respawnServerCommand);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        ByteMask mask = new ByteMask();
        mask.read(in);

        this.spawnServerProxy = mask.getBit(0);
        if (mask.getBit(1)) this.spawnServerCommand = in.readUTF();
        if (mask.getBit(2)) this.respawnServerCommand = in.readUTF();
    }

    public boolean getSpawnServerProxy() {
        return spawnServerProxy;
    }

    public String getSpawnServerCommand() {
        return spawnServerCommand;
    }

    public String getRespawnServerCommand() {
        return respawnServerCommand;
    }
}

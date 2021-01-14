package de.codingair.warpsystem.base.transfer.packets.proxy;

import de.codingair.packetmanagement.packets.ResponsePacket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class SendUUIDPacket implements ResponsePacket {
    private byte options;
    private UUID id;

    public SendUUIDPacket() {
    }

    public SendUUIDPacket(UUID id) {
        this.options = (byte) (id == null ? 0 : 1);
        this.id = id;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeByte(this.options);
        if (this.id != null) {
            out.writeLong(this.id.getMostSignificantBits());
            out.writeLong(this.id.getLeastSignificantBits());
        }
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        this.options = in.readByte();
        if ((this.options & 1) == 1) this.id = new UUID(in.readLong(), in.readLong());
    }

    public UUID getId() {
        return id;
    }
}

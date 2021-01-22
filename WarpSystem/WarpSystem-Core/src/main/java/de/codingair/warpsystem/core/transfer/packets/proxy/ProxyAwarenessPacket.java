package de.codingair.warpsystem.core.transfer.packets.proxy;

import de.codingair.packetmanagement.packets.Packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ProxyAwarenessPacket implements Packet {
    private String proxy;

    public ProxyAwarenessPacket() {
    }

    public ProxyAwarenessPacket(String proxy) {
        this.proxy = proxy;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeUTF(this.proxy);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        this.proxy = in.readUTF();
    }

    public String getProxy() {
        return proxy;
    }
}

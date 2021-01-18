package de.codingair.warpsystem.core.transfer.packets.spigot;

import de.codingair.packetmanagement.packets.RequestPacket;
import de.codingair.packetmanagement.packets.impl.StringPacket;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class RequestFullNamePacket implements RequestPacket<StringPacket> {
    private String name; //not the full name

    public RequestFullNamePacket() {
    }

    public RequestFullNamePacket(@NotNull String name) {
        this.name = name;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeUTF(name);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        this.name = in.readUTF();
    }

    public String getName() {
        return name;
    }
}

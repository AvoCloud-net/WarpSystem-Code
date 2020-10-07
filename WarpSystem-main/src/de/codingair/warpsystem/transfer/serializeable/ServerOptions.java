package de.codingair.warpsystem.transfer.serializeable;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ServerOptions implements Serializable {
    private String version;
    private int updateFetching;
    private int maxPlayers;

    private boolean fetched = false;
    private boolean sameVersion = false;

    public ServerOptions() {
    }

    public ServerOptions(String version, int updateFetching, int maxPlayers) {
        this.version = version;
        this.updateFetching = updateFetching;
        this.maxPlayers = maxPlayers;
    }

    @Override
    public void write(DataOutputStream out) throws IOException {
        out.writeUTF(this.version);
        out.writeByte(updateFetching);
        out.writeInt(maxPlayers);
    }

    @Override
    public void read(DataInputStream in) throws IOException {
        this.version = in.readUTF();
        updateFetching = in.readUnsignedByte();
        this.maxPlayers = in.readInt();
    }

    public String getVersion() {
        return version;
    }

    public int getUpdateFetching() {
        return updateFetching;
    }

    public boolean isFetched() {
        return fetched;
    }

    public void setFetched(boolean fetched) {
        this.fetched = fetched;
    }

    public boolean sameVersion() {
        return sameVersion;
    }

    public void setSameVersion(boolean sameVersion) {
        this.sameVersion = sameVersion;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }
}

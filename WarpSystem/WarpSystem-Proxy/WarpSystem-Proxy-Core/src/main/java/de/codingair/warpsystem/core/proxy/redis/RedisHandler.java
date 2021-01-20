package de.codingair.warpsystem.core.proxy.redis;

public abstract class RedisHandler {
    protected final String source;
    protected DataSink sink;

    public RedisHandler(String source) {
        this.source = source;
    }

    public abstract void send(byte[] data, String channel);

    public abstract void registerChannel(String... channel);

    public abstract void unregisterChannel(String... channel);

    public void receive(byte[] data, String source) {
        if (this.source != null && this.source.equals(source)) return;
        sink.receive(data, source);
    }

    public void setSink(DataSink sink) {
        this.sink = sink;
    }

    public String getSource() {
        return source;
    }
}

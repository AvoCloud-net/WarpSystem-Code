package de.codingair.warpsystem.core.proxy.redis;

public abstract class RedisHandler {
    protected final String source;
    protected final String channel;
    protected DataSink sink;

    public RedisHandler(String source, String channel) {
        this.source = source;
        this.channel = channel;
    }

    public abstract void send(byte[] data);

    public abstract void registerChannel();

    public abstract void unregisterChannel();

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

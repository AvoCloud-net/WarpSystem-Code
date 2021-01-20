package de.codingair.warpsystem.core.proxy.redis;

public interface DataSink {
    void receive(byte[] data, String source);
}

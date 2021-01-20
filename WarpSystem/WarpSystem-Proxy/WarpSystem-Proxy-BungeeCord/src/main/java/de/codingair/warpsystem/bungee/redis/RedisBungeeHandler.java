package de.codingair.warpsystem.bungee.redis;

import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import com.imaginarycode.minecraft.redisbungee.events.PubSubMessageEvent;
import de.codingair.warpsystem.core.proxy.redis.RedisHandler;
import de.codingair.warpsystem.core.proxy.transfer.CoreDataHandler;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.*;

public class RedisBungeeHandler extends RedisHandler implements Listener {
    public RedisBungeeHandler() {
        super(RedisBungee.getApi().getServerId(), CoreDataHandler.redisChannel);
    }

    @Override
    public void send(byte[] data) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(stream);

        try {
            out.writeUTF(source);
            out.writeUTF(new String(data));
            RedisBungee.getApi().sendChannelMessage(channel, new String(stream.toByteArray()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void registerChannel() {
        RedisBungee.getApi().registerPubSubChannels(channel);
    }

    @Override
    public void unregisterChannel() {
        RedisBungee.getApi().unregisterPubSubChannels(channel);
    }

    @EventHandler
    public void onPubSub(PubSubMessageEvent e) {
        if(e.getChannel().equals(channel)) {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(e.getMessage().getBytes()));

            try {
                String source = in.readUTF();

                if(source.equals(this.source)) return;

                byte[] data = in.readUTF().getBytes();
                sink.receive(data, source);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}

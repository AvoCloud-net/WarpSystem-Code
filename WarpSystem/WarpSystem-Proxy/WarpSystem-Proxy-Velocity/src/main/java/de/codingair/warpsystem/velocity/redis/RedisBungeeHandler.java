package de.codingair.warpsystem.velocity.redis;

import com.imaginarycode.minecraft.redisbungee.RedisBungeeAPI;
import com.imaginarycode.minecraft.redisbungee.events.PubSubMessageEvent;
import com.velocitypowered.api.event.Subscribe;
import de.codingair.warpsystem.core.proxy.redis.RedisHandler;
import de.codingair.warpsystem.core.proxy.transfer.CoreDataHandler;

import java.io.*;
import java.util.Base64;

public class RedisBungeeHandler extends RedisHandler {
    public RedisBungeeHandler() {
        super(RedisBungeeAPI.getRedisBungeeApi().getProxyId(), CoreDataHandler.redisChannel);
    }

    @Override
    public void send(byte[] data) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(stream);

        try {
            out.writeUTF(source);

            //use Base64 to avoid virtual ends for the packet stream
            byte[] encoded = Base64.getEncoder().encode(data);
            out.writeUTF(new String(encoded));

            RedisBungeeAPI.getRedisBungeeApi().sendChannelMessage(channel, stream.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void registerChannel() {

    }

    @Override
    public void unregisterChannel() {

    }

    @Subscribe
    public void onPubSub(PubSubMessageEvent e) {
        if (e.getChannel().equals(channel)) {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(e.getMessage().getBytes()));

            try {
                String source = in.readUTF();
                if (source.equals(this.source)) return;

                String encoded = in.readUTF();
                byte[] data = Base64.getDecoder().decode(encoded.getBytes());

                sink.receive(data, source);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}

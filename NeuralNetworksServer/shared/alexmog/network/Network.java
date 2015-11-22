package alexmog.network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

import alexmog.network.packets.EntityPacket;
import alexmog.network.packets.HandshakePacket;
import alexmog.network.packets.NewEntityPacket;
import alexmog.network.packets.RemoveEntityPacket;

public class Network {
    public static int port = 42420;
    public static final int udpPort = 42421;
    public static String host = "5.196.135.184";
    public static int version = 0;
    
    public static void register(EndPoint endPoint) {
        Kryo kryo = endPoint.getKryo();
        kryo.register(EntityPacket.class);
        kryo.register(RemoveEntityPacket.class);
        kryo.register(NewEntityPacket.class);
        kryo.register(HandshakePacket.class);
        kryo.register(int[].class);
    }
}


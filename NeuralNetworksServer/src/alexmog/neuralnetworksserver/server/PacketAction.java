package alexmog.neuralnetworksserver.server;

import com.esotericsoftware.kryonet.Connection;

public abstract class PacketAction {
    public abstract void run(Connection connection, Object packet) throws Exception;
    public boolean needLoggedIn() {return false;}
}

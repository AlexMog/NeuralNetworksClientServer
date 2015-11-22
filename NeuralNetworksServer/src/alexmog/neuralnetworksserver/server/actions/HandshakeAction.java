package alexmog.neuralnetworksserver.server.actions;

import com.esotericsoftware.kryonet.Connection;

import alexmog.neuralnetworksserver.Client;
import alexmog.neuralnetworksserver.Main;
import alexmog.neuralnetworksserver.server.PacketAction;

public class HandshakeAction extends PacketAction {

    @Override
    public void run(Connection connection, Object packet) throws Exception {
        Client c = (Client)connection;
        c.setHandshaked(true);
        Main.game.getEntityManager().synchronize(connection);
    }

}

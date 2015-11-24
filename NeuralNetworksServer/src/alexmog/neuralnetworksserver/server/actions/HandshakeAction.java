package alexmog.neuralnetworksserver.server.actions;

import com.esotericsoftware.kryonet.Connection;

import alexmog.network.packets.HandshakePacket;
import alexmog.neuralnetworksserver.Client;
import alexmog.neuralnetworksserver.Main;
import alexmog.neuralnetworksserver.game.Game;
import alexmog.neuralnetworksserver.game.entity.EntityManager;
import alexmog.neuralnetworksserver.server.PacketAction;

public class HandshakeAction extends PacketAction {

    @Override
    public void run(Connection connection, Object packet) throws Exception {
        Client c = (Client)connection;
        c.setHandshaked(true);
        HandshakePacket p = new HandshakePacket();
        p.startTimestamp = Game.startTimestamp;
        p.worldHeight = EntityManager.WORLD_HEIGHT;
        p.worldWidth = EntityManager.WORLD_WIDTH;
        c.sendTCP(p);
        Main.game.getEntityManager().synchronize(connection);
    }

}

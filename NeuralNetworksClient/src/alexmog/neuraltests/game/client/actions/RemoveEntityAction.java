package alexmog.neuraltests.game.client.actions;

import com.esotericsoftware.kryonet.Connection;

import alexmog.network.packets.RemoveEntityPacket;
import alexmog.neuraltests.game.GameScreen;
import alexmog.neuraltests.game.client.PacketAction;
import alexmog.neuraltests.geneticalgorithm.entities.Entity;

public class RemoveEntityAction extends PacketAction {
    @Override
    public void run(Connection connection, Object packet) throws Exception {
        RemoveEntityPacket p = (RemoveEntityPacket)packet;
        
        Entity e = GameScreen.mEntityManager.getEntity(p.id);
        if (e != null) e.setToRemove(true);
    }
}

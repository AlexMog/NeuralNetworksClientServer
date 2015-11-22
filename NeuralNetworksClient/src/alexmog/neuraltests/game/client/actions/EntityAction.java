package alexmog.neuraltests.game.client.actions;

import com.esotericsoftware.kryonet.Connection;

import alexmog.network.packets.EntityPacket;
import alexmog.neuraltests.game.GameScreen;
import alexmog.neuraltests.game.client.PacketAction;
import alexmog.neuraltests.geneticalgorithm.entities.Entity;
import alexmog.neuraltests.geneticalgorithm.entities.LivingEntity;

public class EntityAction extends PacketAction {
    @Override
    public void run(Connection connection, Object packet) throws Exception {
        EntityPacket p = (EntityPacket)packet;
        
        Entity e = GameScreen.mEntityManager.getEntity(p.id);
        if (e != null) {
            e.getShape().setX(p.x);
            e.getShape().setY(p.y);
            if (e instanceof LivingEntity) {
                LivingEntity le = (LivingEntity)e;
                le.setLife(p.actualHp);
                le.setAge(p.age);
                le.setAngle(p.angle);
                le.setColor(p.blue, p.red, p.green);
                le.constructLookingAt(p.lookAt);
                le.setSpikeLength(p.spikeLength);
            }
        }
    }
}

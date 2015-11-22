package alexmog.neuralnetworksserver.server;

import java.util.HashMap;
import java.util.Iterator;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.minlog.Log;

import alexmog.network.packets.HandshakePacket;
import alexmog.neuralnetworksserver.server.actions.HandshakeAction;

@SuppressWarnings("rawtypes")
public class PacketsInterpretator {
    private HashMap<Class, PacketAction> mPackets = new HashMap<Class, PacketAction>();
    
    public PacketsInterpretator() {
        mPackets.put(HandshakePacket.class, new HandshakeAction());
    }
    
    public boolean onPacketReceived(Connection connection, Object packet) {
        Iterator<Class> it = mPackets.keySet().iterator();
        
        while (it.hasNext()) {
            Class item = it.next();
            try {
                if (packet.getClass().isAssignableFrom(item)) {
                    PacketAction a = mPackets.get(item);
//                    AccountConnection c = (AccountConnection)connection;
//                    if (!a.needLoggedIn() || (c.getToken() != null && c.getToken().length() > 0)) {
                    a.run(connection, packet);
//                    } else {
/*                        ErrorPacket p = new ErrorPacket();
                        p.message = "You are not authenticated.";
                        p.status = ErrorType.NOT_AUTHENTICATED;
                        c.sendTCP(p);
                        c.close();
                    }*/
                    return true;
                }
            } catch (Exception e) {
                Log.error("PacketInterpretator", e);
            }
        }
        return false;
    }
}

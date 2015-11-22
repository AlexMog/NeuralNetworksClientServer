package alexmog.neuralnetworksserver.server;

import java.util.Map.Entry;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import alexmog.neuralnetworksserver.Client;
import alexmog.neuralnetworksserver.Main;

public class ServerListener extends Listener {
    public class MyEntry implements Entry<Connection, Object> {
        private Connection mConnection;
        private Object mPacket;
        
        public MyEntry(Connection connection, Object packet) {
            mConnection = connection;
            mPacket = packet;
        }
        
        @Override
        public Connection getKey() {
            return mConnection;
        }

        @Override
        public Object getValue() {
            return mPacket;
        }

        @Override
        public Object setValue(Object value) {
            return mPacket;
        }
        
    }
    
    @Override
    public void connected(Connection c) {
        Main.clients.add((Client)c);
    }
    
    @Override
    public void received(Connection c, Object packet) {
        Main.actions.add(new MyEntry(c, packet));
    }
    
    @Override
    public void disconnected(Connection c) {
        Main.clients.remove(c);
    }
}

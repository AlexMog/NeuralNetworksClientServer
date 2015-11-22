package alexmog.neuralnetworksserver;

import java.io.IOException;
import java.util.Deque;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CopyOnWriteArrayList;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;

import alexmog.network.Network;
import alexmog.neuralnetworksserver.game.Game;
import alexmog.neuralnetworksserver.server.ServerListener;

public class Main {
    public static final Random rand = new Random();
    public static final Deque<ServerListener.MyEntry> actions = new ConcurrentLinkedDeque<>();
    public static final List<Client> clients = new CopyOnWriteArrayList<Client>();
    
    // TODO remove and use multi-game support
    public static final Game game = new Game();
    
    public static void main(String[] args) throws IOException {
        Server server = new Server() {
            @Override
            protected Connection newConnection() {
                return new Client();
            }
        };
        Network.register(server);
        server.start();
        server.addListener(new ServerListener());
        server.bind(Network.port, Network.udpPort);
        
        game.start();
    }
    
    public static void sendTCP(Object packet, boolean needHandshake) {
        for (Client c : clients) {
            if (needHandshake == false || c.isHandshaked()) {
                c.sendTCP(packet);
            }
        }
    }
    
    public static void sendUDP(Object packet, boolean needHandshake) {
        for (Client c : clients) {
            if (needHandshake == false || c.isHandshaked()) {
                c.sendUDP(packet);
            }
        }
    }
}

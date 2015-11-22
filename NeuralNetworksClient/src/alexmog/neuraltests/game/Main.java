package alexmog.neuraltests.game;
import java.io.IOException;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CopyOnWriteArrayList;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.SlickException;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener.ThreadedListener;

import alexmog.network.Network;
import alexmog.neuraltests.game.client.ServerListener;

public class Main {
    public static int WIDTH = 1280;
    public static int HEIGHT = 720;
    public static int FPS = 60;
    public static AppGameContainer app;
    public static Client client;
    public static final Deque<ServerListener.MyEntry> actions = new LinkedList<>();
    public static final Deque<ServerListener.MyEntry> actions2 = new LinkedList<>();

    public static void main(String[] args) throws SlickException, IOException {
        client = new Client(64000, 2048);
        client.start();
        Network.register(client);
        client.addListener(new ThreadedListener(new ServerListener()));
        
        app = new AppGameContainer(new Game("Test"));
        app.setDisplayMode(Main.WIDTH, Main.HEIGHT, false);
        app.setTargetFrameRate(Main.FPS);
        app.setShowFPS(true);
        app.setAlwaysRender(true);
        app.setUpdateOnlyWhenVisible(false);
        app.start();
    }
}

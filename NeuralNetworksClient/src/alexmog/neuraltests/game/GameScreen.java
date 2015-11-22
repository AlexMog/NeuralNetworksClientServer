package alexmog.neuraltests.game;

import java.io.IOException;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;
import org.newdawn.slick.state.GameState;
import org.newdawn.slick.state.StateBasedGame;

import com.esotericsoftware.minlog.Log;

import alexmog.network.Network;
import alexmog.neuraltests.game.client.PacketsInterpretator;
import alexmog.neuraltests.game.client.ServerListener;
import alexmog.neuraltests.geneticalgorithm.entities.EntityManager;

public class GameScreen implements GameState {
/*    public static List<GeneticEntity> mIntelligence = new ArrayList<>();
    List<Food> mFoods = new ArrayList<>();
    GeneticAlgorithm genetic = new GeneticAlgorithm(40, 1);*/
    public static int bestGeneration;
    public static EntityManager mEntityManager = new EntityManager();
    public static int population;
    private PacketsInterpretator mInterpretator = new PacketsInterpretator();
    private Vector2f mCameraPos = new Vector2f();
    private Vector2f mCameraSize = new Vector2f(1, 1);
    
    @Override
    public void init(GameContainer container, StateBasedGame game)
            throws SlickException {
/*        for (int i = 0; i < 150; ++i) {
            Food e = new Food();
            e.getShape().setX(rand.nextInt(Main.WIDTH - 40) + 20);
            e.getShape().setY(rand.nextInt(Main.HEIGHT - 40) + 20);
            mFoods.add(e);
        }
        
        for (int i = 0; i < 50; ++i) {
            IntelligentEvolutiveEntity e = new IntelligentEvolutiveEntity();
            mIntelligence.add(e);
        }*/
    }

    @Override
    public void render(GameContainer container, StateBasedGame game, Graphics g)
            throws SlickException {
        g.translate(mCameraPos.x, mCameraPos.y);
        g.scale(mCameraSize.x, mCameraSize.y);
        mEntityManager.render(container, game, g);
        /*for (GeneticEntity e : mIntelligence) {
            e.render(container, game, g);
        }
        
        for (Food f : mFoods) {
            f.render(container, game, g);
        }*/
        g.scale(1, 1);
        g.translate(-mCameraPos.x, -mCameraPos.y);
        g.setColor(Color.white);
        g.drawString("Best generation: " + bestGeneration, 10, 70);
        g.drawString("Population: " + population, 10, 130);
    }

    @Override
    public void update(GameContainer container, StateBasedGame game, int delta)
            throws SlickException {
        bestGeneration = 0;
        
        if (container.getInput().isKeyDown(Input.KEY_UP)) {
            mCameraPos.y += 1 * delta;
        } else if (container.getInput().isKeyDown(Input.KEY_DOWN)) {
            mCameraPos.y -= 1 * delta;
        }
        if (container.getInput().isKeyDown(Input.KEY_RIGHT)) {
            mCameraPos.x -= 1 * delta;
        } else if (container.getInput().isKeyDown(Input.KEY_LEFT)) {
            mCameraPos.x += 1 * delta;
        }

        // Executing packets
        synchronized(Main.actions) {
            Main.actions2.clear();
            Main.actions2.addAll(Main.actions);
            Main.actions.clear();
        }
        
        while (Main.actions2.size() > 0) {
            ServerListener.MyEntry e = Main.actions2.pop();
            mInterpretator.onPacketReceived(e.getKey(), e.getValue());
        }
        
        mEntityManager.update(container, game, delta);
        population = mEntityManager.getLivingEntitiesNumber();
    }

    @Override
    public void enter(GameContainer container, StateBasedGame game)
            throws SlickException {
        Log.info("Connecting to server...");        
        try {
            Main.client.connect(5000, Network.host, Network.port, Network.udpPort);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void leave(GameContainer container, StateBasedGame game)
            throws SlickException {
        // TODO Auto-generated method stub
        
    }


    @Override
    public void mouseWheelMoved(int change) {
        mCameraSize.set(mCameraSize.x + (float)change / 400, mCameraSize.y + (float)change / 400);
    }

    @Override
    public void mouseClicked(int button, int x, int y, int clickCount) {
        
    }

    @Override
    public void mousePressed(int button, int x, int y) {
    }

    @Override
    public void mouseReleased(int button, int x, int y) {
    }

    @Override
    public void mouseMoved(int oldx, int oldy, int newx, int newy) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void mouseDragged(int oldx, int oldy, int newx, int newy) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setInput(Input input) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean isAcceptingInput() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public void inputEnded() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void inputStarted() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void keyPressed(int key, char c) {

    }
    
    @Override
    public void keyReleased(int key, char c) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void controllerLeftPressed(int controller) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void controllerLeftReleased(int controller) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void controllerRightPressed(int controller) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void controllerRightReleased(int controller) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void controllerUpPressed(int controller) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void controllerUpReleased(int controller) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void controllerDownPressed(int controller) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void controllerDownReleased(int controller) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void controllerButtonPressed(int controller, int button) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void controllerButtonReleased(int controller, int button) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public int getID() {
        // TODO Auto-generated method stub
        return 0;
    }

}

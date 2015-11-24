package alexmog.neuralnetworksserver.game;

import alexmog.neuralnetworksserver.Main;
import alexmog.neuralnetworksserver.game.entity.EntityManager;
import alexmog.neuralnetworksserver.game.entity.Food;
import alexmog.neuralnetworksserver.game.entity.LivingEntity;
import alexmog.neuralnetworksserver.server.PacketsInterpretator;
import alexmog.neuralnetworksserver.server.ServerListener;

public class Game {
    private static final int FPS = 60;
    private static final float WAIT = 1000 / FPS;
    private static float MS_PER_TICK = 10;
    public static final int MAX_LIVING = 50, MIN_LIVING = 20;
    int ticks;
    public static int bestGeneration;
    int deaths;
    private EntityManager mEntityManager = new EntityManager(this);
    private PacketsInterpretator mInterpretator = new PacketsInterpretator();
    private int mPopulation;
    private float lag = 0;

    public void start() {
        long currTicks = System.currentTimeMillis();
        while (true) {
            long prevTicks = currTicks;
            currTicks = System.currentTimeMillis();
            
            // Executing packets
            while (Main.actions.size() > 0) {
                ServerListener.MyEntry e = Main.actions.pop();
                mInterpretator.onPacketReceived(e.getKey(), e.getValue());
            }
            
            bestGeneration = 0;

            long delta = currTicks - prevTicks;
            
            if (delta == 0) continue;
            
            lag += (float)delta;
            
            
            while (lag >= MS_PER_TICK) {
                mEntityManager.update(MS_PER_TICK);
                deaths = 0;
                mPopulation = mEntityManager.getLivingEntitiesNumber();
                
                if (mPopulation < MIN_LIVING) {
                    populateLiving(MIN_LIVING - mPopulation);
                }
                
                if (ticks == 0 || ticks % 100 == 0) {
                    int size = mEntityManager.getAll(Food.class).size();
                    if (size <= 200) {
                        populateFood(300 - size);
                    }
                }
                ticks++;
                lag -= MS_PER_TICK;
            }
            mEntityManager.synchronize();
            if (delta < WAIT) {
                try {
                    Thread.sleep((long) (WAIT - delta));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public EntityManager getEntityManager() {
        return mEntityManager;
    }
    
    private void populateFood(int size) {
        for (int i = 0; i < size; ++i) {
            Food f = new Food();
            f.getShape().setX(Main.rand.nextInt(EntityManager.WORLD_WIDTH - 40) + 20);
            f.getShape().setY(Main.rand.nextInt(EntityManager.WORLD_HEIGHT - 40) + 20);
            mEntityManager.addEntity(f);
        }
    }
    
    private void populateLiving(int size) {
        for (int i = 0; i < size; ++i) {
            mEntityManager.addEntity(new LivingEntity());
        }
    }

    public int getPopulation() {
        return mPopulation;
    }

    public void setPopulation(int i) {
        mPopulation = i;
    }
}

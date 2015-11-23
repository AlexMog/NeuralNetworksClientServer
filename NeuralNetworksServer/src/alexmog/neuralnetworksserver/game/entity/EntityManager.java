package alexmog.neuralnetworksserver.game.entity;

import java.util.ArrayList;
import java.util.List;
import org.newdawn.slick.geom.Rectangle;

import com.esotericsoftware.kryonet.Connection;

import alexmog.network.packets.EntityPacket;
import alexmog.network.packets.NewEntityPacket;
import alexmog.network.packets.RemoveEntityPacket;
import alexmog.neuralnetworksserver.Main;
import alexmog.neuralnetworksserver.game.Game;

public class EntityManager {
    public static final int WORLD_WIDTH = 1000, WORLD_HEIGHT = 1000;
    private List<Entity> mToAdd = new ArrayList<>();
    private List<Entity> mEntities = new ArrayList<>();
    private List<Entity> mCollided = new ArrayList<>();
    private Quadtree mQuadTree = new Quadtree(0, new Rectangle(0, 0, WORLD_WIDTH, WORLD_HEIGHT));
    private int mLivingEntities = 0;
    private Game mGame;
    private int mIds = 1;
    private float mUdpTimeout = 0;
    
    public EntityManager(Game game) {
        mGame = game;
    }
    
    public int getLivingEntitiesNumber() {
        return mLivingEntities;
    }
    
    public void update(float delta) {
        for (Entity e : mToAdd) {
            e.setGame(mGame);
            mEntities.add(e);
            e.setId(mIds++);
            Main.sendTCP(constructEntityDatas(e), true);
        }
        mToAdd.clear();
        
        mQuadTree.clear();
        for (Entity e : mEntities) {
            mQuadTree.insert(e);
        }
        
        mLivingEntities = 0;
        for (int i = 0; i < mEntities.size();) {
            Entity e = mEntities.get(i);

            updateEntity(e, (int)delta);

            if (e.toDelete()) {
                mEntities.remove(e);
                Main.sendTCP(constructDeleteEntity(e), true);
            } else {
                if (e instanceof LivingEntity) {
                    Main.sendUDP(constructSyncDatas(e), true);
                }
                ++i;
                if (e instanceof LivingEntity) {
                    mLivingEntities++;
                }
            }
        }
    }
    
    private void updateEntity(Entity e, int delta) {
        e.update(delta);
        // Collision detection
        mCollided.clear();
        mQuadTree.collideList(mCollided, e);
        for (int x = 0; x < mCollided.size(); ++x) {
            Entity cEntity = mCollided.get(x);
            if (e != cEntity) {
                e.inVision(cEntity);
                if (e.getShape().intersects(cEntity.getShape())) {
                    e.isCollision(cEntity);                    
                }
            }
        }
    }
    
    public void synchronize(Connection c) {
        for (Entity e : mEntities) {
            c.sendTCP(constructEntityDatas(e));
        }
    }
    
    private NewEntityPacket constructEntityDatas(Entity e) {
        NewEntityPacket p = new NewEntityPacket();
        p.id = e.getId();
        p.x = e.getPos().x;
        p.y = e.getPos().y;
        p.isLiving = false;
        if (e instanceof LivingEntity) {
            LivingEntity le = (LivingEntity)e;
            p.isLiving = true;
            p.damages = le.getDamages();
            p.generation = le.getGeneration();
            p.hatchTime = le.getHatchTime();
            p.maxAge = le.getMaxAge();
            p.maxHp = le.getMaxLife();
            p.team = le.getTeam();
        }
        return p;
    }
    
    private EntityPacket constructSyncDatas(Entity e) {
        EntityPacket p = new EntityPacket();
        p.id = e.getId();
        p.x = e.getPos().x;
        p.y = e.getPos().y;
        p.red = e.getRed();
        p.blue = e.getBlue();
        p.green = e.getGreen();
        if (e instanceof LivingEntity) {
            LivingEntity le = (LivingEntity) e;
            p.angle = le.getAngle();
            p.actualHp = (int)le.getLife();
            p.age = le.getAge();
            p.spikeLength = le.getSpikeLength();
            p.lookAt = new int[le.getLookingAt().length];
            int i = 0;
            for (Entity lookedAt : le.getLookingAt()) {
                if (lookedAt != null) p.lookAt[i++] = lookedAt.getId();
            }
        }
        return p;
    }
    
    private RemoveEntityPacket constructDeleteEntity(Entity e) {
        RemoveEntityPacket p = new RemoveEntityPacket();
        p.id = e.getId();
        return p;
    }
    
    public void addEntity(Entity e) {
        mToAdd.add(e);
    }
    
    @SuppressWarnings("unchecked")
    public <T> List<T> getAll(Class<T> c) {
        List<T> ret = new ArrayList<T>();
        
        for (Entity e : mEntities) {
            if (c.isInstance(e)) {
                ret.add((T)e);
            }
        }
        
        return ret;
    }
    
    public <T> void removeAll(Class<T> c) {
        for (int i = 0; i < mEntities.size();) {
            Entity e = mEntities.get(i);
            if (c.isInstance(e)) {
                mEntities.remove(e);
            } else {
                ++i;
            }
        }
    }

    public void addAll(List<GeneticEntity> e) {
        mToAdd.addAll(e);
    }
}

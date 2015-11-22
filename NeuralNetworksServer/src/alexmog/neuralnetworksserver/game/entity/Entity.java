package alexmog.neuralnetworksserver.game.entity;

import java.util.ArrayList;
import java.util.List;

import org.newdawn.slick.geom.Shape;
import org.newdawn.slick.geom.Vector2f;

import alexmog.neuralnetworksserver.game.Game;

public class Entity {
    protected double mRotation;
    protected Shape mShape;
    protected boolean mToDelete = false;
    protected List<Entity> mInVision = new ArrayList<>();
    protected List<Entity> mCollided = new ArrayList<>();
    protected float red = 1, green = 1, blue = 1;
    protected Game mGame;
    protected int mId;
    
    public void update(int delta) {
        mInVision.clear();
        mCollided.clear();
    }
    
    public void setId(int id) {
        mId = id;
    }
    
    public void setGame(Game game) {
        mGame = game;
    }
    
    public void inVision(Entity e) {
        mInVision.add(e);
    }
    
    public Shape getShape() {
        return mShape;
    }
    
    public boolean toDelete() {
        return mToDelete;
    }

    public void isCollision(Entity e) {
        mCollided.add(e);
    }

    public int getId() {
        return mId;
    }

    public Vector2f getPos() {
        return mShape.getLocation();
    }

    public float getRed() {
        return red;
    }
    
    public float getBlue() {
        return blue;
    }
    
    public float getGreen() {
        return green;
    }
}

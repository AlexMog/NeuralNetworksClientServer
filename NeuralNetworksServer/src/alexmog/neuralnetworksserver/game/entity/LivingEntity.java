package alexmog.neuralnetworksserver.game.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.newdawn.slick.geom.Circle;
import org.newdawn.slick.geom.Vector2f;

import alexmog.neuralnetworksserver.Main;
import alexmog.neuralnetworksserver.game.Game;
import alexmog.neuralnetworksserver.game.neuralnetwork.NeuralNetwork;

public class LivingEntity extends GeneticEntity {
    protected static final int INPUT_NUMBER = 20, HIDDEN_LAYER_NUMBER = 200, OUTPUT_NUMBER = 7, MUTATION_CHANCE = 1,
            EVO_POINTS = 100;
    protected NeuralNetwork mBrain = new NeuralNetwork(INPUT_NUMBER, HIDDEN_LAYER_NUMBER, OUTPUT_NUMBER);
    private float angle;
    private Vector2f leftSensor;
    private Vector2f rightSensor;
    private Entity[] mNearestEntities = new Entity[4];
    private float life;
    private int maxLife = 0;
    private int age;
    private int maxAge;
    private float spikeLength;
    private Circle spikeCircle;
    private int team;
    private int generation = 0;
    private int spikeTimeout = 100;
    private int damages = 0;
    private int hatchTime = 0;

    public LivingEntity() {
        mShape = new Circle(Main.rand.nextInt(EntityManager.WORLD_WIDTH), Main.rand.nextInt(EntityManager.WORLD_HEIGHT), 20);
        spikeCircle = new Circle(mShape.getCenterX(), mShape.getCenterY(), 0);
        reset();
    }
    
    public GeneticEntity createChild(GeneticEntity parentB, LivingEntity child) {
        LivingEntity pB = (LivingEntity) parentB;
        double[] weights = mBrain.getWeights();
        double[] weights2 = pB.getBrain().getWeights();
        
        double[] childWeigts = new double[weights.length];
        
        /*for (int i = 0; i < childWeigts.length; ++i) {
            if (GameScreen.rand.nextInt(100) > 50) {
                childWeigts[i] = weights[i];
            } else {
                childWeigts[i] = weights2[i];
            }
        }*/
        
        int crossOverPoint = Main.rand.nextInt(weights.length);

        for (int i = 0; i < crossOverPoint; ++i) {
            childWeigts[i] = weights[i];
        }
        
        for (int i = crossOverPoint; i < weights.length; ++i) {
            childWeigts[i] = weights2[i];
        }
        LivingEntity le = (LivingEntity) parentB;
        
        child.getBrain().setWeights(childWeigts);
        child.getShape().setX(mShape.getX());
        child.getShape().setY(mShape.getY());
        child.generation = (generation > le.generation ? generation : le.generation) + 1;
        
        child.maxLife = Main.rand.nextInt(2) == 0 ? maxLife : le.maxLife;
        child.damages = Main.rand.nextInt(2) == 0 ? damages : le.damages;
        child.hatchTime = Main.rand.nextInt(2) == 0 ? hatchTime : le.hatchTime;
        child.maxAge = Main.rand.nextInt(2) == 0 ? maxAge : le.maxAge;
        
        if (Main.rand.nextInt(100) < MUTATION_CHANCE) {
            mutate();
        }
        
        return child;
    }
    
    @Override
    public GeneticEntity createChild(GeneticEntity parentB) {
        return createChild(parentB, new LivingEntity());
    }
    
    public NeuralNetwork getBrain() {
        return mBrain;
    }

    @Override
    public void mutate() {
        Random rand = new Random();
        double[] weigths = mBrain.getWeights();
        weigths[rand.nextInt(mBrain.getDendritesNum())] = rand.nextDouble();
        mBrain.setWeights(weigths);
    }
    
    private Vector2f extendPoint(Vector2f center, float angle, int length) {
        float rad = (float)(angle * Math.PI / 180);
        return new Vector2f((float)(center.x + (Math.cos(rad) * length)),
                (float)(center.y + (Math.sin(rad) * length)));
    }
    
    private Entity getNearestEntity(List<? extends Entity> entities, Vector2f point, List<? extends Entity> without) {
        Entity ret = null;
        float closest = 320000f;
        for (Entity f : entities) {
            if (team == 1 && !(f instanceof LivingEntity)) continue;
            Vector2f newloc = new Vector2f(f.getShape().getLocation());
            newloc.x += mShape.getWidth() / 2;
            newloc.y += mShape.getHeight() / 2;
            float distance = newloc.distanceSquared(point);
            if (distance < closest && f != this && (without == null || !without.contains(f))) {
                closest = distance;
                ret = f;
            }
        }
        return ret;
    }
    
    private LivingEntity findOtherParent() {
        int biggestAge = 0;
        LivingEntity ret = this;
        
        List<LivingEntity> l = mGame.getEntityManager().getAll(LivingEntity.class);
        for (LivingEntity e : l) {
            if (e == this || e.team != team) continue;
            if (e.age > biggestAge) {
                biggestAge = e.age;
                ret = e;
            }
        }
        return ret;
    }

    public void update(int delta) {
        if (age > maxAge) {
            life = 0;
        }
        if (life > maxLife) {
            life = maxLife;
        }
        if (life <= 0) {
            mToDelete = true;
            red = 0;
            green = 0;
            blue = 0;
            mFitness = 0;
            spikeCircle.setRadius(0);
            return;
        }
        double[] input;
        double[] output;

        life -= 1;
        age++;
        spikeTimeout--;
        
        if (age % hatchTime == 0 && mGame.getPopulation() < Game.MAX_LIVING) {
            mGame.getEntityManager().addEntity(createChild(findOtherParent()));
            mGame.setPopulation(mGame.getPopulation() + 1);
        }
        
        Vector2f newloc = new Vector2f(mShape.getLocation());
        newloc.x += mShape.getWidth() / 2;
        newloc.y += mShape.getHeight() / 2;
        leftSensor = extendPoint(newloc, (float)(angle - 45 - 90), 12);
        rightSensor = extendPoint(newloc, (float)(angle + 45 - 90), 12);
        List<Entity> without = new ArrayList<>();
        mNearestEntities[0] = getNearestEntity(mInVision, leftSensor, without);
        mNearestEntities[1] = getNearestEntity(mInVision, rightSensor, without);
        without.add(mNearestEntities[0]);
        without.add(mNearestEntities[1]);
        mNearestEntities[2] = getNearestEntity(mInVision, leftSensor, without);
        mNearestEntities[3] = getNearestEntity(mInVision, rightSensor, without);

        // set inputs
        input = new double[INPUT_NUMBER];
        input[0] = life / (float)maxLife;
        input[1] = red;
        input[2] = green;
        input[3] = blue;
        int j = 4;
        for (int i = 0; i < mNearestEntities.length; ++i) {
            if (mNearestEntities[i] != null) {
                input[j++] = mNearestEntities[i].getShape().getLocation().distanceSquared((i % 2 == 0 ? rightSensor : rightSensor));
                input[j++] = mNearestEntities[i].red;
                input[j++] = mNearestEntities[i].green;
                input[j++] = mNearestEntities[i].blue;
            }
        }
        
        for (Entity e : mCollided) {
            if (team == 0 && e instanceof Food) {
                e.mToDelete = true;
                mFitness += 15;
                life += 300;
                /*if (life > maxLife) {
                    life = maxLife;
                    mFitness -= 20;
                }*/
            } else if (e instanceof LivingEntity) {
                if (spikeTimeout <= 0) {
                    ((LivingEntity) e).life -= spikeLength * damages;
                    life += spikeLength * (80 * damages / 100);
                    mFitness += 10;
                    spikeTimeout = 50;
                }
/*                GameScreen.mEntityManager.addEntity(createChild((GeneticEntity)e));
                mFitness += 10;
                life -= 1000;
                ((LivingEntity)e).life -= 1000;*/
            }
        }
        
/*        for (Entity e : mInVision) {
            if (e == this) return;
            if (e instanceof LivingEntity && e.getShape().intersects(spikeCircle)) {
                LivingEntity le = (LivingEntity)e;
                if (le.life > 0) {
                    mFitness += le.team == team ? -50 : 50;
                }
                le.life -= 50;
                le.mFitness -= 8;
                if (le.life < 0) le.life = 0;
            }
        }*/
        
/*        if (age >= 5000) {
            GameScreen.mEntityManager.addEntity(createChild(this));
            age = 0;
        }*/
//        nearestFoodRight = (Food) getNearestEntity(foods, rightSensor);
//        nearestFoodLeft = (Food) getNearestEntity(foods, leftSensor);
//        nearestRightBuddy = GameScreen.mIntelligence.get(rightEntityId);
//        nearestLeftBuddy = GameScreen.mIntelligence.get(leftEntityId);
//        double closestFoodRight = nearestFoodRight.getShape().getLocation().distanceSquared(rightSensor);
//        double closestFoodLeft = nearestFoodLeft.getShape().getLocation().distanceSquared(leftSensor);
//        double closestBuddyLeft = nearestLeftBuddy.getShape().getLocation().distanceSquared(leftSensor);
//        double closestBuddyRight = nearestRightBuddy.getShape().getLocation().distanceSquared(rightSensor);
        
//        Food closest = (Food) getNearestEntity(foods, newloc);
        
//        IntelligentEvolutiveEntity nearestBuddy = (IntelligentEvolutiveEntity) getNearestEntity(GameScreen.mIntelligence, newloc);
        
        /*if (team == 0 && closest.getShape().intersects(mShape)) {
            closest.getShape().setX(GameScreen.rand.nextInt(Main.WIDTH - 40) + 20);
            closest.getShape().setY(GameScreen.rand.nextInt(Main.HEIGHT - 40) + 20);
            mFitness += 10;
            life += 100;
        }
        
        if (team != 0 && team != nearestBuddy.team && nearestBuddy.getShape().intersects(mShape)) {
            nearestBuddy.life = 0;
            mFitness += 20;
            life += 200;
        }*/

/*        input[0] = closestFoodLeft;
        input[1] = closestFoodRight;
        input[2] = closestBuddyLeft;
        input[3] = ((IntelligentEvolutiveEntity)nearestLeftBuddy).team;
        input[4] = closestBuddyRight;
        input[5] = ((IntelligentEvolutiveEntity)nearestRightBuddy).team;
        input[6] = team;*/
        
        output = mBrain.process(input);
        
        if (output[0] > output[1]) {
            angle += output[0] * 4;
        } else {
            angle -= output[1] * 4;
        }
        double speed = output[2] * 2;
        life -= output[2];
        red = (float)output[3];
        green = (float)output[4];
        blue = (float)output[5];
        if (team == 1) {
            spikeLength = (float)output[6];
            spikeCircle.setRadius(spikeLength * 40);
        }
        
        float radians = (float)((angle - 90) * Math.PI / 180);
        mShape.setX(mShape.getX() + (float)(Math.cos(radians) * speed));
        mShape.setY(mShape.getY() + (float)(Math.sin(radians) * speed));
        
        if (mShape.getX() > EntityManager.WORLD_WIDTH - mShape.getWidth()) {
            mShape.setX(EntityManager.WORLD_WIDTH - mShape.getWidth());
        } else if (mShape.getX() < 0) {
            mShape.setX(0);
        }
        if (mShape.getY() > EntityManager.WORLD_HEIGHT - mShape.getHeight()) {
            mShape.setY(EntityManager.WORLD_HEIGHT - mShape.getHeight());
        } else if (mShape.getY() < 0) {
            mShape.setY(0);
        }
        spikeCircle.setCenterX(mShape.getCenterX());
        spikeCircle.setCenterY(mShape.getCenterY());
        
        super.update(delta);
    }
        
    public float getLife() {
        return life;
    }

    @Override
    public void reset() {
        mShape.setX(Main.rand.nextInt(EntityManager.WORLD_WIDTH));
        mShape.setY(Main.rand.nextInt(EntityManager.WORLD_HEIGHT));
        team = Main.rand.nextInt(2);
        red = 0;
        green = 1;
        blue = 0;
        if (team == 1) {
            red = 1;
            green = 0;
        }
        hatchTime = 2500;
        maxLife = 3000;
        maxAge = 5200;
        for (int i = 0; i < EVO_POINTS; ++i) {
            int random = Main.rand.nextInt(4);
            if (random == 0) {
                damages += 4;
            } else if (random == 1) {
                hatchTime -= 10;
            } else if (random == 2) {
                maxLife += 30; 
            } else {
                maxAge += 100;
            }
        }
        life = maxLife;
    }

    public float getAngle() {
        return angle;
    }

    public int getAge() {
        return age;
    }

    public float getSpikeLength() {
        return spikeLength;
    }

    public int getDamages() {
        return damages;
    }

    public int getGeneration() {
        return generation;
    }

    public int getHatchTime() {
        return hatchTime;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public int getMaxLife() {
        return maxLife;
    }

    public int getTeam() {
        return team;
    }

    public Entity[] getLookingAt() {
        return mNearestEntities;
    }

}

package alexmog.neuralnetworksserver.game.entity;

import org.newdawn.slick.geom.Circle;

public class Food extends Entity {

    public Food() {
        mShape = new Circle(0, 0, 5);
    }
}

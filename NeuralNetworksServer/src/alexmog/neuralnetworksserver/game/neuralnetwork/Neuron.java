package alexmog.neuralnetworksserver.game.neuralnetwork;

public class Neuron {
    public Dendrite[] mDendrites;
    public double mBias;
    public double mValue;
    public double mDelta;
    
    public class Dendrite {
        public double weight;
    };
}

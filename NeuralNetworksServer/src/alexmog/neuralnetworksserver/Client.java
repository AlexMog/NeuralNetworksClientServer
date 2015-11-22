package alexmog.neuralnetworksserver;

import com.esotericsoftware.kryonet.Connection;

public class Client extends Connection {
    private boolean mHandshaked = false;
    
    public void setHandshaked(boolean b) {
        mHandshaked = b;
    }
    
    public boolean isHandshaked() {
        return mHandshaked;
    }
}

package combitech.sdp.simulator;

import android.swedspot.scs.data.SCSData;

/**
 * Created by nine on 8/5/14.
 */
public interface ReceiveListener {
    void receiveData(int signalID, SCSData data);
}

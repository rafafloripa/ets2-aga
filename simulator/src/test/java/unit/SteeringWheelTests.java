package unit;

import android.swedspot.scs.data.Uint32;
import android.swedspot.sdp.ConnectionStatus;
import android.swedspot.sdp.SubscriptionStatus;
import com.swedspot.vil.keys.HardwareButtonController;
import com.swedspot.vil.keys.HardwareButtonControllerFactory;
import com.swedspot.vil.keys.HardwareButtonListener;
import com.swedspot.vil.keys.Key;
import combitech.sdp.simulator.SimulatorGateway;
import org.junit.Test;

import java.util.ArrayList;

import static acceptancetest.util.Util.staticSimulator;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by nine on 8/13/14.
 */
public class SteeringWheelTests {

    @Test
    public void sendDataToServiceTest() throws InterruptedException {
        // ACT I:
        System.out.println("initializing local variables");
        final int signalID = 514;
        final Uint32 sendData = new Uint32(1);
        final String expectedValue = Key.HOME.name();
        final ArrayList<Key> receiveList = new ArrayList<Key>();
        staticSimulator = new SimulatorGateway();

        HardwareButtonController server = HardwareButtonControllerFactory.getInstance();
        Thread testNodeThread = new Thread(() -> staticSimulator.addAndInitiateNode("localhost", 9899, connectionStatus -> {
            if (connectionStatus == ConnectionStatus.CONNECTED)
                synchronized (this) {
                    notifyAll();
                }
        }, (id ,subscriptionStatus) -> {
            if(subscriptionStatus == SubscriptionStatus.SUBSCRIBED){
                synchronized (this){
                    notifyAll();
                }
            }
        }, null),"Test Node Thread");


        server.addListener(new HardwareButtonListener() {
            @Override public void onPressed(Key key) {
                System.out.println("Server got key: " + key.ordinal() + " named " + key.name());
                receiveList.add(key);
            }

            @Override public void onLongPressed(Key key) {
                System.out.println("Server got long key: " + key.ordinal() + " named " + key.name());
                receiveList.add(key);
            }
        });

        testNodeThread.start();

        synchronized(testNodeThread) {
            testNodeThread.wait(6000);
        }
        // ACT II:
        System.out.println("Providing and sending data");
        staticSimulator.provideSignal(signalID);
        synchronized(testNodeThread) {
            testNodeThread.wait(6000);
        }
        staticSimulator.sendValue(signalID, sendData);
        Thread.sleep(2000);

        // ACT III:
        System.out.println("asserting received values");
        assertNotNull(receiveList.get(0));
        System.out.println("comparing " + expectedValue + " with " + receiveList.get(0).name());
        assertEquals(expectedValue, receiveList.get(0).name());
    }
}

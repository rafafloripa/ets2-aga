package combitech.sdp.dummy;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import android.swedspot.automotiveapi.AutomotiveSignal;
import android.swedspot.automotiveapi.AutomotiveSignalInfo;
import android.swedspot.scs.SCS;
import android.swedspot.scs.SCSDataListener;
import android.swedspot.scs.SCSFactory;
import android.swedspot.scs.data.SCSData;
import android.swedspot.scs.data.Uint8;
import android.swedspot.sdp.SDPFactory;
import android.swedspot.sdp.configuration.Configuration;
import android.swedspot.sdp.observer.SDPGatewayNode;
import android.swedspot.sdp.observer.SDPNode;
import android.swedspot.sdp.routing.SDPNodeEthAddress;

import com.swedspot.automotiveapi.AutomotiveFactory;
import com.swedspot.automotiveapi.AutomotiveListener;
import com.swedspot.automotiveapi.AutomotiveManager;
import com.swedspot.vil.configuration.ConfigurationFactory;
import com.swedspot.vil.distraction.DriverDistractionLevel;
import com.swedspot.vil.distraction.DriverDistractionListener;
import com.swedspot.vil.distraction.LightMode;
import com.swedspot.vil.distraction.StealthMode;
import com.swedspot.vil.policy.AutomotiveCertificate;

public class Server implements Runnable, DriverDistractionListener,
        AutomotiveListener {
    private static final int DRIVER_DISTRACTION_ID = 0x0201;
    private static Thread serverThread;
    final private Lock lock;
    private AutomotiveManager manager;
    private SCS sendNode;
    private boolean isRunning;
    private volatile Map<Integer, List<SCSData>> receivedValues;
    private Map<Integer, SCSData> sentValues;
    private Parser dummyParser;

    public Server() {
        receivedValues = new HashMap<>();
        sentValues = new HashMap<>();
        lock = new ReentrantLock();
        dummyParser = new Parser(this);
    }

    public static void main(String[] args) throws InterruptedException {
        Server dummyServer = new Server();
        dummyServer.start();

        serverThread.join();
        System.out.println("Good Bye!");
        System.exit(1);
    }

    @Override
    public void levelChanged(DriverDistractionLevel level) {
        System.out.println("driver distraction level changed to "
                + level.getLevel());
        lock.lock();
        try {
            List<SCSData> data = receivedValues
                    .get(DRIVER_DISTRACTION_ID);
            if (data == null) {
                data = new LinkedList<>();
            }
            data.add(new Uint8(level.getLevel()));
            receivedValues.put(DRIVER_DISTRACTION_ID, data);

        } finally {
            lock.unlock();
        }
    }

    @Override
    public void lightModeChanged(final LightMode lightMode) {
        // TODO
    }

    @Override
    public void stealthModeChanged(final StealthMode stealthMode) {
        // TODO
    }

    public void timeout(final int signalId) {
        System.out.println("Timeout for: " + signalId);
    }

    public void receive(AutomotiveSignal signal) {
        System.out.println("got: " + signal.getSignalId() + " as a "
                + signal.getData().getDataType());
        lock.lock();
        try {
            List<SCSData> data = receivedValues.get(signal.getSignalId());
            if (data == null) {
                data = new LinkedList<>();
            }
            data.add(signal.getData());
            receivedValues.put(signal.getSignalId(), data);
        } finally {
            lock.unlock();
        }
    }

    public void notAllowed(final int signalId) {
        System.out.println("Not allowed: " + signalId);
    }

    public void startServer() {
        manager = AutomotiveFactory.createAutomotiveManagerInstance(
                new AutomotiveCertificate(new byte[0]), this, this);
    }

    public void startSendNode() {
        SDPNode tmpNode1 = SDPFactory.createNodeInstance();
        SDPNode tmpNode2 = SDPFactory.createNodeInstance();
        SDPNode tmpNode3 = SDPFactory.createNodeInstance();
        SDPGatewayNode gatewayNode1 = SDPFactory.createGatewayServerInstance();
        SDPGatewayNode gatewayNode2 = SDPFactory.createGatewayServerInstance();
        SDPGatewayNode gatewayNode3 = SDPFactory.createGatewayServerInstance();
        gatewayNode1.init(new SDPNodeEthAddress("localhost", 8251), tmpNode1);
        gatewayNode2.init(new SDPNodeEthAddress("localhost", 9898), tmpNode2);
        gatewayNode3.init(new SDPNodeEthAddress("localhost", 9899), tmpNode3);
        Configuration conf = ConfigurationFactory.getConfiguration();
        sendNode = SCSFactory.createSCSInstance(tmpNode1, conf);
        sendNode.setDataListener(new SCSDataListener() {
            @Override
            public void receive(int signalID, SCSData scsData) {
                SCSData data = sentValues.get(signalID);
                if (data == null || !data.getData().equals(scsData.getData())) {
                    System.out.println("got data from: " + signalID);

                }
            }

            @Override
            public SCSData request(int signalID) {
                return sentValues.get(signalID);
            }
        });
        gatewayNode1.start();
        gatewayNode2.start();
        gatewayNode3.start();
    }

    public void shutdownServer() {
        isRunning = false;
        manager = null;
    }

    @Override
    public void run() {
        String[] cmd = new String[] {};
        String cmdLine = "";
        printWelcomeMessage();
        if (System.console() == null) {
            startServer();
            while (true) {
            }
        }
        while (isRunning) {
            System.out.print("\n\nEnter command > ");
            cmdLine = System.console().readLine();
            cmd = cmdLine.split(" ");
            dummyParser.parseCommand(cmd);
        }
    }

    public void printWelcomeMessage() {
        System.out.println("\n\n");
        System.out.println("Welcome to Combitech's SDP server");
        System.out.println("Type help for info on available commands");
    }

    public void unsubscribe(String[] cmd) {
        for (int i = 0; i < cmd.length; i++) {
            try {
                manager.unregister(Integer.parseInt(cmd[i]));
            } catch (NumberFormatException e) {
                System.out
                        .println("One or more of the parameters are not valid integers: "
                                + cmd[i]);
            }
        }
    }

    public void subscribe(String[] cmd) {
        for (int i = 0; i < cmd.length; i++) {
            try {
                manager.register(Integer.parseInt(cmd[i]));
            } catch (NumberFormatException e) {
                System.out
                        .println("One or more of the parameters are not valid integers: "
                                + cmd[i]);
            }
        }
    }

    public void printHelp() {
        String com = "\n\n\t";
        String des = "\t\t";
        System.out.print("\nThe commands are:");

        System.out.println(com + "startServer");
        System.out.println(des
                + "Starts the server listening on port 9898 and 8251");

        System.out.println(com + "subscribe <int> <int> <int> ...");
        System.out
                .println(des
                        + "The server subscribes for the signals provided as the argument");

        System.out.println(com + "unsubscribe <int> <int> <int> ...");
        System.out
                .println(des
                        + "The server unsubscribes for the signals provided as the argument");

        System.out.println(com + "exit");
        System.out.println(des + "The server shuts down and the program exits");
    }

    public void start() {
        serverThread = new Thread(this, "SDP Server Thread");
        isRunning = true;
        serverThread.start();
    }

    public boolean didReceiveValue(int signalID, SCSData value) {
        lock.lock();
        boolean result = false;
        try {
            List<SCSData> data = receivedValues.get(signalID);
            if (data == null) {
                System.out.println(receivedValues.toString());
                return false;
            }
            for (SCSData scsData : data) {
                if (Arrays.equals(scsData.getData(), value.getData())) {
                    result = true;
                }
            }
        } finally {
            lock.unlock();
        }
        return result;
    }

    public Map<Integer, List<SCSData>> getReceivedValues() {
        return receivedValues;
    }

    public AutomotiveManager getManager() {
        return manager;
    }

    public void sendFromManager(int signalID, SCSData data) {
        AutomotiveSignalInfo info = new AutomotiveSignalInfo(
                ConfigurationFactory.getConfiguration().getSignalInformation(
                        signalID));
        manager.send(new AutomotiveSignal(signalID, data, info.getUnit()));
        sentValues.put(signalID, data);
    }

    public void sendFromNode(int signalID, SCSData data) {
        System.out.println("server is sending: " + signalID + " with data "
                + Arrays.toString(data.getData()));
        sendNode.send(signalID, data);
        sentValues.put(signalID, data);
    }

    public void provide(int... signalIDs) {
        for (int id : signalIDs) {
            sendNode.provide(id);
            System.out.println("server is now providing signal: " + id);
        }
    }

    public void unprovide(int... signalIDs) {
        for (int id : signalIDs) {
            sendNode.unprovide(id);
            System.out.println("server no longer provides signal: " + id);
        }
    }

    public SCS getSendNode() {
        return sendNode;
    }
}

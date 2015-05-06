package combitech.sdp.simulator;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantLock;

import android.swedspot.scs.SCS;
import android.swedspot.scs.SCSDataListener;
import android.swedspot.scs.SCSFactory;
import android.swedspot.scs.SCSStatusListener;
import android.swedspot.scs.data.SCSData;
import android.swedspot.sdp.ConnectionStatus;
import android.swedspot.sdp.SDPFactory;
import android.swedspot.sdp.configuration.Configuration;
import android.swedspot.sdp.observer.SDPConnectionListener;
import android.swedspot.sdp.observer.SDPGatewayNode;
import android.swedspot.sdp.observer.SDPNode;
import android.swedspot.sdp.routing.SDPNodeEthAddress;

import com.swedspot.vil.configuration.ConfigurationFactory;
import com.swedspot.vil.configuration.VilConstants;

public class SimulatorGateway {
    private static final int DRIVER_DISTRACTION_LEVEL_DATA_ID = 0x0201;
    private static final int HARDWARE_KEY_ID = 0x0202;
    private static final int LIGHT_MODE_DATA_ID = 0x0203;
    private static final int STEALTH_MODE_DATA_ID = 0x0204;

    private Map<Integer, Integer> provideMap;
    private Map<Integer, SCSData> lastValueSent;
    private List<SDPGatewayNode> simulatorGateways;
    private List<SCS> driverDistractionNodes;
    private List<SCS> hardwareKeyNodes;
    private List<SCS> signalNodes;
    private Map<Integer, LinkedList<SCSData>> receivedData;
    private List<Integer> collectedIds;
    private ReentrantLock lock;
    private Thread countdownThread;
    private boolean isConnected = false;
    private boolean isConnecting = false;

    public SimulatorGateway() {
        simulatorGateways = new LinkedList<>();
        driverDistractionNodes = new LinkedList<>();
        hardwareKeyNodes = new LinkedList<>();
        signalNodes = new LinkedList<>();
        provideMap = new HashMap<>();
        lock = new ReentrantLock();
        lastValueSent = new HashMap<>();
        receivedData = new HashMap<>();
        collectedIds = collectAllIds(false);
    }

    public SimulatorGateway(final boolean debugSignals) {
        simulatorGateways = new LinkedList<>();
        driverDistractionNodes = new LinkedList<>();
        hardwareKeyNodes = new LinkedList<>();
        signalNodes = new LinkedList<>();
        provideMap = new HashMap<>();
        lock = new ReentrantLock();
        lastValueSent = new HashMap<>();
        receivedData = new HashMap<>();
        collectedIds = collectAllIds(debugSignals);
    }

    public static LinkedList<Integer> collectAllIds(final boolean debugSignals) {
        LinkedList<Integer> tmp = new LinkedList<>();
        if (debugSignals) {
            tmp.addAll(new SimulatorConfig()
                    .getConfiguredSignals().keySet());
        } else {
            tmp.addAll(ConfigurationFactory.getConfiguration()
                    .getConfiguredSignals().keySet());
        }
        return tmp;
    }

    private void gotConnected() {
        isConnected = true;
    }

    private void notConnected() {
        isConnected = false;
        lock.lock();
        try {
            for (SDPGatewayNode node : simulatorGateways) {
                node.stop();
            }
            simulatorGateways.clear();
        } finally {
            lock.unlock();
        }
        countdownThread = null;
    }

    public void startCountdown() {
        isConnecting = true;
        lock.lock();
        try {
            if (countdownThread == null) {
                countdownThread = new Thread(() -> {
                    int i = 8;
                    do {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                        }
                        i--;
                    } while (!isConnected && i != 0);
                    if (!isConnected) {
                        notConnected();
                    }
                });
                countdownThread.start();
            }
        } finally {
            isConnecting = false;
            lock.unlock();
        }
    }

    public boolean addAndInitiateNode(String address, int port,
            SDPConnectionListener connectionListener, SCSStatusListener statusListener, ReceiveListener listener) {
        SDPNode tmpNode = SDPFactory.createNodeInstance();
        SDPGatewayNode simulatorGateway = SDPFactory
                .createGatewayClientInstance();
        simulatorGateway.init(new SDPNodeEthAddress(address, port), tmpNode);
        if (connectionListener != null) {
            simulatorGateway.setConnectionListener(status -> {
                if (connectionListener != null) {
                    connectionListener.connectionStatusChanged(status);
                }
                if (status == ConnectionStatus.CONNECTED) {
                    gotConnected();
                }
                if (status == ConnectionStatus.DISCONNECTED) {
                    notConnected();
                }
            });
        }

        simulatorGateway.start();
        Configuration conf = new SimulatorConfig();

        lock.lock();
        try {
            simulatorGateways.add(simulatorGateway);

            SCS node = SCSFactory.createSCSInstance(tmpNode, conf);
            node.setDataListener(new SCSDataListener() {
                @Override
                public void receive(int signalID, SCSData data) {
                    if (listener != null) {
                        listener.receiveData(signalID, data);
                    }
                    lock.lock();
                    try {
                        LinkedList<SCSData> dataList = receivedData.get(signalID);
                        if (dataList != null) {
                            dataList.add(data);
                        } else {
                            dataList = new LinkedList<>();
                            dataList.add(data);
                            receivedData.put(signalID, dataList);
                        }
                    } finally {
                        lock.unlock();
                    }
                }

                @Override
                public SCSData request(int signalID) {
                    SCSData data = null;
                    lock.lock();
                    try {
                        data = lastValueSent.get(signalID);
                    } finally {
                        lock.unlock();
                    }
                    return data;
                }
            });
            if (statusListener != null) {
                node.setStatusListener(statusListener);
            }

            if (port == VilConstants.DRIVER_DISTRACTION_PORT) {
                driverDistractionNodes.add(node);
            } else if (port == VilConstants.HARDWARE_BUTTON_PORT) {
                hardwareKeyNodes.add(node);
            } else {
                subscribeForAll(node);
                signalNodes.add(node);
            }
        } finally {
            lock.unlock();
        }
        return true;
    }

    public boolean addAndInitiateNode(String address, int port,
            SDPConnectionListener connectionListener, ReceiveListener listener) {
        return addAndInitiateNode(address, port, connectionListener, null, listener);
    }

    /**
     * The Simulator provides on all signalNodes a signal id
     *
     * @param signalID
     */
    public void provideSignal(int signalID) {
        // System.out.println("SimulatorGateway is providing signal; " +
        // signalID);
        lock.lock();
        try {
            if (!provideMap.containsKey(signalID)) {
                provideMap.put(signalID, 1);
                if (signalID == DRIVER_DISTRACTION_LEVEL_DATA_ID || signalID == LIGHT_MODE_DATA_ID || signalID == STEALTH_MODE_DATA_ID) {
                    for (SCS node : driverDistractionNodes) {
                        node.unsubscribe(signalID);
                        node.provide(signalID);
                        // System.out.println("Providing signal " + signalID +
                        // " on driver distraction node");
                    }
                } else if (signalID == HARDWARE_KEY_ID) {
                    for (SCS node : hardwareKeyNodes) {
                        node.unsubscribe(signalID);
                        node.provide(signalID);
                        // System.out.println("Providing signal " + signalID +
                        // " on hardware node");
                    }
                } else {
                    for (SCS node : signalNodes) {
                        node.unsubscribe(signalID);
                        node.provide(signalID);
                        // System.out.println("Providing signal " + signalID +
                        // " on data node");
                    }
                    // System.out.println("providing: " + signalID
                    // + " on signalNodes");
                }
            } else {
                provideMap.put(signalID, provideMap.get(signalID) + 1);
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * The Simulator unprovides on all signalNodes a signal id
     *
     * @param signalID
     */
    public void unprovideSignal(int signalID) {
        lock.lock();
        try {
            if (provideMap.containsKey(signalID)) {
                if (signalID == DRIVER_DISTRACTION_LEVEL_DATA_ID || signalID == LIGHT_MODE_DATA_ID || signalID == STEALTH_MODE_DATA_ID) {
                    for (SCS node : driverDistractionNodes) {
                        node.unprovide(signalID);
                        node.subscribe(signalID);
                    }
                } else if (signalID == HARDWARE_KEY_ID) {
                    for (SCS node : hardwareKeyNodes) {
                        node.unprovide(signalID);
                        node.subscribe(signalID);
                    }
                } else {
                    for (SCS node : signalNodes) {
                        node.unprovide(signalID);
                        node.subscribe(signalID);
                    }
                }
                provideMap.remove(signalID);
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Unprovides all signals for all current signalNodes but retains the IDs
     */
    public void unprovideAll() {
        lock.lock();
        try {
            for (SCS node : signalNodes) {
                for (Entry<Integer, Integer> entry : provideMap.entrySet()) {
                    node.unprovide(entry.getKey());
                }
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Provides all signals that have not been removed from the simulator for
     * all current signalNodes
     */
    public void provideAll() {
        lock.lock();
        try {
            for (SCS node : signalNodes) {
                for (Entry<Integer, Integer> entry : provideMap.entrySet()) {
                    node.provide(entry.getKey());
                    System.out.println("providing " + entry.getKey());
                }
            }
            for (SCS node : hardwareKeyNodes) {
                if (provideMap.keySet().contains(HARDWARE_KEY_ID)) {
                    node.provide(HARDWARE_KEY_ID);
                    System.out.println("providing hardware key");
                }
            }
            for (SCS node : driverDistractionNodes) {
                if (provideMap.keySet().contains(DRIVER_DISTRACTION_LEVEL_DATA_ID)) {
                    node.provide(DRIVER_DISTRACTION_LEVEL_DATA_ID);
                    System.out.println("providing driver distraction");
                }
                if (provideMap.keySet().contains(LIGHT_MODE_DATA_ID)) {
                    node.provide(LIGHT_MODE_DATA_ID);
                    System.out.println("providing light mode data");
                }
                if (provideMap.keySet().contains(STEALTH_MODE_DATA_ID)) {
                    node.provide(STEALTH_MODE_DATA_ID);
                    System.out.println("providing stealth mode data");
                }
            }

        } finally {
            lock.unlock();
        }
    }

    /**
     * Sends a single signal to all connected signalNodes It is important that
     * the SCSData match the signal id
     *
     * @param signalID
     * @param data
     */
    public void sendValue(int signalID, SCSData data) {
        lock.lock();
        // System.out.println("sending data");
        try {
            if (signalID == HARDWARE_KEY_ID) {
                for (SCS node : hardwareKeyNodes) {
                    node.send(signalID, data);
                    // System.out.println("GXT27 Steering Wheel module is sending"
                    // + new Uint32(data.getData()).getIntValue());
                }
            }
            if (signalID == DRIVER_DISTRACTION_LEVEL_DATA_ID || signalID == LIGHT_MODE_DATA_ID || signalID == STEALTH_MODE_DATA_ID) {
                for (SCS node : driverDistractionNodes) {
                    node.send(signalID, data);
                }
            } else {
                for (SCS node : signalNodes) {
                    node.send(signalID, data);
                }
            }
            lastValueSent.put(signalID, data);

        } finally {
            lock.unlock();
        }

    }

    public void disconnectSimulator() {
        lock.lock();
        try {
            provideMap.keySet().forEach(providedSignal -> {
                simulatorGateways.forEach(gateway -> gateway.unprovide(providedSignal));
            });
            simulatorGateways.forEach(gateway -> gateway.stop());
            simulatorGateways.clear();
            hardwareKeyNodes.clear();
            driverDistractionNodes.clear();
            signalNodes.clear();
            provideMap.clear();
        } finally {
            simulatorGateways.clear();
            lock.unlock();
        }
    }

    public void subscribeForAll(SCS node) {
        lock.lock();
        for (Integer i : collectedIds) {
            node.subscribe(i);
        }
        lock.unlock();
    }

    public LinkedList<SCSData> getReceivedValuesFor(int signalID) {
        return receivedData.get(signalID);
    }

    public SCSData getLastSentValueFor(int signalID) {
        return lastValueSent.get(signalID);
    }

    public boolean isConnecting() {
        return isConnecting;
    }
}

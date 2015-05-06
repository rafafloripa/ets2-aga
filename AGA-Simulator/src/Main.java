import combitech.sdp.simulator.SimulatorGateway;
import combitech.sdp.simulator.car.torcs.Ets2;
import combitech.sdp.simulator.car.torcs.Torcs;
import simulator.steeringwheel.gxt27.GXT27Module;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) throws Exception {
        String ipaddress = "127.0.0.1";
        SimulatorGateway gateway = new SimulatorGateway();
        gateway.addAndInitiateNode(ipaddress, 8251, null, null);
        gateway.addAndInitiateNode(ipaddress, 9898, null, null);
        gateway.addAndInitiateNode(ipaddress, 9899, null, null);
        Thread.sleep(1000);
        Ets2 ets2 = new Ets2(gateway);
        for (int i : ets2.getProvidingSignals()) {
            gateway.provideSignal(i);
        }
        Thread.sleep(1000);
        GXT27Module gtGxt27Module = new GXT27Module(gateway);
        for (int i : gtGxt27Module.getProvidingSignals()) {
            gateway.provideSignal(i);
        }
        Thread.sleep(1000);
        gtGxt27Module.startModule();
        ets2.startModule();
    }
}

package combitech.sdp.simulator;

public abstract class BasicModule implements Runnable {

    protected SimulatorGateway gateway;
    protected SimulationModuleState state;
    protected Thread moduleThread;

    public BasicModule(SimulatorGateway gateway) {
        this.gateway = gateway;
    }

    public void startModule() {
        state = SimulationModuleState.RUNNING;
        moduleThread = new Thread(this);
        moduleThread.start();
    }

    public void stopModule() {
        state = SimulationModuleState.STOPPED;
        try {
            if (moduleThread != null) {
                moduleThread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setSimulator(SimulatorGateway simulator) {
        this.gateway = simulator;
    }

    public abstract int[] getProvidingSignals();

    @Deprecated
    protected void provide() {
        for (int signalID : getProvidingSignals()) {
            gateway.provideSignal(signalID);
            // System.out.println("GXT27 Steering Wheel module is providing " + Arrays.toString(getProvidingSignals()));
        }
    }

    @Deprecated
    protected void unprovide() {
        for (int signalID : getProvidingSignals())
            gateway.unprovideSignal(signalID);
    }

    public Thread getModuleThread() {
        return moduleThread;
    }
}

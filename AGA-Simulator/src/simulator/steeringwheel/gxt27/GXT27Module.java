package simulator.steeringwheel.gxt27;

import android.swedspot.scs.data.Uint32;
import combitech.sdp.simulator.BasicModule;
import combitech.sdp.simulator.SimulationModuleState;
import combitech.sdp.simulator.SimulatorGateway;
import net.java.games.input.Component;
import net.java.games.input.Component.Identifier.Button;
import net.java.games.input.Controller;
import net.java.games.input.DirectAndRawInputEnvironmentPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class GXT27Module extends BasicModule {
    public final static int STEERING_WHEEL_ID = 514;
    private final static Logger LOGGER = LoggerFactory.getLogger(GXT27Module.class);
    private Controller controller;
    private ControllerModel model;
    private Component com;

    public GXT27Module(SimulatorGateway gateway) {
        super(gateway);
    }

    @Override
    public void run() {
        getModuleThread().setName("GXT27Module");
        initDevice();
        if (controller == null) {
            // LOGGER.debug("Could not find the Trust GXT 27 steering wheel");
            return;
        }

        while (state == SimulationModuleState.RUNNING) {
            updateControllerModel(controller.poll());
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private Controller getController() throws Exception {
        // LOGGER.debug("Looking for controller");

        if (System.getProperty("net.java.games.input.librarypath") == null) {
            String binPath = getBinPath();
            if (binPath != null)
                System.setProperty("net.java.games.input.librarypath", binPath + File.separator + "lib" + File.separator + "natives");
        }
        DirectAndRawInputEnvironmentPlugin env = new DirectAndRawInputEnvironmentPlugin();
        Controller[] controllers = env.getControllers();
        // LOGGER.debug("Environment found");
        for (Controller cont : controllers) {
            // LOGGER.debug("Controller: " + cont.getName());
            if (cont.getName().equals("Steering Wheel")) {
                // LOGGER.debug("Found Controller");
                return cont;
            }
        }
        // LOGGER.debug("No controller found");
        return null;
    }

    private String getBinPath() {
        String binPath;
        try {
            binPath = getClass().getClassLoader().getResource(".").getPath();
        } catch (Exception e) {
            return null;
        }

        binPath = binPath.substring(1).replace("/", File.separator);

        int indexOfBuild = binPath.indexOf("build");
        if (indexOfBuild > -1) // Gradle
        {
            binPath = binPath.substring(0, indexOfBuild + 5);
            return binPath;
        }

        int indexOfBin = binPath.indexOf("bin");
        if (indexOfBin > -1) // no Gradle
        {
            binPath = binPath.substring(0, indexOfBin + 3);
            return binPath;
        }
        return null;
    }

    private void initDevice() {
        try {
            controller = getController();
        } catch (Exception e) {
            // LOGGER.debug("Exception with" + e.getLocalizedMessage());
            e.printStackTrace();
        }
        model = new ControllerModel();
    }

    private void updateControllerModel(boolean didPoll) {
        if (didPoll) {
            com = controller.getComponent(Button._8);
            if (com.getPollData() > 0) {
                model.home = true;
            } else if (model.home) {
                // LOGGER.debug("home");
                gateway.sendValue(STEERING_WHEEL_ID, new Uint32(1 << 0));
                model.home = false;
            }

            com = controller.getComponent(Button._9);
            if (com.getPollData() > 0) {
                model.app = true;
            } else if (model.app) {
                // LOGGER.debug("app");
                gateway.sendValue(STEERING_WHEEL_ID, new Uint32(1 << 1));
                model.app = false;
            }

            com = controller.getComponent(Button._11);
            if (com.getPollData() > 0) {
                model.enter = true;
            } else if (model.enter) {
                // LOGGER.debug("enter");
                gateway.sendValue(STEERING_WHEEL_ID, new Uint32(1 << 2));
                model.enter = false;
            }

            com = controller.getComponent(Button._10);
            if (com.getPollData() > 0) {
                model.back = true;
            } else if (model.back) {
                // LOGGER.debug("back");
                gateway.sendValue(STEERING_WHEEL_ID, new Uint32(1 << 3));
                model.back = false;
            }

            com = controller.getComponent(Component.Identifier.Axis.POV);
            float pollData = com.getPollData();
            if (pollData > 0) {
                if (pollData == 0.25) {
                    model.up = true;
                }
                if (pollData == 0.5) {
                    model.right = true;
                }
                if (pollData == 0.75) {
                    model.down = true;
                }
                if (pollData == 1.0) {
                    model.left = true;
                }
            } else if (model.up) {
                // LOGGER.debug("up");
                gateway.sendValue(STEERING_WHEEL_ID, new Uint32(1 << 4));
                model.up = false;
            } else if (model.right) {
                // LOGGER.debug("right");
                gateway.sendValue(STEERING_WHEEL_ID, new Uint32(1 << 5));
                model.right = false;
            } else if (model.down) {
                // LOGGER.debug("down");
                gateway.sendValue(STEERING_WHEEL_ID, new Uint32(1 << 6));
                model.down = false;
            } else if (model.left) {
                // LOGGER.debug("left");
                gateway.sendValue(STEERING_WHEEL_ID, new Uint32(1 << 7));
                model.left = false;
            }

            com = controller.getComponent(Button._7);
            if (com.getPollData() > 0) {
                model.plus = true;
            } else if (model.plus) {
                // LOGGER.debug("plus");
                gateway.sendValue(STEERING_WHEEL_ID, new Uint32(1 << 8));
                model.plus = false;
            }

            com = controller.getComponent(Button._4);
            if (com.getPollData() > 0) {
                model.minus = true;
            } else if (model.minus) {
                // LOGGER.debug("minus");
                gateway.sendValue(STEERING_WHEEL_ID, new Uint32(1 << 9));
                model.minus = false;
            }

            com = controller.getComponent(Button._2);
            if (com.getPollData() > 0) {
                model.power = true;
            } else if (model.power) {
                // LOGGER.debug("power");
                gateway.sendValue(STEERING_WHEEL_ID, new Uint32(1 << 10));
                model.power = false;
            }
        }
    }

    @Override
    public int[] getProvidingSignals() {
        return new int[]{STEERING_WHEEL_ID};
    }

    class ControllerModel {
        private boolean up = false;
        private boolean right = false;
        private boolean down = false;
        private boolean left = false;
        private boolean app = false;
        private boolean home = false;
        private boolean power = false;
        private boolean back = false;
        private boolean enter = false;
        private boolean minus = false;
        private boolean plus = false;

        public int getFlags() {
            int upInt = up ? 1 : 0;
            int rightInt = right ? 1 : 0;
            int downInt = down ? 1 : 0;
            int leftInt = left ? 1 : 0;
            int appInt = app ? 1 : 0;
            int powerInt = power ? 1 : 0;
            int backInt = back ? 1 : 0;
            int enterInt = enter ? 1 : 0;
            int minusInt = minus ? 1 : 0;
            int plusInt = plus ? 1 : 0;
            int homeInt = home ? 1 : 0;
            return (homeInt << 0) +
                    (appInt << 1) +
                    (enterInt << 2) +
                    (backInt << 3) +
                    (upInt << 4) +
                    (rightInt << 5) +
                    (downInt << 6) +
                    (leftInt << 7) +
                    (plusInt << 8) +
                    (minusInt << 9) +
                    (powerInt << 10);
        }
    }
}

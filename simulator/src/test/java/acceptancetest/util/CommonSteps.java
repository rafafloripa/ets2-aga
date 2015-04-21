package acceptancetest.util;

import android.swedspot.scs.data.*;
import combitech.sdp.dummy.Server;
import combitech.sdp.simulator.SimulatorGateway;
import cucumber.api.java.After;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;

import java.util.LinkedList;

import static acceptancetest.util.Util.*;
import static org.junit.Assert.*;

public class CommonSteps {

    @After("@shutdownNode")
    public void shutdownNodeStep() {
        try {
            if (staticSimulator != null) {
                staticSimulator.disconnectSimulator();
                staticSimulator = null;
            }
            if (staticDummyApp != null) {
                staticDummyApp.stop();
                staticDummyApp = null;
            }
            if (staticServer != null) {
                staticServer.shutdownServer();
                staticServer = null;
            }
            if (staticModule != null) {
                staticModule.stopModule();
                staticModule = null;
            }
            Thread.sleep(3000);
        } catch (Exception e) {
        }

    }

    @Given("^The simulator is setup$")
    public void setupSimulator() throws Throwable {
        staticSimulator = new SimulatorGateway();
    }

    @Given("^The simulator is providing signal id (\\d+)$")
    public void simulatorProvidesSignal(int signalID) {
        staticSimulator.provideSignal(signalID);
    }

    @Given("^Add a node to simulator on port (\\d+) and ip (.*)$")
    public void addNode(int port, String ipAddress) {
        staticSimulator.addAndInitiateNode(ipAddress, port, null, null);
    }

    @And("^After (\\d+) mSec have passed$")
    public void wait(int timeOut) throws Throwable {
        Thread.sleep(timeOut);
    }

    @Given("^The dummy application is setup and listening on port (\\d+)$")
    public void the_dummy_application_is_setup_and_listening_on_port(int port) throws Throwable {
        staticDummyApp = new DummyApplication(port);
    }

    @Given("^The dummy application subscribes for signal id (\\d+)$")
    public void dummyAppSubscribe(int signalID) {
        staticDummyApp.subscribe(signalID);
    }

    @And("^The server subscribes for (\\d+)$")
    public void serverSubscribe(int signalID){
        staticServer.subscribe(new String[]{String.valueOf(signalID)});
    }

    @Then("^The simulator should have received (\\d+) from signal id (\\d+) as a (.*)$")
    public void simulatorHasReceived(int value, int signalID, String t) {
        String type = t.toLowerCase();
        LinkedList<SCSData> data = staticSimulator.getReceivedValuesFor(signalID);
        assertNotNull(data);

        switch (type) {
        case "integer":
            SCSInteger scsInteger = new SCSInteger(data.getFirst().getData());
            assertEquals(scsInteger.getIntValue(), value);
            break;
        case "float":
            SCSFloat scsFloat = new SCSFloat(data.getFirst().getData());
            assertEquals(scsFloat.getFloatValue(), value, 0.01);
            break;
        case "uint32":
            Uint32 uint32 = new Uint32(data.getFirst().getData());
            assertEquals(uint32.getIntValue(), value);
            break;
        case "uint16":
            Uint16 uint16 = new Uint16(data.getFirst().getData());
            assertEquals(uint16.getIntValue(), value);
            break;
        case "uint8":
            Uint8 uint8 = new Uint8(data.getFirst().getData());
            assertEquals(uint8.getIntValue(), value);
            break;
        case "double":
            SCSDouble scsDouble = new SCSDouble(data.getFirst().getData());
            assertEquals(scsDouble.getDoubleValue(), value, 0.01);
            break;
        }
    }

    @And("^The simulator sends signal (\\d+) as a (.*) with the value (\\d+)$")
    public void simulatorSendsData(int signalID, String t, int value) {
        String type = t.toLowerCase();
        SCSData data = null;

        switch (type) {
        case "integer":
            data = new SCSInteger(value);
            break;
        case "float":
            data = new SCSFloat(value);
            break;
        case "uint32":
            data = new Uint32(value);
            break;
        case "uint16":
            data = new Uint16(value);
            break;
        case "uint8":
            data = new Uint8(value);
            break;
        case "double":
            data = new SCSDouble(value);
            break;
        default:
            fail();
        }
        staticSimulator.sendValue(signalID, data);
    }

    @And("^The simulator disconnects$")
    public void simulatorDisconnects() {
        staticSimulator.disconnectSimulator();
        staticSimulator = null;
    }

    @And("^The server should have received (\\d+) from signal id (\\d+) as a (.*)$")
    public void serverHasReceived(int value, int signalID, String t) {
        String type = t.toLowerCase();
        SCSData data = null;

        switch (type) {
        case "integer":
            data = new SCSInteger(value);
            break;
        case "float":
            data = new SCSFloat(value);
            break;
        case "uint32":
            data = new Uint32(value);
            break;
        case "uint16":
            data = new Uint16(value);
            break;
        case "uint8":
            data = new Uint8(value);
            break;
        case "double":
            data = new SCSDouble(value);
            break;
        default:
            fail("not a valid type! Check your spelling");
        }
        assertTrue(staticServer.didReceiveValue(signalID, data));
    }

    @Given("^The dummy server is setup$")
    public void dummyServerSetup() {
        staticServer = new Server();
        staticServer.startServer();
        staticServer.start();
    }

}

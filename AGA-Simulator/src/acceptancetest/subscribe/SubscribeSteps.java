package acceptancetest.subscribe;

import static org.junit.Assert.*;

import android.swedspot.scs.data.Uint32;

import acceptancetest.util.Predicate;
import acceptancetest.util.Util;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class SubscribeSteps {

    // First given method lies within the ReadFileSetps.java

    @Given("^The simulator is providing (\\d+) with value (\\d+)$")
    public void setupSimulatorSignal(int signalID, int startingValue) throws Throwable {
        Util.staticSimulator.provideSignal(signalID);
        Util.staticSimulator.sendValue(signalID, new Uint32(startingValue));
        Thread.sleep(1000);
    }

    @Given("^DummyApp subscribes for signal (\\d+)$")
    public void developerSubscribes(int signalID) throws Throwable {
        Util.staticDummyApp.subscribe(signalID);
    }

    @When("^The simulator changes the signal (\\d+) to (\\d+)$")
    public void signalChangesValue(int signalID, int newValue) throws Throwable {
        Util.staticSimulator.sendValue(signalID, new Uint32(newValue));
    }

    @Then("^DummyApp should get a notification for signal (\\d+) with value (\\d+)$")
    public void notifyDeveloper(final int signalID, final int newValue) throws Throwable {
    	boolean result = Util.WaitFor(new Predicate() {
			
			@Override
			public boolean check() {
				Integer value = Util.staticDummyApp.getReceivedValue(signalID);
				System.out.println("comparing "+value +" with "+newValue);
				return (value != null) && (value.equals(newValue));
			}
		}, 8000, 500);
    	assertTrue("The new value was not received within 8 seconds!", result);
    }
}

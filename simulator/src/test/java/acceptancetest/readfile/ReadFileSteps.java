package acceptancetest.readfile;

import static org.junit.Assert.assertTrue;

import java.io.File;

import simulator.filereplayer.FileReplayer;
import acceptancetest.util.Predicate;
import acceptancetest.util.Util;
import cucumber.api.DataTable;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

public class ReadFileSteps {

	@Given("^The DummyApp has subscribed to signal (\\d+)$")
	public void setupDummyApp(int signalID1) throws Throwable {
		Thread.sleep(1000);
		Util.staticDummyApp.subscribe(signalID1);
	}

	@Given("^The simulator reads ExampleData file to replay$")
	public void the_simulator_reads_ExampleData_file_to_replay()
			throws Throwable {
		System.err.println(new java.io.File(".").getCanonicalPath());
		File exampleData = new File("testResources/ExampleData");
		assertTrue(exampleData.exists());
		Util.staticModule = new FileReplayer(Util.staticSimulator);
		assertTrue(((FileReplayer)Util.staticModule).readFile(exampleData));
	}

	@When("^The simulator start replaying$")
	public void the_simulator_start_replaying() throws Throwable {
		Util.staticModule.startModule();
	}

	@Then("^The DummyApp should wait for the simulator to finish sending all data$")
	public void dummyapp_should_get_all_changes(DataTable table)
			throws Throwable {
		long totalTimestamp = 0;
		long diff = 0;
		for (int i = 2; i < table.raw().size(); i++) {
			diff = Long.parseLong(table.raw().get(i).get(0))
					- Long.parseLong(table.raw().get(i - 1).get(0));
			totalTimestamp += diff;
		}
		Thread.sleep(totalTimestamp + 1000);
	}

	@Then("^The DummyApp should have received all data$")
	public void receivedSignals(final DataTable table) {
		boolean result = Util.WaitFor(new Predicate() {

			@Override
			public boolean check() {
				if (table.raw().size()-1 != Util.staticDummyApp.getListOfreceivedValues().size()) {
					return false;
				}
				for (int i = 1; i < table.raw().size(); i++) {
					if (Integer.parseInt(table.raw().get(i).get(2)) != (int) Util.staticDummyApp.getListOfreceivedValues().get(i - 1)) {
						return false;
					}
						
				}
				return true;
			}
		}, 10000, 400);

		assertTrue("Not recieved the expected signals within 10 Sec.", result);
	}
}

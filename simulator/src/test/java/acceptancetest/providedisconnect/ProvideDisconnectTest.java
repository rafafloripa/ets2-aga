package acceptancetest.providedisconnect;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
		features = "classpath:acceptancetest/providedisconnect/provideDisconnect.feature",
		glue = { "acceptancetest.providedisconnect", "acceptancetest.util" },
		monochrome = true, 
		format = {"pretty", "json:build/cucumber-reports/cucumber.json"})
public class ProvideDisconnectTest {

}

package acceptancetest.receivedata;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
		features = "classpath:acceptancetest/receivedata/receiveData.feature",
		glue = { "acceptancetest.receivedata", "acceptancetest.util" },
		monochrome = true, 
		format = {"pretty", "json:build/cucumber-reports/cucumber.json"})
public class ReceiveDataTest {

}

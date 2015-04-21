package acceptancetest.gxt27sendsignal;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(
		features = "classpath:acceptancetest/gxt27sendsignal/gxt27sendsignal.feature", 
		glue = { "acceptancetest.gxt27sendsignal", "acceptancetest.util" }, 
		monochrome = true, 
		format = {"pretty", "json:build/cucumber-reports/cucumber.json"})
public class GXT27sendsignalTest {
}

package acceptancetest.anchorTest;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(
		features = "classpath:acceptancetest/anchorTest/anchor.feature", 
		glue = { "acceptancetest.anchorTest", "acceptancetest.util" }, 
		monochrome = true, 
		format = {"pretty", "json:build/cucumber-reports/cucumber.json"})
public class AnchorTest {
}

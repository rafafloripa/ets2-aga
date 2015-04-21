package acceptancetest.readfile;

import org.junit.runner.RunWith;

import cucumber.api.CucumberOptions;
import cucumber.api.junit.Cucumber;

@RunWith(Cucumber.class)
@CucumberOptions(
		features = "src/test/java/acceptancetest/readfile/readFile.feature",
		glue = { "acceptancetest.readfile", "acceptancetest.util" }, 
		monochrome = true, 
		format = {"pretty", "json:build/cucumber-reports/cucumber.json"})
public class ReadFileTest {
}

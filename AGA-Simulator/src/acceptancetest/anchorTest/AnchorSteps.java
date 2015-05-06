package acceptancetest.anchorTest;

import static org.junit.Assert.assertTrue;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

/**
 * This is a very basic type of test, for tutorials look here:
 * https://github.com/cucumber/cucumber/wiki/tutorials-and-related-blog-posts
 * 
 * @author Christopher
 * 
 */
public class AnchorSteps {
    @Given("^dummy given$")
    public void dummy_given() throws Throwable {
        assertTrue(true);
    }

    @When("^dummy when$")
    public void dummy_when() throws Throwable {
        assertTrue(true);
    }

    @Then("^dummy then$")
    public void dummy_then() throws Throwable {
        assertTrue(true);
    }
}

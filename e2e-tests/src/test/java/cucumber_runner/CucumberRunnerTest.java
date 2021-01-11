package cucumber_runner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.AfterSuite;

@CucumberOptions(
        features = "src/test/resources/features",
        glue = {"step_definitions"}
)
public class CucumberRunnerTest extends AbstractTestNGCucumberTests {
    @AfterSuite
    public void terminate() {
        //driver.quit();
    }
}

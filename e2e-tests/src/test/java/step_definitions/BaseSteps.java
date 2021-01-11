package step_definitions;

import base.BaseTest;
import io.cucumber.java.After;
import io.cucumber.java.Before;

import java.net.MalformedURLException;

import static base.Constants.SOCIAPP_URL;

public class BaseSteps extends BaseTest {

    @Before
    public void init() {
        try {
            initializeDriver();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        driver.get(SOCIAPP_URL);
    }

    @After
    public void tearDown() {
        driver.quit();
    }
}

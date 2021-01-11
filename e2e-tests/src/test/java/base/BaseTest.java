package base;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import util.TestContext;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class BaseTest {

    public static WebDriver driver;

    public void initializeDriver() throws MalformedURLException {
        String browser = System.getProperty("browser");
        boolean isRemote = Boolean.parseBoolean(System.getProperty("remote"));

        if(browser != null && browser.equals("firefox")) {
            System.setProperty("webdriver.gecko.driver", "./test/resources/drivers/geckodriver");
            driver = new FirefoxDriver();
        } else {
            ChromeOptions chromeOptions = new ChromeOptions();
            chromeOptions.addArguments("--disable-dev-shm-usage"); // overcome limited resource problems
            chromeOptions.addArguments("--no-sandbox");
            WebDriverManager.chromedriver().setup();
            if(isRemote) {
                driver = new RemoteWebDriver(new URL("http://192.168.0.14:4444/wd/hub"), chromeOptions);
            } else {
                driver = new ChromeDriver(chromeOptions);
            }
        }



        driver.manage().window().maximize();
        driver.manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS);
        driver.manage().timeouts().implicitlyWait(15, TimeUnit.SECONDS);
    }

    public TestContext testContext() {
        return TestContext.CONTEXT;
    }
}

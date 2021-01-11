package util;

import com.google.gson.Gson;
import org.apache.commons.io.IOUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

public class HelperMethods {

    public static Map<String, Object> executeCurlAndReturnResponse(String curl) {
        Process exec;
        String responseAsString = null;
        try {
            exec = Runtime.getRuntime().exec(curl);
            responseAsString = IOUtils.toString(Objects.requireNonNull(exec).getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(responseAsString);
        return new Gson().fromJson(responseAsString, Map.class);
    }

    public static WebElement getElementByText(String text, WebDriver driver) {
        return driver.findElement(By.xpath("//*[contains(text(),'" + text + "')]"));
    }

    public static By getLocatorByText(String text) {
        return By.xpath("//*[contains(text(),'" + text + "')]");
    }

    public static void clearAndEnterInput(WebElement element, String input) {
        element.clear();
        element.sendKeys(input);
    }

    public static void waitUntilTextIsDisplayed(WebDriverWait wait, String text, WebDriver driver) {
        wait.until(ExpectedConditions.textToBePresentInElement(getElementByText(text, driver), text));
    }

    public static void clickOnElementWithJs(String id, WebDriver driver) {
        ((JavascriptExecutor) driver).executeScript("document.getElementById('" + id + "').click();");
    }
}

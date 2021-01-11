package page_objects;

import base.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class CommonPageObjects extends BaseTest {

    public CommonPageObjects() {
        PageFactory.initElements(driver, this);
    }

    @FindBy(css = ".alert-danger")
    WebElement errorMessage;

    @FindBy(css = ".btn-delete-link")
    List<WebElement> deleteButtons;

    @FindBy(css = ".btn-danger")
    WebElement dangerButton;

    private By deleteButtonLocator = By.cssSelector(".btn-delete-link");

    public String getErrorMessage() {
        return errorMessage.getText();
    }

    public void enterCredentialsByGivenName(String input, String name) {
        WebElement element = driver.findElement(By.name(name));
        if(input != null) element.sendKeys(input);
    }

    public List<WebElement> getDeleteButtons() {
        return deleteButtons;
    }

    public WebElement getDangerButton() {
        return dangerButton;
    }

    public By getDeleteButtonLocator() {
        return deleteButtonLocator;
    }
}

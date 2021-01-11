package page_objects;

import base.BaseTest;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class SignupPage extends BaseTest {

    public SignupPage() {
        PageFactory.initElements(driver, this);
    }

    @FindBy(css = ".btn")
    WebElement signupButton;

    public void clickOnSignupButton() {
        signupButton.click();
    }

    public boolean isSignupButtonEnabled() {
        return signupButton.isEnabled();
    }
}

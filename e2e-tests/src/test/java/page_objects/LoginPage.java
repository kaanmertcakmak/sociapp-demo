package page_objects;

import base.BaseTest;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.Objects;

public class LoginPage extends BaseTest {

    public LoginPage() {
        PageFactory.initElements(driver, this);
    }

    @FindBy(name = "username")
    WebElement userNameInputField;

    @FindBy(name = "password")
    WebElement passwordInputField;

    @FindBy(css = ".btn")
    WebElement loginButton;

    public void enterLoginInformations(String username, String password) {
        if (username != null) {
            userNameInputField.sendKeys(username);
        }
        if (password != null) {
            passwordInputField.sendKeys(password);
        }
    }

    public void clickLoginButton() {
        loginButton.click();
    }

    public boolean isLoginButtonEnabled() {
        return loginButton.isEnabled();
    }
}

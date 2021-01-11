package page_objects;

import base.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class HomePage extends BaseTest {

    @FindBy(css = "a[href='#/login']")
    WebElement loginPageButton;

    @FindBy(css = "a[href='#/register']")
    WebElement signupPageButton;

    @FindBy(css = ".nav-link")
    WebElement navbar;

    @FindBy(css = "textarea.form-control")
    WebElement contentTextEntryField;

    @FindBy(css = ".btn-primary")
    WebElement shareContentsButton;

    @FindBy(css = ".card:nth-child(1) .pl-5")
    WebElement firstContentsText;

    @FindBy(css = ".list-group-item span")
    List<WebElement> usersInUserList;

    private By contentLocator = By.cssSelector(".card.p-1 .text-dark");


    public HomePage() {
        PageFactory.initElements(driver, this);
    }

    public void openLoginPage() {
        loginPageButton.click();
    }

    public void openSignupPage() {
        signupPageButton.click();
    }

    public WebElement getNavbar() {
        return navbar;
    }

    public void postContentWithGivenText(String content) {
        contentTextEntryField.click();
        contentTextEntryField.sendKeys(content);
        shareContentsButton.click();
    }

    public void postContentWithGivenTextAndImage(String content, String imagePath) {
        contentTextEntryField.click();
        contentTextEntryField.sendKeys(content);
        MyProfilePage myProfilePage = PageFactory.initElements(driver, MyProfilePage.class);
        myProfilePage.getSelectFileButton().sendKeys(System.getProperty("user.dir") + imagePath);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        shareContentsButton.click();
    }


    public String getFirstContentsText() {
        return firstContentsText.getText();
    }

    public void clickOnFirstContent() {
        driver.findElement(contentLocator).click();
    }
    public By getContentLocator() {
        return contentLocator;
    }

    public List<WebElement> getUsersInUserList() {
        return usersInUserList;
    }
}

package page_objects;

import base.BaseTest;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class MyProfilePage extends BaseTest {

    public MyProfilePage() {
        PageFactory.initElements(driver, this);
    }

    @FindBy(css = ".btn-success")
    WebElement editButton;

    @FindBy(css = ".form-group .form-control")
    WebElement updateDisplayNameInput;

    @FindBy(css = ".card-body h3")
    WebElement displayNameUnderProfileCard;

    @FindBy(css = "img[alt='Profile'] + div h6")
    List<WebElement> profileNamesInMyProfilePage;

    @FindBy(css = ".form-control-file")
    WebElement selectFileButton;

    public void clickOnEditButton() {
        editButton.click();
    }

    public WebElement getUpdateDisplayNameInput() {
        return updateDisplayNameInput;
    }

    public String getDisplayNameUnderProfileCard() {
        return displayNameUnderProfileCard.getText();
    }

    public List<WebElement> getProfileNamesInMyProfilePage() {
        return profileNamesInMyProfilePage;
    }

    public WebElement getSelectFileButton() {
        return selectFileButton;
    }
}

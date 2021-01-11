package step_definitions;

import base.BaseTest;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import page_objects.CommonPageObjects;
import page_objects.HomePage;
import page_objects.MyProfilePage;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static util.HelperMethods.*;

public class MyProfileStepDefinitions extends BaseTest {

    private final MyProfilePage myProfilePage;
    private final HomePage homePage;
    private final CommonPageObjects commonPageObjects;
    private final WebDriverWait wait;

    public MyProfileStepDefinitions() {
        super();
        myProfilePage = PageFactory.initElements(driver, MyProfilePage.class);
        homePage = PageFactory.initElements(driver, HomePage.class);
        commonPageObjects = PageFactory.initElements(driver, CommonPageObjects.class);
        wait = new WebDriverWait(driver, 10);
    }

    @When("I try to update display name as {string}")
    public void updateProfile(String displayName) {
        myProfilePage.clickOnEditButton();
        clearAndEnterInput(myProfilePage.getUpdateDisplayNameInput(), displayName);
        testContext().set("updatedDisplayName", displayName);
        getElementByText("Save", driver).click();
    }

    @Then("I verify display name is updated properly")
    public void iVerifyDisplayNameIsUpdatedProperly() {
        String updatedDisplayName = testContext().get("updatedDisplayName");

        assertThat("profile name Should contain " + updatedDisplayName,
                myProfilePage.getDisplayNameUnderProfileCard().contains(updatedDisplayName));

        assertThat("displayName on navbar Should contain " + updatedDisplayName,
                homePage.getNavbar().getText().contains(updatedDisplayName));

        driver.navigate().refresh();
        myProfilePage.getProfileNamesInMyProfilePage().forEach(profileName -> assertThat("profileNames Should contain " + updatedDisplayName,
                profileName.getText().contains(updatedDisplayName)));
    }

    @After(value = "@revert_update")
    public void revertUpdate() {
        updateProfile("display1");
    }

    @When("I try to delete all of the user's contents")
    public void iTryToDeleteAllOfTheUserSContents() {
        List<WebElement> deleteContentButtons = commonPageObjects.getDeleteButtons();

        deleteContentButtons.forEach(deleteContentButton -> {
            deleteContentButton.click();
            waitUntilTextIsDisplayed(wait, "Are you sure to delete following post?", driver);
            clickOnElementWithJs("deleteContent", driver);
        });
        wait.until(ExpectedConditions.numberOfElementsToBe(commonPageObjects.getDeleteButtonLocator(), 0));
    }

    @After(value = "@delete_content")
    public void deleteContent() {
        iTryToDeleteAllOfTheUserSContents();
        iVerifyTextShouldBeDisplayed("There are not shared Contents");
    }

    @Then("I verify {string} text should be displayed")
    public void iVerifyTextShouldBeDisplayed(String text) {
        waitUntilTextIsDisplayed(wait, text, driver);
    }

    @And("I verify posted content should be displayed in My Profile page")
    public void iVerifyPostedContentShouldBeDisplayedInMyProfilePage() {
        String createdContent = testContext().get("createdContent");
        assertEquals(homePage.getFirstContentsText(), createdContent);
    }

    @When("I try to update profile photo and verified if it is updated properly")
    public void iTryToUpdateProfilePhoto() {
        myProfilePage.clickOnEditButton();
        myProfilePage.getSelectFileButton().sendKeys(System.getProperty("user.dir") + "/src/test/resources/assets/pica.png");
        getElementByText("Save", driver).click();
        wait.until(ExpectedConditions.numberOfElementsToBe(getLocatorByText("Save"), 0));
    }

    @After(value = "@revert_profile_photo")
    public void revertPhoto() {
        myProfilePage.clickOnEditButton();
        myProfilePage.getSelectFileButton().sendKeys(System.getProperty("user.dir") + "/src/test/resources/assets/logo192.png");
        getElementByText("Save", driver).click();
        wait.until(ExpectedConditions.numberOfElementsToBe(getLocatorByText("Save"), 0));
    }
}

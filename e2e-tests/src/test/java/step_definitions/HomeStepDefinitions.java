package step_definitions;

import base.BaseTest;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import page_objects.HomePage;

import static base.Constants.SOCIAPP_URL;
import static org.junit.Assert.*;
import static org.testng.Assert.assertThrows;
import static util.HelperMethods.getElementByText;
import static util.HelperMethods.waitUntilTextIsDisplayed;

public class HomeStepDefinitions extends BaseTest {

    private final HomePage homePage;
    private WebDriverWait wait;

    public HomeStepDefinitions() {
        super();
        homePage = PageFactory.initElements(driver, HomePage.class);
        wait = new WebDriverWait(driver, 10);
    }


    @Given("I post a following content")
    public void iPostAFollowingContent(DataTable dataTable) {
        String content = dataTable.asList().get(0);
        homePage.postContentWithGivenText(content);

        driver.navigate().refresh();
        waitUntilTextIsDisplayed(wait, homePage.getFirstContentsText(), driver);

        testContext().set("createdContent", content);
    }

    @Given("I post a following content with an image")
    public void iPostAFollowingContentWithImage(DataTable dataTable) {
        String content = dataTable.asList().get(0);
        homePage.postContentWithGivenTextAndImage(content, "/src/test/resources/assets/pica.png");

        wait = new WebDriverWait(driver, 100);
        waitUntilTextIsDisplayed(wait, "There are new contents", driver);
        getElementByText("There are new contents", driver).click();

        testContext().set("createdContent", content);
    }

    @After(value = "@delete_content_home_page")
    public void deleteContent() {
        MyProfileStepDefinitions myProfileStepDefinitions = new MyProfileStepDefinitions();
        myProfileStepDefinitions.iTryToDeleteAllOfTheUserSContents();
    }

    @Then("I verify posted content should be displayed in Home page")
    public void iVerifyPostedContentShouldBeDisplayedInHomePage() {
        String createdContent = testContext().get("createdContent");

        assertEquals(homePage.getFirstContentsText(), createdContent);
    }

    @Given("I've displayed Home page")
    public void iVeDisplayedHomePage() {
        wait.until(ExpectedConditions.urlContains(SOCIAPP_URL));

    }

    @Then("I verify user is able to load older contents properly")
    public void iVerifyUserIsAbleToLoadOlderContentsProperly() {
        int startingContentsCount = 10;
        wait.until(ExpectedConditions.numberOfElementsToBe(homePage.getContentLocator(), startingContentsCount));
        while(true) {
            try {
                getElementByText("Load Past Shared Thoughts", driver).click();
                startingContentsCount += 10;
                wait.until(ExpectedConditions.numberOfElementsToBe(homePage.getContentLocator(), startingContentsCount));
            } catch (NoSuchElementException e) {
                System.out.println("element does not display");
                break;
            }
        }
    }

    @And("I navigate through user list and verify logged in user does not displayed in User list")
    public void iNavigateThroughUserListAndVerifyLoggedInUserDoesNotDisplayedInUserList() {
        while(true) {
            try {
                homePage.getUsersInUserList().forEach(user -> {
                    assertNotEquals(testContext().get("loggedInDisplayName") + "@" + testContext().get("loggedInUsername"),
                            user.getText());
                });
                getElementByText("Next", driver).click();
                System.out.println("clicked on element");
            } catch (StaleElementReferenceException ignored) {
            } catch (NoSuchElementException e) {
                System.out.println("element does not display");
                break;
            }
        }
    }

    @And("I clicked on {string}")
    public void iClickedOn(String text) {
        getElementByText(text, driver).click();
    }

    @And("I navigate first content's profile page")
    public void iNavigateFirstContentSUserSProfilePage() {
        homePage.clickOnFirstContent();
        wait.until(ExpectedConditions.urlContains("/#/user/"));
    }

    @Then("I verify Edit and Delete User buttons do not display")
    public void iVerifyEditAndDeleteUserButtonsDoNotDisplay() {
        boolean areButtonsDisplayed = false;
        try {
            getElementByText("Edit", driver);
            getElementByText("Delete My Account", driver);
            areButtonsDisplayed = true;
        } catch (NoSuchElementException e) {
            assertNotNull(e);
        }
        assertFalse(areButtonsDisplayed);
    }
}

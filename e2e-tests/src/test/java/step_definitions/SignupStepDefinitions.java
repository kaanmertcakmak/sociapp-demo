package step_definitions;

import base.BaseTest;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import page_objects.CommonPageObjects;
import page_objects.HomePage;
import page_objects.SignupPage;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static util.HelperMethods.executeCurlAndReturnResponse;
import static util.HelperMethods.getElementByText;

public class SignupStepDefinitions extends BaseTest {

    private final HomePage homePage;
    private final CommonPageObjects commonPageObjects;
    private final SignupPage signupPage;
    private WebDriverWait wait;

    public SignupStepDefinitions() {
        super();
        homePage = PageFactory.initElements(driver, HomePage.class);
        commonPageObjects = PageFactory.initElements(driver, CommonPageObjects.class);
        signupPage = PageFactory.initElements(driver, SignupPage.class);
        wait = new WebDriverWait(driver, 10);
    }

    @Given("I navigate to Signup Page")
    public void iNavigateToSignupPage() {
        homePage.openSignupPage();
        wait.until(ExpectedConditions.urlContains("/register"));
    }

    @When("I try to signup with following credentials")
    public void iTryToSignupWithFollowingCredentials(DataTable dataTable) {
        List<Map<String, String>> table = dataTable.asMaps();
        Map<String, String> columns = table.get(0);
        Set<String> headers = columns.keySet();

        headers.forEach(name -> {
            commonPageObjects.enterCredentialsByGivenName(columns.get(name), name);
            if (name != null) testContext().set(name, columns.get(name));
        });

        signupPage.clickOnSignupButton();
    }

    @Then("I should be successfully registered")
    public void iShouldBeSuccessfullyRegistered() {
        Object username = testContext().get("username");
        String displayName = testContext().get("displayName");

        wait.until(ExpectedConditions.textToBePresentInElement(homePage.getNavbar(), displayName));

        String getUserByUsernameCommand = "curl --location --request GET \"http://localhost:8080/api/1.0/users/" + username +"\"";
        Map<String, Object> getByUsernameResponse = executeCurlAndReturnResponse(getUserByUsernameCommand);

        assertEquals(getByUsernameResponse.get("username"), username);
        assertEquals(getByUsernameResponse.get("displayName"), displayName);
    }

    @After(value = "@delete_user_ui")
    public void deleteUser() {
        homePage.getNavbar().click();
        getElementByText("My Profile", driver).click();
        wait.until(ExpectedConditions.urlContains("/user/" + testContext().get("username") + ""));

        getElementByText("Delete My Account", driver).click();
        wait.until(ExpectedConditions.elementToBeClickable(getElementByText("Delete User", driver)));

        getElementByText("Delete User", driver).click();
        wait.until(ExpectedConditions.elementToBeClickable(getElementByText("Login", driver)));
    }

    @Then("I verify {string} is displayed under {string}")
    public void iVerifyIsDisplayedUnder(String message, String fieldName) {
        WebElement element = driver.findElement(By.cssSelector("input[name='" + fieldName + "'] + div"));

        wait.until(ExpectedConditions.textToBePresentInElement(element, message));
        assertEquals(element.getText(), message);
    }

    @And("I verify signup button is disabled")
    public void iVerifySignupButtonIsDisabled() {
        assertFalse(signupPage.isSignupButtonEnabled());
    }

    @Then("I verify url contains {string}")
    public void iVerifyUrlContains(String url) {
        wait.until(ExpectedConditions.urlContains(url));
    }
}

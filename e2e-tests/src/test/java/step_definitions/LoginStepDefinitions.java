package step_definitions;

import base.BaseTest;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import page_objects.CommonPageObjects;
import page_objects.HomePage;
import page_objects.LoginPage;

import java.util.List;

import static base.Constants.SOCIAPP_URL;
import static org.testng.Assert.*;
import static util.HelperMethods.getElementByText;

public class LoginStepDefinitions extends BaseTest {

    private final HomePage homePage;
    private final LoginPage loginPage;
    private final CommonPageObjects commonPageObjects;
    private WebDriverWait wait;

    public LoginStepDefinitions() {
        super();
        homePage = PageFactory.initElements(driver, HomePage.class);
        loginPage = PageFactory.initElements(driver, LoginPage.class);
        commonPageObjects = PageFactory.initElements(driver, CommonPageObjects.class);
        wait = new WebDriverWait(driver, 10);
    }

    @Given("I navigate to Login Page")
    public void iNavigateToLoginPage() {
        homePage.openLoginPage();
        wait.until(ExpectedConditions.urlContains("/login"));
    }

    @Before(value = "@login")
    public void login() {
        iNavigateToLoginPage();
        loginPage.enterLoginInformations("user1", "Kaan1234");
        loginPage.clickLoginButton();
        iverifyLoginIsSuccessful();
    }

    @Before(value = "@login_2")
    public void login2() {
        iNavigateToLoginPage();
        loginPage.enterLoginInformations("UserSelenium", "Kaan1234");
        loginPage.clickLoginButton();
        iverifyLoginIsSuccessful();
        testContext().set("loggedInUsername", "UserSelenium");
        testContext().set("loggedInDisplayName", "display1");
    }

    @When("I login with following credentials")
    public void iLoginWithFollowingCredentials(DataTable dataTable) {
        List<String> loginCredentials = dataTable.asList();
        loginPage.enterLoginInformations(loginCredentials.get(0), loginCredentials.get(1));
        loginPage.clickLoginButton();
    }

    @When("I should be successfully logged in")
    public void iverifyLoginIsSuccessful() {
        wait.until(ExpectedConditions.textToBePresentInElement(homePage.getNavbar(), "display1"));
    }

    @Then("I verify login button is disabled")
    public void iVerifyLoginButtonIsDisabled() {
        assertFalse(loginPage.isLoginButtonEnabled());
    }

    @Then("I verify {string} error message is displayed")
    public void iVerifyErrorMessageIsDisplayed(String message) {
        assertEquals(message, commonPageObjects.getErrorMessage());
    }

    @Given("I navigate to {string} >> {string}")
    public void iNavigateTo(String firstPage, String secondPage) {
        getElementByText(firstPage, driver).click();
        getElementByText(secondPage, driver).click();
    }
}

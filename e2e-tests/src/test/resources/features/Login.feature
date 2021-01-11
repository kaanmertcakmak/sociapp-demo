Feature: Login Page Tests

  @successful_login
  Scenario: User should be able to login successfully
    Given I navigate to Login Page
    When I login with following credentials
    |user1|Kaan1234|
    Then I should be successfully logged in


  Scenario: Login button should be disabled when username is empty
    Given I navigate to Login Page
    When I login with following credentials
      |user1| |
    Then I verify login button is disabled

  Scenario: Login button should be disabled when password is empty
    Given I navigate to Login Page
    When I login with following credentials
      ||Kaan1234|
    Then I verify login button is disabled

  Scenario: Proper error message should be displayed when username is incorrect
    Given I navigate to Login Page
    When I login with following credentials
      |userIncorrect|Kaan1234|
    Then I verify "Username is incorrect" error message is displayed

  Scenario: Proper error message should be displayed when password is incorrect
    Given I navigate to Login Page
    When I login with following credentials
      |user1|Kaan12345|
    Then I verify "Password is incorrect" error message is displayed
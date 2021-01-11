Feature: Signup Page Tests

  Background: Navigate to signup page
    Given I navigate to Signup Page

  @delete_user_ui
  Scenario: User should be able to signup successfully
    When I try to signup with following credentials
    |username    |displayName    |password|confirmPassword|
    |UserTest5   |Display        |Kaan1234|Kaan1234       |
    Then I should be successfully registered

  Scenario: Proper error messages should be displayed when User tries to signup with empty fields
    When I try to signup with following credentials
    |username|displayName|password|confirmPassword|
    |        |           |        |               |
    Then I verify "Username can not be null!" is displayed under "username"
    Then I verify "Display name can not be null!" is displayed under "displayName"
    Then I verify "Password can not be null!" is displayed under "password"

  Scenario: Proper error messages should be displayed when passwords do not match
    When I try to signup with following credentials
      |username |displayName|password|confirmPassword|
      |UserTest5|Display    |Kaan1234|Kaan12345      |
    Then I verify "Passwords do not macth!" is displayed under "confirmPassword"
    And I verify signup button is disabled

  Scenario: Proper error messages should be displayed when username is already taken
    When I try to signup with following credentials
      |username |displayName|password|confirmPassword|
      |user1    |Display    |Kaan1234|Kaan1234       |
    Then I verify "This username is already taken" is displayed under "username"

  Scenario: Proper error messages should be displayed when fields sizes are below minimum
    When I try to signup with following credentials
      |username |displayName|password|confirmPassword|
      |a        |b          |Ka1     |Ka1            |
    Then I verify "size must be between 6 and 50" is displayed under "password"
    Then I verify "size must be between 4 and 50" is displayed under "username"
    Then I verify "size must be between 4 and 50" is displayed under "displayName"

  Scenario: Proper error messages should be displayed when password does not match regex
    When I try to signup with following credentials
      |username |displayName|password|confirmPassword|
      |user1    |Display    |Kaanmert|Kaanmert       |
    Then I verify "Password must have atleast 1 uppercase letter, 1 lowercase letter and 1 number" is displayed under "password"
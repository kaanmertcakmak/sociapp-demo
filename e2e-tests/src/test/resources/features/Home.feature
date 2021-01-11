Feature: Homepage test cases

  @login_2 @delete_content_home_page
  Scenario: User should be able to post content with an image
    Given I post a following content with an image
      |This is a test content|
    Then I verify posted content should be displayed in Home page

  Scenario: One should be able to load old contents
    Given I've displayed Home page
    Then I verify user is able to load older contents properly

  @login_2
  Scenario: Logged in user should not be displayed in user list
    Given I've displayed Home page
    And I navigate through user list and verify logged in user does not displayed in User list

  @login_2
  Scenario: Logged in user should be able to navigate other profiles but should not be able to delete them
    Given I've displayed Home page
    And I navigate first content's profile page
    Then I verify Edit and Delete User buttons do not display
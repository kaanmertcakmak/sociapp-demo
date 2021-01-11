Feature: My Profile Page Tests

  @login @revert_update
  Scenario: User should be able to edit displayName
    Given I navigate to "display1" >> "My Profile"
    Then I verify url contains "/user/user1"
    When I try to update display name as "displayNameUpdated"
    Then I verify display name is updated properly

  @login
  Scenario: Proper error message should displayed if new display name is too short
    Given I navigate to "display1" >> "My Profile"
    Then I verify url contains "/user/user1"
    When I try to update display name as "a"
    Then I verify "size must be between 4 and 50" is displayed under "displayNameUpdate"

  @login_2 @delete_content
  Scenario: Created content should be displayed in profile page and user should be able to delete them
    Given I post a following content
    |This is a test content|
    Given I navigate to "display1" >> "My Profile"
    Then I verify url contains "/user"
    And I verify posted content should be displayed in My Profile page

  @login_2 @revert_profile_photo
  Scenario: User should be able to change profile photo
    Given I navigate to "display1" >> "My Profile"
    Then I verify url contains "/user/"
    When I try to update profile photo and verified if it is updated properly
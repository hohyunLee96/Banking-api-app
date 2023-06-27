Feature: Login

    Scenario: Log in with valid email and valid password
        Given I have a valid login object with valid email and valid password
        When I call the application login endpoint
        Then I receive a token response

    Scenario: Log in with valid email and invalid password
        Given I have a valid login object with valid email and invalid password
        When I call the application login endpoint
        Then I receive a 401 response
        And I receive a message that the password is invalid

    Scenario: Log in with invalid email and valid password
        Given I have a valid login object with invalid email and valid password
        When I call the application login endpoint
        Then I receive a 401 response
        And I receive a message that the email is invalid

    Scenario: Log in with invalid email and invalid password
        Given I have a valid login object with invalid email and invalid password
        When I call the application login endpoint
        Then I receive a 401 response
        And I receive a message that credentials are invalid

    Scenario: Log in with valid email and empty password
        Given I have a valid login object with valid email and empty password
        When I call the application login endpoint
        Then I receive a 400 response
        And I receive a message that the password is empty

    Scenario: Log in with empty email and valid password
        Given I have a valid login object with empty email and valid password
        When I call the application login endpoint
        Then I receive a 400 response
        And I receive a message that the email is empty

    Scenario: Log in with empty email and empty password
        Given I have a valid login object with empty email and empty password
        When I call the application login endpoint
        Then I receive a 400 response
        And I receive a message that the email is empty
        And I receive a message that the password is empty

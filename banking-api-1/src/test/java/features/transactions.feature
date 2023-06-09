Feature: Getting all transactions

  Scenario: Getting all transactions
    Given I have a valid token
    When I request to get all transactions
    Then I should get all transactions
    Then I get a status code of 200
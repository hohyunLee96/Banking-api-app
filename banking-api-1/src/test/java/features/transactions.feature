Feature: Getting all transactions

  Scenario: Getting all transactions
    Given I login as an employee
    When I request to get all transactions
    Then I should get all transactions
    Then I get a status code of 200

  Scenario: Getting a single transaction
    Given I login as an employee
    When I request to get a single transaction
    Then I should get a single transaction
    Then I get a status code of 200

  Scenario: Create a transaction with an invalid user
    Given I login with an invalid user
    When I request to create a transaction
    Then I get a status code of 401

  Scenario: Withdraw from selected account
    Given I login as a customer
    When I request to withdraw from selected account 200.00
    Then I get a response object of a transaction with amount 200.00
    Then I get a status code of 201

  Scenario: Withdraw from selected account with insufficient funds
    Given I login as a customer
    And I want to withdraw from current account amount 20000.0
    When I request to withdraw from selected account
    Then I get a status code of 400

  Scenario: Deposit from account with insufficient funds
    Given I login as a customer
    And I want to deposit from current account amount 200000.0
    When I request to deposit from selected account
    Then I get a status code of 400

  Scenario: Customer transfers money to deactivated account
    Given I login as an employee
    And I want to transfer from current account amount 200.0
    When I request to transfer to deactivated account
    Then I get a status code of 400
    And I get an error message of "Receiving account cannot be a CLOSED account."

  Scenario: Employee transfers money from closed account
    Given I login as an employee
    And I want to transfer from current account amount 200.0
    When I request to transfer from deactivated account
    Then I get a status code of 400
    And I get an error message of "Sender account cannot be a CLOSED account."

Scenario: Customer tries to transfer amount 0
    Given I login as a customer
    And I want to transfer from current account amount 0.0
    Then I get a status code of 406
    And I get an error message of "Amount must be greater than 0."

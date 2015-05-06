Feature: subscribing for a signal
  As a developer
  I want to subscribe for a specific signal
  So that I can use it in my application

  @shutdownNode
  Scenario: subscribing for a signal
    Given The dummy application is setup and listening on port 8126 
    And The simulator is setup
    And Add a node to simulator on port 8126 and ip localhost
    And After 1000 mSec have passed
    And The simulator is providing 150 with value 1
    And After 2000 mSec have passed
    And DummyApp subscribes for signal 150
    And After 1000 mSec have passed
    When The simulator changes the signal 150 to 10
    And After 2000 mSec have passed
    Then DummyApp should get a notification for signal 150 with value 10

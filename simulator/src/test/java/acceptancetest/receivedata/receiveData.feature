Feature: subscribing for a signal
  As a developer
  I want to subscribe for a specific signal
  So that I can use it in my application

  @shutdownNode
  Scenario: subscribing for a signal
    Given The dummy server is setup
    And The simulator is setup
    And After 3000 mSec have passed
    And Add a node to simulator on port 8251 and ip localhost
    And After 3000 mSec have passed
    And The dummy server sends the signal 320 with the value 10 as a float
    And After 3000 mSec have passed
    Then The simulator should have received 10 from signal id 320 as a float

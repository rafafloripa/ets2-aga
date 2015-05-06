Feature: subscribing for a signal
  As a developer
  I want the simulator to handle crashing
  So that I can as quickly as possible return to work

  @shutdownNode
  Scenario: subscribing for a signal
    Given The dummy server is setup

    And The server subscribes for 320
    And After 2000 mSec have passed

    And The simulator is setup
    And After 2000 mSec have passed

    And Add a node to simulator on port 8251 and ip localhost
    And After 2000 mSec have passed

    And The simulator is providing signal id 320
    And After 2000 mSec have passed

    And The simulator sends signal 320 as a float with the value 10
    And After 2000 mSec have passed

    And The server should have received 10 from signal id 320 as a float
    And After 2000 mSec have passed

    When The simulator disconnects
    And After 2000 mSec have passed

    And The simulator is setup
    And After 2000 mSec have passed

    And Add a node to simulator on port 8251 and ip localhost
    And After 2000 mSec have passed

    And The simulator is providing signal id 320
    And After 2000 mSec have passed

    And  The simulator sends signal 320 as a float with the value 99
    And After 2000 mSec have passed

    Then The server should have received 99 from signal id 320 as a float
Feature: GXT27 steering wheel signal sending 

  @shutdownNode
  Scenario: Send steering wheel data from the GXT27 to the dummy application
    Given The simulator is setup
    And The dummy application is setup and listening on port 8251
    And After 2000 mSec have passed
    And Add a node to simulator on port 8251 and ip localhost
    And After 3000 mSec have passed
    And The dummy application subscribes for signal id 514
    And After 1000 mSec have passed
	And The simulator is providing signal id 514
    And After 1000 mSec have passed
    When The simulator sends 1 as a steering wheel signal        
    And After 2000 mSec have passed                  
 	Then The dummyApplication should get the value 1 from the steering wheel

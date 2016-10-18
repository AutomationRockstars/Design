# Created by Hannah at 11/06/2016
Feature: Second feature

  @two @common

  Scenario: One Beta
    Given Some data <data>
    When I read it
    Then I see result

  Examples:

  |data|
  |unos|
  |dos|

  Scenario: Two Beta
    Given different <input>
    When I read it
    Then I expect stuff
    And I see result

  Examples:
  |input|
  |ichi|
  |ni|
  |san|
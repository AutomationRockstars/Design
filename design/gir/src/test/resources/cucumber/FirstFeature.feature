Feature: The First Feature

  @One @Common

  Scenario: One Alfa
    Given Some data <data>
    When I read it
    Then I see result

  Examples:

  |data|
  |unos|
  |dos|

  Scenario: Two Alfa
    Given different <input>
    When I read it
    Then I expect stuff

  Examples:
  |input|
  |ichi|
  |ni|
  |san|

Feature: Reporting on Allure
Narrative: This is to present information on Allure report

Scenario: Verify strings can fail

Given simple string
Then expect simple

Scenario: Use table from CSV

Given <in> string
Then expect <out>

Examples:
 	data/inOut_vertical.csv

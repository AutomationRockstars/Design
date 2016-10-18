Narrative: Goal of this one is to verify corner cases of jbehave

Scenario: There is no given

When use some string
Then expect some
When use different string
Then expect different
Given lame string
Then expect lame

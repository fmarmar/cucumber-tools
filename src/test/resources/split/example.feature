@aFeatureTag @anotherFeatureTag
Feature: A feature

@scenarioTag
Scenario: An scenario
	Given the condition
	When do the action
	Then get the result 

@scenarioOutlineTag
Scenario Outline: An scenario outline <number>
	Given the <condition> condition
	When do the action <action>
	Then get the result <result>
	
	Examples:
	| number | condition | action | result |
	| 1      | right     | ok     | ok     |
	| 2      | wrong     | ok     | ko     |
	
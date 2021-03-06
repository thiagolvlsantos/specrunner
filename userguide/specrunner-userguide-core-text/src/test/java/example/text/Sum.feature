# language: en
Feature: Sum

  Background:
  	Clean status

  Scenario: Add a table of values
    When I make a sum of:
	    | Values  |
	    | 20      |
	    | 2       |
	    | 0       |
    and type:
	    """
	    SUM ENTER
	    """
    Then the result is 22.
    
  Scenario: Multiply a table of values 
    When I make a multiplication of:
	    | Values  |
	    | 6       |
	    | 10      |
    and call:
	    """
	    ENTER
	    """
    Then the result is 60.
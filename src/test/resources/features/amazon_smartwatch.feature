Feature: Amazon Smartwatch Shopping
  As a customer
  I want to search for smartwatches on Amazon
  So that I can find and purchase smartwatches from multiple brands within my budget

  Background:
    Given I wait for the Amazon homepage to load completely
    Then I should see the Amazon page title contains "Amazon"

  Scenario Outline: Search and purchase smartwatches from multiple brands
    When I search for "smartwatches" in the search box
    And I click on the search button
    Then I should see search results for smartwatches
    When I apply brand filter for "<brand>"
    And I set price filter with minimum price "<minPrice>" and maximum price "<maxPrice>"
    Then all displayed smartwatches should have prices between "<minPrice>" and "<maxPrice>" rupees
    When I sort the search results by price from high to low
    And I click on the product with the highest price
    Then the product details page should open in a new window
    When I switch to the new product window
    And I verify the product details are displayed
    And I add the product to my cart
    And the cart count should be updated
    When I close the new window and return to main window
    Then I should be back on the search results page

    Examples:
      | brand        | minPrice | maxPrice |
      | Noise        | 1000     | 5000     |
      | boAt         | 1000     | 2000     |
      | Fire-Boltt   | 1500     | 1800     |
      | Fastrack     | 1800     | 2500     |



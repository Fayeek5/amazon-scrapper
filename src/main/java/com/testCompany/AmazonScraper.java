package com.testCompany;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.*;
import java.time.Duration;

public class AmazonScraper {

    public static void main(String[] args) {
        // Set the WebDriver HTTP factory (e.g., Netty or JDK HTTP client)
        System.setProperty("webdriver.http.factory", "netty");

        // Disable WebDriverManager's internal logging
        System.setProperty("wdm.console.level", "OFF");

        // Automatically download and set up Edge WebDriver

        WebDriverManager.edgedriver().setup();
        // Set up EdgeOptions
        EdgeOptions options = new EdgeOptions();
        options.addArguments("--remote-allow-origins=*");

        // Initialize WebDriver with EdgeDriver
        WebDriver driver = new EdgeDriver(options);

        // Initialize WebDriverWait
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            // Open the Amazon website and search for "Lg sound bar"
            driver.get("https://www.amazon.in/");

            // Wait for the search box to be visible
            WebElement searchBox = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("twotabsearchtextbox")));
            searchBox.sendKeys("Lg sound bar");
            searchBox.submit();

            // Wait for the results page to load and ensure the products are visible
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".s-main-slot")));

            // Get all product elements on the first page
            List<WebElement> productElements = driver.findElements(By.cssSelector(".s-main-slot .s-result-item"));

            // Create a map to store product names and prices
            Map<String, Integer> productPriceMap = new HashMap<>();

            // Loop through the products and extract name and price
            for (WebElement product : productElements) {
                try {
                    // Get product name
                    WebElement nameElement = product.findElement(By.cssSelector(".a-size-medium"));
                    String productName = nameElement.getText();

                    // Get product price (if available)
                    WebElement priceElement = product.findElement(By.cssSelector(".a-price-whole"));
                    String priceText = priceElement.getText().replaceAll("[^\\d]", ""); // Remove non-numeric characters

                    // If no price is found, consider it as 0
                    int productPrice = priceText.isEmpty() ? 0 : Integer.parseInt(priceText);

                    // Save the product name and price in the map
                    productPriceMap.put(productName, productPrice);
                } catch (Exception e) {
                    // Handle cases where either name or price is not found, continue with other products
                    continue;
                }
            }

            // Sort products by price in ascending order
            List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(productPriceMap.entrySet());
            sortedEntries.sort(Comparator.comparingInt(Map.Entry::getValue));

            // Print all combinations of product names and prices
            int counter = 1;
            for (Map.Entry<String, Integer> entry : sortedEntries) {
                System.out.println(counter + ". " + entry.getValue() + ", " + entry.getKey());
                counter++;
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            driver.close();
        }
    }
}

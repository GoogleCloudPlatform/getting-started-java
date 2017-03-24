/*
 * Copyright 2017 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.getstarted.basicactions;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.service.DriverService;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

@RunWith(JUnit4.class)
@SuppressWarnings("checkstyle:abbreviationaswordinname")
public class UserJourneyTestIT {

  private static final String TITLE = "mytitle";
  private static final String AUTHOR = "myauthor";
  private static final String PUBLISHED_DATE = "1984-02-27";
  private static final String DESCRIPTION = "mydescription";
  private static final String EMAIL = "userjourneytest@example.com";

  private static DriverService service;
  private WebDriver driver;

  @BeforeClass
  public static void setupClass() throws Exception {
    service = ChromeDriverService.createDefaultService();
    service.start();

  }

  @AfterClass
  public static void tearDownClass() {
    service.stop();
  }

  @Before
  public void setup() {
    driver = new RemoteWebDriver(service.getUrl(), DesiredCapabilities.chrome());
  }

  @After
  public void tearDown() {
    driver.quit();
  }

  private WebElement checkLandingPage() throws Exception {
    WebElement button = driver.findElement(By.linkText("Login"));
    assertTrue(null != button);

    WebElement list = driver.findElement(By.cssSelector("body>.container p"));
    assertEquals("No books found", list.getText());

    return button;
  }

  private WebElement checkLandingPage(String email) throws Exception {
    WebElement logout = driver.findElement(By.linkText(email));
    assertTrue(null != logout);

    WebElement button = driver.findElement(By.cssSelector("a.btn"));
    assertEquals("Add book", button.getText().trim());

    WebElement list = driver.findElement(By.cssSelector("body>.container p"));
    assertEquals("No books found", list.getText());

    return button;
  }

  private void checkAddBookPage() throws Exception {
    List<WebElement> inputContainers = driver.findElements(By.cssSelector("form .form-group"));
    assertTrue("Should have more than 5 inputs", inputContainers.size() > 5);
  }

  private void submitForm(String title, String author, String datePublished, String description)
      throws Exception {
    driver.findElement(By.cssSelector("[name=title]")).sendKeys(title);
    driver.findElement(By.cssSelector("[name=author]")).sendKeys(author);
    driver.findElement(By.cssSelector("[name=publishedDate]")).sendKeys(datePublished);
    driver.findElement(By.cssSelector("[name=description]")).sendKeys(description);

    driver.findElement(By.cssSelector("button[type=submit]")).submit();
  }

  private void checkReadPage(String addedBy) throws Exception {
    assertTrue(driver.findElement(By.cssSelector(".book-added-by")).getText()
        .indexOf(addedBy) > 0);
  }

  private void login(String email) {
    WebElement input = driver.findElement(By.cssSelector("input[type=text]"));
    input.clear();
    input.sendKeys(email);
    input.submit();
  }

  private void logout(String email) {
    WebElement button = driver.findElement(By.linkText(email));
    button.click();
  }

  @Test
  public void userJourney() throws Exception {
    driver.get("http://localhost:8080");

    try {
      WebElement loginButton = checkLandingPage();

      loginButton.click();
      (new WebDriverWait(driver, 10)).until(ExpectedConditions.urlMatches("login"));

      login(EMAIL);
      (new WebDriverWait(driver, 10)).until(ExpectedConditions.urlMatches("/books"));

      WebElement button = checkLandingPage(EMAIL);

      button.click();
      (new WebDriverWait(driver, 10)).until(ExpectedConditions.urlMatches(".*/create$"));

      checkAddBookPage();

      submitForm(TITLE, AUTHOR, PUBLISHED_DATE, DESCRIPTION);
      (new WebDriverWait(driver, 10)).until(ExpectedConditions.urlMatches(".*/read\\?id=[0-9]+$"));

      checkReadPage(EMAIL);

      logout(EMAIL);
      (new WebDriverWait(driver, 10)).until(
          ExpectedConditions.presenceOfElementLocated(By.linkText("Login")));
    } catch (Exception e) {
      System.err.println(driver.getPageSource());
      throw e;
    }
  }
}

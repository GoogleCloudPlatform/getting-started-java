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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.cloud.firestore.QueryDocumentSnapshot;

import java.time.Duration;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.remote.service.DriverService;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/*
 * I can't figure out how to test server-side logging in a selenium test, so just sanity-check that
 * it hasn't broken anything.
 */
@RunWith(JUnit4.class)
@SuppressWarnings("checkstyle:abbreviationaswordinname")
public class UserJourneyTestIT {

  private static final String TITLE = "mytitle";
  private static final String AUTHOR = "myauthor";
  private static final String PUBLISHED_DATE = "1984-02-27";
  private static final String DESCRIPTION = "mydescription";
  private static final String EMAIL = "userjourneytest@example.com";

  private static final String APP_ID = System.getProperty("appengine.appId");
  private static final String APP_VERSION = System.getProperty("appengine.version");
  private static final boolean LOCAL_TEST = null == APP_ID || null == APP_VERSION;

  private static DriverService service;
  private WebDriver driver;

  @BeforeClass
  public static void setupClass() throws Exception {
    service = ChromeDriverService.createDefaultService();
    service.start();
  }

  @AfterClass
  public static void tearDownClass() throws ExecutionException, InterruptedException {
    // Clear the datastore if we're not using the local emulator
    if (!LOCAL_TEST) {
      Firestore firestore = FirestoreOptions.getDefaultInstance().getService();
      for (QueryDocumentSnapshot docSnapshot :
          firestore.collection("books").get().get().getDocuments()) {
        try {
          docSnapshot.getReference().delete().get();
        } catch (InterruptedException | ExecutionException e) {
          e.printStackTrace();
        }
      }
    }

    service.stop();
  }

  @Before
  public void setup() {
    driver = new ChromeDriver();
  }

  @After
  public void tearDown() {
    driver.quit();
  }

  private WebElement checkLandingPage() {
    WebElement button = driver.findElement(By.cssSelector("a.btn"));
    assertEquals("Add book", button.getText());

    WebElement heading = driver.findElement(By.cssSelector("body>.container h3"));
    assertEquals("Books", heading.getText());

    WebElement list = driver.findElement(By.cssSelector("body>.container p"));
    assertEquals("No books found", list.getText());

    WebElement loginButton = driver.findElement(By.linkText("Login"));
    assertNotNull(loginButton);

    return button;
  }

  private WebElement checkLandingPage(String email) {
    WebElement logout = driver.findElement(By.linkText(email));
    assertNotNull(logout);

    WebElement button = driver.findElement(By.cssSelector("a.btn"));
    assertEquals("Add book", button.getText().trim());

    WebElement heading = driver.findElement(By.cssSelector("body>.container h3"));
    assertEquals("Books", heading.getText());

    WebElement list = driver.findElement(By.cssSelector("body>.container p"));
    assertEquals("No books found", list.getText());

    return button;
  }

  private void checkAddBookPage() {
    List<WebElement> inputContainers = driver.findElements(By.cssSelector("form .form-group"));
    assertTrue("Should have more than 5 inputs", inputContainers.size() > 5);
    assertEquals(
        "First input should be Title",
        "Title",
        inputContainers.get(0).findElement(By.tagName("label")).getText());
    assertEquals(
        "Second input should be Author",
        "Author",
        inputContainers.get(1).findElement(By.tagName("label")).getText());
    assertEquals(
        "Third input should be Date Published",
        "Date Published",
        inputContainers.get(2).findElement(By.tagName("label")).getText());
    assertEquals(
        "Fourth input should be Description",
        "Description",
        inputContainers.get(3).findElement(By.tagName("label")).getText());

    // The rest should be hidden
    for (Iterator<WebElement> iter = inputContainers.listIterator(5); iter.hasNext(); ) {
      WebElement el = iter.next();
      assertTrue(el.getAttribute("class").contains("hidden"));
    }
  }

  private void submitForm() {
    driver.findElement(By.cssSelector("[name=title]")).sendKeys(UserJourneyTestIT.TITLE);
    driver.findElement(By.cssSelector("[name=author]")).sendKeys(UserJourneyTestIT.AUTHOR);
    driver
        .findElement(By.cssSelector("[name=publishedDate]"))
        .sendKeys(UserJourneyTestIT.PUBLISHED_DATE);
    driver
        .findElement(By.cssSelector("[name=description]"))
        .sendKeys(UserJourneyTestIT.DESCRIPTION);

    driver.findElement(By.cssSelector("button[type=submit]")).submit();
  }

  private void checkReadPage(String addedBy) {
    WebElement heading = driver.findElement(By.cssSelector("h3"));
    assertEquals("Book", heading.getText());

    List<WebElement> buttons = driver.findElements(By.cssSelector("a.btn"));
    assertEquals(2, buttons.size());
    assertEquals("Edit book", buttons.get(0).getText());
    assertEquals("Delete book", buttons.get(1).getText());

    // Should be a cat thumbnail
    assertTrue(
        driver
            .findElement(By.cssSelector("img.book-image"))
            .getAttribute("src")
            .indexOf("placekitten")
            > 0);
    assertTrue(
        "Should show title",
        driver
            .findElement(By.cssSelector(".book-title"))
            .getText()
            .startsWith(UserJourneyTestIT.TITLE));
    assertEquals(
        "Should show author",
        "By " + UserJourneyTestIT.AUTHOR,
        driver.findElement(By.cssSelector(".book-author")).getText());
    assertEquals(
        "Should show description",
        UserJourneyTestIT.DESCRIPTION,
        driver.findElement(By.cssSelector(".book-description")).getText());

    assertTrue(driver.findElement(By.cssSelector(".book-added-by")).getText().indexOf(addedBy) > 0);
  }

  private void checkBookList() {
    List<WebElement> media = driver.findElements(By.cssSelector("div.media"));
    assertEquals(1, media.size());

    WebElement book = media.get(0);

    assertEquals(UserJourneyTestIT.TITLE, book.findElement(By.tagName("h4")).getText());
    assertEquals(UserJourneyTestIT.AUTHOR, book.findElement(By.tagName("p")).getText());
  }

  private void login() {
    WebElement input = driver.findElement(By.cssSelector("input[type=text]"));
    input.clear();
    input.sendKeys(UserJourneyTestIT.EMAIL);
    input.submit();
  }

  private void logout() {
    WebElement button = driver.findElement(By.linkText(UserJourneyTestIT.EMAIL));
    button.click();
  }

  @Test
  public void userJourney() {
    // Do selenium tests on the deployed version, if applicable
    String endpoint = "http://localhost:8080";
    if (!LOCAL_TEST) {
      endpoint = String.format("https://%s-dot-%s.appspot.com", APP_VERSION, APP_ID);
    }
    System.out.println("Testing endpoint: " + endpoint);
    driver.get(endpoint);

    try {
      WebElement button = checkLandingPage();

      if (LOCAL_TEST) {
        WebElement loginButton = driver.findElement(By.linkText("Login"));
        loginButton.click();
        new WebDriverWait(driver, Duration.ofSeconds(10))
            .until(ExpectedConditions.urlMatches("login")::apply);

        login();
        new WebDriverWait(driver, Duration.ofSeconds(10))
            .until(ExpectedConditions.urlMatches("/books")::apply);

        button = checkLandingPage(EMAIL);

        button.click();
        new WebDriverWait(driver, Duration.ofSeconds(10))
            .until(ExpectedConditions.urlMatches(".*/create$")::apply);

        checkAddBookPage();

        submitForm();
        new WebDriverWait(driver, Duration.ofSeconds(10))
            .until(ExpectedConditions.urlMatches(".*/read\\?id=[0-9]+$")::apply);

        checkReadPage(EMAIL);

        logout();
        new WebDriverWait(driver, Duration.ofSeconds(10))
            .until(ExpectedConditions.presenceOfElementLocated(By.linkText("Login"))::apply);

      } else {
        button.click();
        new WebDriverWait(driver, Duration.ofSeconds(10))
            .until(ExpectedConditions.urlMatches(".*/create$")::apply);

        checkAddBookPage();

        submitForm();
        new WebDriverWait(driver, Duration.ofSeconds(10))
            .until(ExpectedConditions.urlMatches(".*/read\\?id=[0-9]+$")::apply);

        checkReadPage("Anonymous");

        // Now check the list of books for the one we just submitted
        driver.findElement(By.linkText("Books")).click();
        new WebDriverWait(driver, Duration.ofSeconds(10))
            .until(ExpectedConditions.urlMatches(".*/$")::apply);

        checkBookList();
      }
    } catch (Exception e) {
      System.err.println(driver.getPageSource());
      throw e;
    }
  }
}

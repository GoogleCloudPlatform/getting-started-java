/* Copyright 2019 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.getstarted.background;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
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
 * I can't figure out how to test server-side logging in a selenium test, so just confidence-check that
 * it hasn't broken anything.
 */
@RunWith(JUnit4.class)
@SuppressWarnings("checkstyle:abbreviationaswordinname")
@Ignore("Issue #498")
public class UserJourneyTestIT {

  private static final String TEXT = "Hello World!";
  private static final String SOURCE_LANG_CODE = "en";
  private static final String TARGET_LANG_CODE = "es";

  private static DriverService service;
  private WebDriver driver;

  @BeforeClass
  public static void setupClass() throws Exception {
    service = ChromeDriverService.createDefaultService();
    service.start();
  }

  @AfterClass
  public static void tearDownClass() throws ExecutionException, InterruptedException {
    // Clear the firestore list if we're not using the local emulator
    Firestore firestore = FirestoreOptions.getDefaultInstance().getService();
    for (QueryDocumentSnapshot docSnapshot :
        firestore.collection("translations").get().get().getDocuments()) {
      try {
        docSnapshot.getReference().delete().get();
      } catch (InterruptedException | ExecutionException e) {
        e.printStackTrace();
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
    assertEquals("Request Translation", button.getText());

    WebElement heading = driver.findElement(By.cssSelector("body>.container h3"));
    assertEquals("Translations", heading.getText());

    List<WebElement> list = driver.findElements(By.cssSelector("body>.container tr"));
    assertEquals("Should be no entries in translation list.", 1, list.size());

    return button;
  }

  private void checkRequestTranslationPage() {
    List<WebElement> inputContainers = driver.findElements(By.cssSelector("form .form-group"));
    assertTrue("Should have more than 2 inputs", inputContainers.size() > 2);
    assertEquals(
        "First input should be Text",
        "Text",
        inputContainers.get(0).findElement(By.tagName("label")).getText());
    assertEquals(
        "Second input should be Source Language Code",
        "Source Language Code",
        inputContainers.get(1).findElement(By.tagName("label")).getText());
    assertEquals(
        "Third input should be Target Language Code",
        "Target Language Code",
        inputContainers.get(2).findElement(By.tagName("label")).getText());
  }

  private void submitForm() {
    driver.findElement(By.cssSelector("[name=data]")).sendKeys(UserJourneyTestIT.TEXT);
    driver
        .findElement(By.cssSelector("[name=sourceLang]"))
        .sendKeys(UserJourneyTestIT.SOURCE_LANG_CODE);
    driver
        .findElement(By.cssSelector("[name=targetLang]"))
        .sendKeys(UserJourneyTestIT.TARGET_LANG_CODE);

    driver.findElement(By.cssSelector("button[type=submit]")).submit();
  }

  @Test
  public void userJourney() {
    // Do selenium tests on the deployed version, if applicable
    String endpoint = "http://localhost:8080";
    System.out.println("Testing endpoint: " + endpoint);
    driver.get(endpoint);

    try {
      WebElement button = checkLandingPage();

      button.click();
      new WebDriverWait(driver, 10L) // 10 seconds
          .until(ExpectedConditions.urlMatches(".*/create$")::apply);

      checkRequestTranslationPage();

      submitForm();
      new WebDriverWait(driver, 10L)  // 10 seconds
          .until(ExpectedConditions.urlMatches(".*/")::apply);
    } catch (Exception e) {
      System.err.println(driver.getPageSource());
      throw e;
    }
  }
}

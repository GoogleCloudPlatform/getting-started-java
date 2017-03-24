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

import com.google.cloud.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ComparisonFailure;
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@RunWith(JUnit4.class)
@SuppressWarnings("checkstyle:abbreviationaswordinname")
public class UserJourneyTestIT {

  private static final String TITLE = "mytitle";
  private static final String AUTHOR = "myauthor";
  private static final String PUBLISHED_DATE = "1984-02-27";
  private static final String DESCRIPTION = "mydescription";
  private static final String IMAGE_FILENAME = "appengine.png";

  private static DriverService service;
  private WebDriver driver;
  private String filePath;

  @BeforeClass
  public static void setupClass() throws Exception {
    service = ChromeDriverService.createDefaultService();
    service.start();

  }

  @AfterClass
  public static void tearDownClass() {
    // Delete any objects in the bucket
    Storage storage = StorageOptions.getDefaultInstance().getService();
    Page<Blob> blobs = storage.list(System.getProperty("bookshelf.bucket"));
    Iterator<Blob> blobIterator = blobs.iterateAll();
    List<BlobId> blobIds = new ArrayList<BlobId>();
    while (blobIterator.hasNext()) {
      blobIds.add(blobIterator.next().getBlobId());
    }
    storage.delete(blobIds);
    service.stop();
  }

  @Before
  public void setup() {
    driver = new RemoteWebDriver(service.getUrl(), DesiredCapabilities.chrome());
    filePath = this.getClass().getResource(IMAGE_FILENAME).getPath();
  }

  @After
  public void tearDown() {
    driver.quit();
  }

  private WebElement checkLandingPage() throws Exception {
    WebElement button = driver.findElement(By.cssSelector("a.btn"));
    assertEquals("Add book", button.getText().trim());

    WebElement list = driver.findElement(By.cssSelector("body>.container p"));
    assertEquals("No books found", list.getText());

    return button;
  }

  private void checkAddBookPage() throws Exception {
    List<WebElement> inputContainers = driver.findElements(By.cssSelector("form .form-group"));
    assertTrue("Should have more than 5 inputs", inputContainers.size() > 5);
    assertEquals("Fifth input should be Cover Image",
        "Cover Image", inputContainers.get(4).findElement(By.tagName("label")).getText());

    // The rest should be hidden
    for (Iterator<WebElement> iter = inputContainers.listIterator(5); iter.hasNext();) {
      WebElement el = iter.next();
      assertTrue(el.getAttribute("class").indexOf("hidden") >= 0);
    }
  }

  private void submitForm(String title, String author, String datePublished, String description,
      String filePath)
      throws Exception {
    WebElement titleEl = driver.findElement(By.cssSelector("[name=title]"));
    titleEl.sendKeys(title);
    WebElement authorEl = driver.findElement(By.cssSelector("[name=author]"));
    authorEl.sendKeys(author);
    WebElement datePublishedEl = driver.findElement(By.cssSelector("[name=publishedDate]"));
    datePublishedEl.sendKeys(datePublished);
    WebElement descriptionEl = driver.findElement(By.cssSelector("[name=description]"));
    descriptionEl.sendKeys(description);

    WebElement fileEl = driver.findElement(By.cssSelector("[name=file]"));
    fileEl.sendKeys(filePath);

    driver.findElement(By.cssSelector("button[type=submit]")).submit();
  }

  private void checkReadPage(String title, String author, String datePublished, String description)
      throws Exception {
    WebElement heading = driver.findElement(By.cssSelector("h3"));
    assertEquals("Book", heading.getText());

    List<WebElement> buttons = driver.findElements(By.cssSelector("a.btn"));
    assertEquals(2, buttons.size());
    assertEquals("Edit book", buttons.get(0).getText());
    assertEquals("Delete book", buttons.get(1).getText());

    // Should be a cat thumbnail
    assertTrue(driver.findElement(By.cssSelector("img.book-image")).getAttribute("src")
        .indexOf(IMAGE_FILENAME) > 0);
    assertTrue("Should show title",
        driver.findElement(By.cssSelector(".book-title")).getText()
        .startsWith(title));
    assertEquals("Should show author",
        "By " + author, driver.findElement(By.cssSelector(".book-author")).getText());
    assertEquals("Should show description",
        description, driver.findElement(By.cssSelector(".book-description")).getText());

    assertTrue(driver.findElement(By.cssSelector(".book-added-by")).getText()
        .indexOf("Anonymous") > 0);
  }

  private void checkBookList(String title, String author, String datePublished, String description)
      throws Exception {
    List<WebElement> media = driver.findElements(By.cssSelector("div.media"));
    assertEquals(1, media.size());

    WebElement book = media.get(0);

    assertEquals(title, book.findElement(By.tagName("h4")).getText());
    assertEquals(author, book.findElement(By.tagName("p")).getText());
  }

  @Test
  public void userJourney() throws Exception {
    driver.get("http://localhost:8080");

    try {
      WebElement button = checkLandingPage();

      button.click();
      (new WebDriverWait(driver, 10)).until(ExpectedConditions.urlMatches(".*/create$"));

      checkAddBookPage();

      submitForm(TITLE, AUTHOR, PUBLISHED_DATE, DESCRIPTION, filePath);
      (new WebDriverWait(driver, 10)).until(ExpectedConditions.urlMatches(".*/read\\?id=[0-9]+$"));

      checkReadPage(TITLE, AUTHOR, PUBLISHED_DATE, DESCRIPTION);

      // Now check the list of books for the one we just submitted
      driver.findElement(By.linkText("Books")).click();
      (new WebDriverWait(driver, 10)).until(ExpectedConditions.urlMatches(".*/$"));

      checkBookList(TITLE, AUTHOR, PUBLISHED_DATE, DESCRIPTION);
    } catch (ComparisonFailure e) {
      throw new RuntimeException(driver.getPageSource(), e);
    }
  }
}

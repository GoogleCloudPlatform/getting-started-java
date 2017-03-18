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

@RunWith(JUnit4.class)
@SuppressWarnings("checkstyle:abbreviationaswordinname")
public class ListBookServletIT {

  private static DriverService service;
  private WebDriver driver;

  @BeforeClass
  public static void createAndStartService() throws Exception {
    service = ChromeDriverService.createDefaultService();
    service.start();

  }

  @AfterClass
  public static void createAndStopService() {
    service.stop();
  }

  @Before
  public void createDriver() {
    driver = new RemoteWebDriver(service.getUrl(), DesiredCapabilities.chrome());
  }

  @After
  public void quitDriver() {
    driver.quit();
  }

  @Test
  public void pageLoad() throws Exception {
    driver.get("http://localhost:8080");

    WebElement button = driver.findElement(By.cssSelector("a.btn"));
    assertEquals("Add book", button.getText().trim());

    WebElement heading = driver.findElement(By.cssSelector("body>.container h3"));
    assertEquals("Books", heading.getText());

    WebElement list = driver.findElement(By.cssSelector("body>.container p"));
    assertEquals("No books found", list.getText());

    button.click();

    (new WebDriverWait(driver, 10)).until(
        ExpectedConditions.presenceOfElementLocated(By.cssSelector("form")));

    assertEquals(4, driver.findElements(By.cssSelector("form input[type=text]")).size());
  }
}

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

package com.example.appengine;

import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.appengine.pubsub.PubSub;
import com.google.api.core.ApiFuture;
import java.util.List;
import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/** Unit tests for {@link PubSub}. */
@RunWith(JUnit4.class)
public class PubSubTest {
  @Mock private HttpServletRequest mockRequest;
  @Mock private HttpServletResponse mockResponse;
  @Mock RequestDispatcher requestDispatcher;

  private PubSub pubSubServlet;

  @Mock ApiFuture<String> future;

  @Mock List<String> messages;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);
    pubSubServlet = new PubSub();
  }

  @Test
  public void testIndex() {
    when(mockRequest.getRequestDispatcher("index.jsp")).thenReturn(requestDispatcher);
    pubSubServlet.doGet(mockRequest, mockResponse);
    Assert.assertEquals(mockRequest.getRequestDispatcher("index.jsp"), requestDispatcher);
  }

  @Test
  public void testPost() throws Exception {
    when(mockRequest.getParameter("payload")).thenReturn("test");
    when(future.get()).thenReturn("630244882789845");
    pubSubServlet.doPost(mockRequest, mockResponse);
    verify(mockRequest, atLeast(1)).getParameter("payload");
    Assert.assertEquals(future.get(), "630244882789845");
  }
}

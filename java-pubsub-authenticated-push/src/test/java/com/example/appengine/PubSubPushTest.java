package com.example.appengine;

import static org.mockito.Mockito.when;

import com.example.appengine.pubsub.PubSubPush;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/** Unit tests for {@link PubSubPush}. */
@RunWith(JUnit4.class)
public class PubSubPushTest {
  @Mock private HttpServletRequest mockRequest;
  @Mock private HttpServletResponse mockResponse;

  private PubSubPush pubSubPushServlet;

  private String PRIVATE_KEY_BYTES;

  private String PUBLIC_CERT_BYTES;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    pubSubPushServlet = new PubSubPush();
    PRIVATE_KEY_BYTES = readFile("privatekey.pem");
    PUBLIC_CERT_BYTES = readFile("public_cert.pem");
  }

  @Test
  public void testPost() throws Exception {
    String authorization =
        "Bearer "
            + createJWTToken("https://accounts.google.com", "1234567890", new Date().getTime());
    when(mockRequest.getParameter("token")).thenReturn("1234abc");
    when(mockRequest.getHeader("Authorization")).thenReturn(authorization);
    pubSubPushServlet.doPost(mockRequest, mockResponse);
  }

  @Test(expected = Exception.class)
  public void testPostErrors() throws Exception {
    pubSubPushServlet.doPost(mockRequest, mockResponse);
  }

  @Test(expected = Exception.class)
  public void testPostErrorsWithBadToken() throws Exception {
    when(mockRequest.getParameter("token")).thenReturn("bad");
    pubSubPushServlet.doPost(mockRequest, mockResponse);
  }

  private String createJWTToken(String issuer, String subject, long ttlMillis) throws Exception {
    SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.RS256;

    long nowMillis = System.currentTimeMillis();
    Date now = new Date(nowMillis);

    JwtBuilder builder =
        Jwts.builder()
            .setIssuedAt(now)
            .setSubject(subject)
            .setIssuer(issuer)
            .signWith(signatureAlgorithm, getKey());

    if (ttlMillis >= 0) {
      long expMillis = nowMillis + ttlMillis;
      Date exp = new Date(expMillis);
      builder.setExpiration(exp);
    }

    return builder.compact();
  }

  private PrivateKey getKey() throws Exception {
    KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
    kpg.initialize(1024);

    KeyPair keyPair = kpg.genKeyPair();

    byte[] data = PRIVATE_KEY_BYTES.getBytes("UTF8");

    Signature sig = Signature.getInstance("SHA1WithRSA");
    sig.initSign(keyPair.getPrivate());
    sig.update(data);
    sig.initVerify(keyPair.getPublic());
    sig.update(data);
    return keyPair.getPrivate();
  }

  public static String readFile(String path) throws IOException {
    ClassLoader classLoader = new PubSubPushTest().getClass().getClassLoader();

    File file = new File(classLoader.getResource(path).getFile());

    // Read File Content
    String content = new String(Files.readAllBytes(file.toPath()));
    return content;
  }
}

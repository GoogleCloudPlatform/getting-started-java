<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.google.api.client.googleapis.auth.oauth2.GoogleIdToken" %>
<%@ page import="io.jsonwebtoken.Claims" %>
<!doctype html>
<html>
<head>
  <title</title>
</head>
<body>
<%List<GoogleIdToken.Payload> messages = (List)request.getAttribute("messages");%>
<%List<String> tokens = (List)request.getAttribute("tokens");%>
<%List<Claims> claims = (List)request.getAttribute("claims");%>
<div>
  <p>
  <%if(null != messages && messages.size()>0) { %>
  <li>messages:</li>
  <%for (GoogleIdToken.Payload msg:messages) { %>
    <li><%=msg.toString()%></li>
  <%}%>
  <%}%>
  </p>
  <p>
    <%if(null != tokens && tokens.size()>0) { %>
    <li>tokens:</li>
    <%for (String token:tokens) { %>
      <li><%=token%></li>
    <%}%>
    <%}%>
    </p>
  <p>
      <%if(null != claims && claims.size()>0) { %>
      <li>Claims:</li>
      <%for (Claims claim:claims) { %>
        <li><%=claim.toString()%></li>
      <%}%>
      <%}%>
      </p>
  <ul>
  </ul>
  <p><small></small></p>
</div>
<!-- [START form] -->
<form method="post">
  <textarea name="payload" placeholder=""></textarea>
  <input type="submit">
</form>
<!-- [END form] -->
</body>
</html>
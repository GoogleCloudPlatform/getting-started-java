<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<!doctype html>
<html>
<head>
  <title</title>
</head>
<body>
<%List<String> messages = (List)request.getAttribute("messages");%>
<div>
  <p>
  <%if(null != messages && messages.size()>0) { %>
  <li>messages:</li>
  <%for (String msg:messages) { %>
    <li><%=msg%></li>
  <%}%>
  <%}%>
  </p>
  <p>
  </p>
  <p></p>
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
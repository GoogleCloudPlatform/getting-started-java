<!--
Copyright 2019 Google LLC

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
-->
<!-- [START getting_started_background_jsp_list] -->
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<style>
table, th, td {
  border: 1px solid black;
  padding: 5px;
}
</style>
<div class="container">
  <h3>Translations</h3>
  <a href="/create" class="btn btn-success btn-sm">
    <i class="glyphicon glyphicon-plus"></i>
    Request translation
  </a>
  <c:choose>
  <c:when test="${empty messages}">
  <p>No translations found.</p>
  </c:when>
  <c:otherwise>
  <table>
    <tr>
      <th>Timestamp</th>
      <th>Message</th>
      <th>Source Language</th>
      <th>Target Language</th>
      <th>Translation</th>
    </tr>
    <c:forEach items="${messages}" var="message">
    <tr>
      <td>${message.publishTime}</td>
      <td>${fn:escapeXml(message.data)}</td>
      <td>${message.attributes.sourceLang}</td>
      <td>${message.attributes.targetLang}</td>
      <td>${fn:escapeXml(message.translatedText)}</td>
    </tr>
    </c:forEach>
  </table>
  <c:if test="${not empty cursor}">
  <nav>
    <ul class="pager">
      <li><a href="?cursor=${fn:escapeXml(cursor)}">More</a></li>
    </ul>
  </nav>
  </c:if>
  </c:otherwise>
  </c:choose>
</div>
<!-- [END getting_started_background_jsp_list] -->

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
<!-- [START getting_started_background_jsp_form] -->
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<div class="container">
  <h3>
    <c:out value="${action}" /> book
  </h3>

  <form method="POST" action="${destination}">

    <div class="form-group">
      <label for="data">Text</label>
      <input type="text" name="data" id="data" class="form-control" />
    </div>

    <div class="form-group">
      <h4><a href="https://cloud.google.com/translate/docs/languages">See language codes</a></h4>
      <label for="sourceLang">Source Language Code</label>
      <input type="text" name="sourceLang" id="sourceLang" class="form-control" />
    </div>
    <div class="form-group">
      <label for="targetLang">Target Language Code</label>
      <input type="text" name="targetLang" id="targetLang" class="form-control" />
    </div>

    <button type="submit" class="btn btn-success">Submit</button>
  </form>
</div>
<!-- [END getting_started_background_jsp_form] -->

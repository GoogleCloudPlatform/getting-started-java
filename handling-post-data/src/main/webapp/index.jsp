<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<div class="container">
  <h2>
    Create a new blog post
  </h2>

  <form method="POST" action="/create">

    <div>
      <label for="title">Title</label>
      <input type="text" name="blogContent_title" id="title" size="40" value="${fn:escapeXml(title)}" />
    </div>

    <div>
      <label for="author">Author</label>
      <input type="text" name="blogContent_author" id="author" size="40" value="${fn:escapeXml(author)}" />
    </div>

    <div>
      <label for="description">Post content</label>
      <textarea name="blogContent_description" id="description" rows="10" cols="50">${fn:escapeXml(description)}</textarea>
    </div>

    <button type="submit">Save</button>
  </form>
</div>

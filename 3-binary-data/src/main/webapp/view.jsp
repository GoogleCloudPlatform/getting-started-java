<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="en">
  <head>
    <title>Bookshelf - Java on Google Cloud Platform</title>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="//maxcdn.bootstrapcdn.com/bootstrap/3.3.2/css/bootstrap.min.css">
  </head>
  <body>
    <div class="navbar navbar-default">
      <div class="container">
        <div class="navbar-header">
          <div class="navbar-brand">Bookshelf</div>
        </div>
        <ul class="nav navbar-nav">
          <li><a href="/books">Books</a></li>
        </ul>
      </div>
    </div>
    <div class="container">
      <h3>Book</h3>
      <div class="btn-group">
        <a href="/update?id=${book.id}" class="btn btn-primary btn-sm">
          <i class="glyphicon glyphicon-edit"></i>
          Edit book
        </a>
        <a href="/delete?id=${book.id}" class="btn btn-danger btn-sm">
          <i class="glyphicon glyphicon-trash"></i>
          Delete book
        </a>
      </div>

      <div class="media">
        <div class="media-left">
          <img class="book-image" src="${fn:escapeXml(not empty book.imageUrl?book.imageUrl:'http://placekitten.com/g/128/192')}">
        </div>
        <div class="media-body">
          <h4 class="book-title">
            ${fn:escapeXml(book.title)}
            <small>${fn:escapeXml(book.publishedDate)}</small>
          </h4>
          <h5 class="book-author">By ${fn:escapeXml(not empty book.author?book.author:'Unknown')}</h5>
          <p class="book-description">${fn:escapeXml(book.description)}</p>
        </div>
      </div>
    </div>
  </body>
</html>


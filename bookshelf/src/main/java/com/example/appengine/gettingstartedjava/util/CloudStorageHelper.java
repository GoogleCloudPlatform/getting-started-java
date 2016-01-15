/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.appengine.gettingstartedjava.util;

import com.google.common.io.ByteStreams;
import com.google.gcloud.storage.Acl;
import com.google.gcloud.storage.BlobInfo;
import com.google.gcloud.storage.BlobWriteChannel;
import com.google.gcloud.storage.Storage;
import com.google.gcloud.storage.StorageOptions;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.Channels;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

// [START example]
public class CloudStorageHelper {

  private final Logger logger =
      Logger.getLogger(
         com.example.appengine.gettingstartedjava.util.CloudStorageHelper.class.getName());
  private static Storage storage = null;

  public CloudStorageHelper() {
    storage = StorageOptions.defaultInstance().service();
  }

  public String uploadFile(Part filePart) throws IOException {
    DateTimeFormatter dtf = DateTimeFormat.forPattern("-YYYY-MM-dd-HHmmssSSS");
    DateTime dt = DateTime.now(DateTimeZone.UTC);
    String dtString = dt.toString(dtf);
    final String fileName = filePart.getSubmittedFileName() + dtString;
    final String BUCKET_NAME = System.getenv("BUCKET_NAME");

    // Modify access list to allow all users with link to read file
    List<Acl> acls = new ArrayList<>();
    acls.add(new Acl(Acl.User.ofAllUsers(), Acl.Role.READER));
    BlobInfo blobInfo = BlobInfo.builder(BUCKET_NAME, fileName).acl(acls).build();
    InputStream filecontent = filePart.getInputStream();
    BlobWriteChannel blobWriter = storage.writer(blobInfo);
    ByteStreams.copy(filecontent, Channels.newOutputStream(blobWriter));
    blobWriter.close();
    blobInfo = storage.get(BUCKET_NAME, fileName);
    logger.log(
        Level.INFO, "Uploaded file {0} as {1}", new Object[]{
            filePart.getSubmittedFileName(), fileName});
    return blobInfo.mediaLink();
  }

  public String getImageUrl(HttpServletRequest req, HttpServletResponse resp) throws IOException,
      ServletException {
    Part filePart = req.getPart("file");
    final String fileName = filePart.getSubmittedFileName();
    String imageUrl = req.getParameter("imageUrl");
    if (fileName != null && !fileName.isEmpty()) {
      final String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
      String[] allowedExt = { "jpg", "jpeg", "png", "gif" };
      for (String s : allowedExt) {
        if (extension.equals(s)) {
          return this.uploadFile(filePart);
        }
      }
      throw new ServletException("file must be an image");
    }
    return imageUrl;
  }
}
// [END example]

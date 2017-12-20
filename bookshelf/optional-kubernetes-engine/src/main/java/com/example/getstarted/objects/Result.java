/* Copyright 2016 Google Inc.
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

package com.example.getstarted.objects;

import java.util.List;

// [START example]
public class Result<K> {

  public String cursor;
  public List<K> result;

  public Result(List<K> result, String cursor) {
    this.result = result;
    this.cursor = cursor;
  }

  public Result(List<K> result) {
    this.result = result;
    this.cursor = null;
  }
}
// [END example]

#!/usr/bin/env bash

# Copyright 2017 Google Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# Fail on non-zero return and print command to stdout
set -xe

# Jenkins provides values for GOOGLE_CLOUD_PROJECT and GOOGLE_VERSION_ID

# Make sure it works on GAE7

# Deploy and run selenium tests
mvn clean appengine:deploy verify \
  -Pselenium \
  -Dbookshelf.endpoint="https://${GOOGLE_VERSION_ID}-dot-${GOOGLE_CLOUD_PROJECT}.appspot.com" \
  -Dapp.deploy.version="${GOOGLE_VERSION_ID}" \
  -Dapp.deploy.promote=false

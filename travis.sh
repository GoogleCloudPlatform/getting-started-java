#!/usr/bin/env bash
# Copyright 2016 Google Inc. All Rights Reserved.
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

set -e
set -x
# Set pipefail so that `egrep` does not eat the exit code.
set -o pipefail

mvn --batch-mode clean verify | egrep -v "(^\[INFO\] Download|^\[INFO\].*skipping)"

# Test running samples on localhost.
if [[ -n "${GOOGLE_APPLICATION_CREDENTIALS}" ]]; then
  # The bookshelf sample requires Cloud Datastore access. Enable when
  # credentials are available (such as branch PRs).
  ./java-repo-tools/scripts/test-localhost.sh jetty bookshelf -- -Plocal
fi
./java-repo-tools/scripts/test-localhost.sh jetty helloworld-jsp
./java-repo-tools/scripts/test-localhost.sh jetty helloworld-servlet
./java-repo-tools/scripts/test-localhost.sh spring-boot helloworld-springboot


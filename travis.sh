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

# Setup GCP application default credentials before `set -x` echos everything out
if [[ $GCLOUD_SERVICE_KEY ]]; then
  echo "$GCLOUD_SERVICE_KEY" | \
    base64 --decode --ignore-garbage > "${HOME}/google-cloud-service-key.json"
  export GOOGLE_APPLICATION_CREDENTIALS="${HOME}/google-cloud-service-key.json"
fi

set -x
# Set pipefail so that `egrep` does not eat the exit code.
set -o pipefail
shopt -s globstar

mvn --batch-mode clean verify \
  -Dbookshelf.bucket="${GCS_BUCKET-GCS_BUCKET envvar is unset}" | \
  egrep -v "(^\[INFO\] Download|^\[INFO\].*skipping)"

# Test running samples on localhost.
git clone https://github.com/GoogleCloudPlatform/java-repo-tools.git

if [[ -n "${GOOGLE_APPLICATION_CREDENTIALS}" ]]; then
  # The bookshelf sample requires Cloud Datastore access. Enable when
  # credentials are available (such as branch PRs).
  ./java-repo-tools/scripts/test-localhost.sh jetty bookshelf/2-structured-data -- -Plocal -DskipTests=true
  ./java-repo-tools/scripts/test-localhost.sh jetty bookshelf/3-binary-data -- -Plocal -DskipTests=true
  ./java-repo-tools/scripts/test-localhost.sh jetty bookshelf/4-auth -- -Plocal -DskipTests=true
  ./java-repo-tools/scripts/test-localhost.sh jetty bookshelf/5-logging -- -Plocal -DskipTests=true
  ./java-repo-tools/scripts/test-localhost.sh jetty bookshelf/6-gce -- -Plocal -DskipTests=true
else
  # only run unit tests and lint on external PRs
  echo 'External PR: skipping integration tests.'
fi
# ./java-repo-tools/scripts/test-localhost.sh jetty helloworld-jsp -- -DskipTests=true
# ./java-repo-tools/scripts/test-localhost.sh jetty helloworld-servlet -- -DskipTests=true
# ./java-repo-tools/scripts/test-localhost.sh jetty helloworld-compat -- -DskipTests=true
# ./java-repo-tools/scripts/test-localhost.sh spring-boot helloworld-springboot -- -DskipTests=true

# Check that all shell scripts in this repo (including this one) pass the
# Shell Check linter.
shellcheck ./**/*.sh

#!/bin/bash
# Copyright 2017 Google Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

set -eo pipefail
shopt -s globstar

set -xe
# We spin up some subprocesses. Don't kill them on hangup
trap '' HUP

# $1 - project
# $2 - PATH
# $3 - search string
function TestIt() {
  curl -s --show-error "https://${1}-${URL}/${2}" | \
  tee -a "${ERROR_OUTPUT_DIR}/response.txt" | \
  grep "${3}"
  if [ "${?}" -ne 0 ]; then
    echo "${1}/${2} ****** NOT FOUND"
  fi
}

# Temporary directory to store any output to display on error
export ERROR_OUTPUT_DIR
ERROR_OUTPUT_DIR="$(mktemp -d)"
# trap 'rm -r "${ERROR_OUTPUT_DIR}"' EXIT

export GOOGLE_APPLICATION_CREDENTIALS=${KOKORO_GFILE_DIR}/service-acct.json
export GOOGLE_CLOUD_PROJECT=java-docs-samples-testing
export PATH=/google-cloud-sdk/bin:$PATH

echo "******** Environment *********"
env
echo "******** mvn & Java *********"
mvn -version

echo "Update gcloud ********"
gcloud components update --quiet

echo "******** activate-service-account ********"
ls -lr ${KOKORO_GFILE_DIR}

gcloud auth activate-service-account\
    --key-file=$GOOGLE_APPLICATION_CREDENTIALS \
    --project=$GOOGLE_CLOUD_PROJECT

echo "********* gcloud config ********"
gcloud config list

echo "******** build everything ********"
cd github/getting-started-java
mvn -B --fail-at-end -q clean verify  | grep -E -v "(^\[INFO\] Download|^\[INFO\].*skipping)"

echo "******** Deploy to prod *******"
cd appengine-standard-java8

export GOOGLE_CLOUD_PROJECT=lesv-qa-999

gcloud auth activate-service-account\
    --key-file=$GOOGLE_APPLICATION_CREDENTIALS \
    --project=$GOOGLE_CLOUD_PROJECT

./deployAll.sh
echo "******* Test prod Deployed Apps ********"
export URL="dot-lesv-qa-999.appspot.com"

TestIt "helloworld" "" "Hello App Engine -- Java 8!"
TestIt "helloworld" "hello" "Hello App Engine - Standard using Google App Engine"

TestIt "kotlin-appengine-standard" "" \
  "Hello, World! I am a Servlet 3.1 running on Java8 App Engine Standard, and written in Kotlin..."

TestIt "kotlin-springboot-appengine-standard" "greeting" \
  "Hello, World, from a SpringBoot Application written in Kotlin, running on Google App Engine Java8 Standard..."

TestIt "springboot-appengine-standard" "" \
  "Hello world - springboot-appengine-standard!"

TestIt "kotlin-spark-appengine-standard" "" \
  "Hello Spark Kotlin running on Java8 App Engine Standard."

TestIt "kotlin-spark-appengine-standard" "hello" \
  "Hello Spark Kotlin running on Java8 App Engine Standard."

TestIt "sparkjava-appengine-standard" "" \
  "Hello from SparkJava running on GAE Standard Java8 runtime"

echo "******** Success ********"

echo "******** Deploy to QA cluster ********"
export CLOUDSDK_API_ENDPOINT_OVERRIDES_APPENGINE='https://staging-appengine.sandbox.googleapis.com/'

./deployAll.sh

echo "******** Success ********"

echo "STATUS: ${?}"



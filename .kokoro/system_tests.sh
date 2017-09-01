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

# Temporary directory to store any output to display on error
export ERROR_OUTPUT_DIR
ERROR_OUTPUT_DIR="$(mktemp -d)"
# trap 'rm -r "${ERROR_OUTPUT_DIR}"' EXIT
URL="dot-lesv-qa-999.prom-qa.sandbox.google.com"

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

echo "******** Deploy to QA cluster ********"
cd appengine-standard-java8

export GOOGLE_CLOUD_PROJECT=lesv-qa-999
export CLOUDSDK_API_ENDPOINT_OVERRIDES_APPENGINE='https://staging-appengine.sandbox.googleapis.com/'

gcloud auth activate-service-account\
    --key-file=$GOOGLE_APPLICATION_CREDENTIALS \
    --project=$GOOGLE_CLOUD_PROJECT

./deployAll.sh

echo "******** Success ********"


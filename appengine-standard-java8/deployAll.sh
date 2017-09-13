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

# set -x
# set -v
# Temporary directory to store any output to display on error
export ERROR_OUTPUT_DIR="$(mktemp -d)"
# trap 'rm -r "${ERROR_OUTPUT_DIR}"' EXIT

# gcloud config configurations activate qa

for app in "helloworld" "kotlin-appengine-standard" \
      "kotlin-springboot-appengine-standard" \
      "springboot-appengine-standard" "kotlin-spark-appengine-standard" \
      "sparkjava-appengine-standard"
do
  (cd "${app}"; \
      sed "s/<\\runtime>/<\\runtime>\n<service>${app}<\\service>" src/main/webapp/WEB-INF/appengine-web.xml
      mvn -B --fail-at-end -q appengine:deploy -Dapp.deploy.version="1" \
          -Dapp.stage.quickstart=true -Dapp.deploy.force=true -Dapp.deploy.promote=true \
          -Dapp.deploy.project="${GOOGLE_CLOUD_PROJECT}" -DskipTests=true)
done

# $1 - project
# $2 - PATH
# $3 - search string
# function TestIt() {
#   curl -s --show-error "https://${1}-${URL}/${2}" | \
#   tee -a "${ERROR_OUTPUT_DIR}/response.txt" | \
#   grep "${3}"
#   if [ "${?}" -ne 0 ]; then
#     echo "${1}/${2} ****** NOT FOUND"
#   fi
# }
# echo "******* Test QA Deployed Apps ********"
#
# TestIt "helloworld" "" "Hello App Engine -- Java 8!"
# TestIt "helloworld" "hello" "Hello App Engine - Standard using Google App Engine"
#
# TestIt "kotlin-appengine-standard" "" \
#   "Hello, World! I am a Servlet 3.1 running on Java8 App Engine Standard, and written in Kotlin..."
#
# TestIt "kotlin-springboot-appengine-standard" "greeting" \
#   "Hello, World, from a SpringBoot Application written in Kotlin, running on Google App Engine Java8 Standard..."
#
# TestIt "springboot-appengine-standard" "" \
#   "Hello world - springboot-appengine-standard!"
#
# TestIt "kotlin-spark-appengine-standard" "" \
#   "Hello Spark Kotlin running on Java8 App Engine Standard."
#
# TestIt "kotlin-spark-appengine-standard" "hello" \
#   "Hello Spark Kotlin running on Java8 App Engine Standard."
#
# TestIt "sparkjava-appengine-standard" "" \
#   "Hello from SparkJava running on GAE Standard Java8 runtime"

echo "STATUS: ${?}"

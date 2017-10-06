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

# gcloud config configurations activate qa

for app in "helloworld" "kotlin-appengine-standard" \
      "kotlin-springboot-appengine-standard" \
      "springboot-appengine-standard" "kotlin-spark-appengine-standard" \
      "sparkjava-appengine-standard"
do
  (cd "${app}"
      sed --in-place='.xx' "s/<\/runtime>/<\/runtime><service>${app}<\/service>/" \
          src/main/webapp/WEB-INF/appengine-web.xml
      mvn -B --fail-at-end -q appengine:deploy -Dapp.deploy.version="1" \
          -Dapp.stage.quickstart=true -Dapp.deploy.force=true -Dapp.deploy.promote=true \
          -Dapp.deploy.project="${GOOGLE_CLOUD_PROJECT}" -DskipTests=true
      mv src/main/webapp/WEB-INF/appengine-web.xml.xx src/main/webapp/WEB-INF/appengine-web.xml)
done

echo "STATUS: ${?}"

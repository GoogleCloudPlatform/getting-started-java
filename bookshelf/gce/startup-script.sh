#! /bin/bash
# Licensed under the Apache License, Version 2.0 (the "License"); you
#  may not use this file except in compliance with the License. You may obtain
#  a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless
#  required by applicable law or agreed to in writing, software distributed
#  under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
#  OR CONDITIONS OF ANY KIND, either express or implied. See the License for
#  the specific language governing permissions and limitations under the License.
#  See accompanying LICENSE file.

set -v

# Talk to the metadata server to get the project id
PROJECTID=$(curl -s "http://metadata.google.internal/computeMetadata/v1/project/project-id" -H "Metadata-Flavor: Google")
BUCKET=$(curl -s "http://metadata.google.internal/computeMetadata/v1/instance/attributes/BUCKET" -H "Metadata-Flavor: Google")

# get our file(s)
gsutil cp gs://${BUCKET}/gce/** .

# Install dependencies from apt
apt-get update
apt-get install  -yq openjdk-8-jdk git build-essential supervisor maven wget netbase ca-certificates

# Make Java8 the default
update-alternatives --set java /usr/lib/jvm/java-8-openjdk-amd64/jre/bin/java

useradd -m -d /home/jetty jetty

wget -q https://storage.googleapis.com/cloud-debugger/compute-java/format_env_gce.sh
chmod +x format_env_gce.sh

# Install app dependencies
mkdir -p /opt/jetty

curl -L http://eclipse.org/downloads/download.php?file=/jetty/stable-9/dist/jetty-distribution-9.3.8.v20160314.tar.gz\&r=1 -o /opt/jetty9.tgz
tar xvf /opt/jetty9.tgz  --strip-components=1 -C /opt/jetty
cp  /opt/jetty/bin/jetty.sh /etc/init.d/jetty

# Make sure the javaapp user owns the application code
chown -R jetty:jetty /opt/jetty

JETTY_HOME=/opt/jetty/

mv bookshelf-1.0-SNAPSHOT.war /opt/jetty/webapps/

# Install logging monitor. The monitor will automatically pickup logs sent to
# syslog.
curl -s "https://storage.googleapis.com/signals-agents/logging/google-fluentd-install.sh" | bash
service google-fluentd restart &


# CDBG_ARGS="$( sudo ./format_env_gce.sh --app_class_path=ZZZZZZ.jar )"
# java ${CDBG_ARGS} -cp sparky/hellosparky-1.0-SNAPSHOT-jar-with-dependencies.jar com.example.hellosparky.App


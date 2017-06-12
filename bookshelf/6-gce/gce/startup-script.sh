#! /bin/bash
# Copyright 2016 Google Inc.
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

# [START script]
set -e
set -v

# Talk to the metadata server to get the project id
PROJECTID=$(curl -s "http://metadata.google.internal/computeMetadata/v1/project/project-id" -H "Metadata-Flavor: Google")
BUCKET=$(curl -s "http://metadata.google.internal/computeMetadata/v1/instance/attributes/BUCKET" -H "Metadata-Flavor: Google")

echo "Project ID: ${PROJECTID}  Bucket: ${BUCKET}"

# get our file(s)
gsutil cp "gs://${BUCKET}/gce/"** .

# Install dependencies from apt
apt-get update
apt-get install -t jessie-backports -yq openjdk-8-jdk

# Make Java8 the default
update-alternatives --set java /usr/lib/jvm/java-8-openjdk-amd64/jre/bin/java

# Jetty Setup
mkdir -p /opt/jetty/temp
mkdir -p /var/log/jetty

# Get Jetty
curl -L https://repo1.maven.org/maven2/org/eclipse/jetty/jetty-distribution/9.3.8.v20160314/jetty-distribution-9.3.8.v20160314.tar.gz -o jetty9.tgz
tar xf jetty9.tgz  --strip-components=1 -C /opt/jetty

# Add a Jetty User
useradd --user-group --shell /bin/false --home-dir /opt/jetty/temp jetty

cd /opt/jetty
# Add running as "jetty"
java -jar /opt/jetty/start.jar --add-to-startd=setuid
cd /

# very important - by renaming the war to root.war, it will run as the root servlet.
mv bookshelf-1.0-SNAPSHOT.war /opt/jetty/webapps/root.war

# Make sure "jetty" owns everything.
chown --recursive jetty /opt/jetty

# Configure the default paths for the Jetty service
cp /opt/jetty/bin/jetty.sh /etc/init.d/jetty
echo "JETTY_HOME=/opt/jetty" > /etc/default/jetty
{
  echo "JETTY_BASE=/opt/jetty"
  echo "TMPDIR=/opt/jetty/temp"
  echo "JAVA_OPTIONS=-Djetty.http.port=80"
  echo "JETTY_LOGS=/var/log/jetty"
} >> /etc/default/jetty

# -Dorg.eclipse.jetty.util.log.class=org.eclipse.jetty.util.log.JavaUtilLog

# Reload daemon to pick up new service
systemctl daemon-reload

# Install logging monitor. The monitor will automatically pickup logs sent to syslog.
curl -s "https://storage.googleapis.com/signals-agents/logging/google-fluentd-install.sh" | bash
service google-fluentd restart &

service jetty start
service jetty check

echo "Startup Complete"
# [END script]

#wget -q https://storage.googleapis.com/cloud-debugger/compute-java/format_env_gce.sh
#chmod +x format_env_gce.sh
# CDBG_ARGS="$( sudo ./format_env_gce.sh --app_class_path=ZZZZZZ.jar )"
# java ${CDBG_ARGS} -cp sparky/hellosparky-1.0-SNAPSHOT-jar-with-dependencies.jar com.example.hellosparky.App


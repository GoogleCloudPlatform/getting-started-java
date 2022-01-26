#! /bin/bash
# Copyright 2019 Google LLC
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

echo "Project ID: ${PROJECTID}"

# Install dependencies from apt
apt-get install -yq openjdk-11-jdk git maven

mvn --version

# Jetty Setup
mkdir -p /opt/jetty/temp
mkdir -p /var/log/jetty

# Get Jetty
curl -L https://repo1.maven.org/maven2/org/eclipse/jetty/jetty-distribution/9.4.13.v20181111/jetty-distribution-9.4.13.v20181111.tar.gz -o jetty9.tgz
tar xf jetty9.tgz  --strip-components=1 -C /opt/jetty

# Add a Jetty User
useradd --user-group --shell /bin/false --home-dir /opt/jetty/temp jetty

cd /opt/jetty
# Add running as "jetty"
java -jar /opt/jetty/start.jar --add-to-startd=setuid
cd /

# Clone the source repository.
git clone https://github.com/GoogleCloudPlatform/getting-started-java
cd getting-started-java/gce

# Build the .war file and rename.
# very important - by renaming the war to root.war, it will run as the root servlet.
mvn clean package -q
mv target/getting-started-gce-1.0-SNAPSHOT.war /opt/jetty/webapps/root.war

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

# Reload daemon to pick up new service
systemctl daemon-reload

# Install logging monitor. The monitor will automatically pickup logs sent to syslog.
curl -sSO https://dl.google.com/cloudagents/add-logging-agent-repo.sh
sudo bash add-logging-agent-repo.sh --also-install

service google-fluentd restart &

service jetty start
service jetty check

echo "Startup Complete"
# [END script]

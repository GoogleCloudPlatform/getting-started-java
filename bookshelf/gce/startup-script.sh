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

echo "Project ID: ${PROJECTID}  Bucket: ${BUCKET}"

# get our file(s)
gsutil cp gs://${BUCKET}/gce/** .

# Install dependencies from apt
apt-get update
apt-get install  -yq openjdk-8-jdk

# Make Java8 the default
update-alternatives --set java /usr/lib/jvm/java-8-openjdk-amd64/jre/bin/java

wget -q https://storage.googleapis.com/cloud-debugger/compute-java/format_env_gce.sh
chmod +x format_env_gce.sh

# Alternative Jetty Setup
mkdir -p /opt/jetty
mkdir -p /opt/jetty/temp

curl -L http://eclipse.org/downloads/download.php?file=/jetty/stable-9/dist/jetty-distribution-9.3.8.v20160314.tar.gz\&r=1 -o jetty9.tgz
tar xvf jetty9.tgz  --strip-components=1 -C /opt/jetty

useradd --user-group --shell /bin/false --home-dir /opt/jetty/temp jetty

cd /opt/jetty
ls
java -jar start.jar --list-modules
java -jar start.jar --list-config

java -jar /opt/jetty/start.jar --add-to-startd=setuid
ls -F

cd /

mv bookshelf-1.0-SNAPSHOT.war /opt/jetty/webapps/root.war

grep jetty.http.port /opt/jetty/start.ini

chown --recursive jetty /opt/jetty

cp /opt/jetty/bin/jetty.sh /etc/init.d/jetty
echo "JETTY_HOME=/opt/jetty" > /etc/default/jetty
echo "JETTY_BASE=/opt/jetty" >> /etc/default/jetty
echo "TMPDIR=/opt/jetty/temp" >> /etc/default/jetty

service jetty status


# Install logging monitor. The monitor will automatically pickup logs sent to
# syslog.
curl -s "https://storage.googleapis.com/signals-agents/logging/google-fluentd-install.sh" | bash
service google-fluentd restart &

service jetty start
service jetty check

# CDBG_ARGS="$( sudo ./format_env_gce.sh --app_class_path=ZZZZZZ.jar )"
# java ${CDBG_ARGS} -cp sparky/hellosparky-1.0-SNAPSHOT-jar-with-dependencies.jar com.example.hellosparky.App


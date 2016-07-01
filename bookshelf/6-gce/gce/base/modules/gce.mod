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
#
# GCE Mdoule
#

[depend]
resources
server

[optional]

[ini-template]

## Google Defaults
jetty.httpConfig.outputAggregationSize=32768
jetty.httpConfig.headerCacheSize=512

jetty.httpConfig.sendServerVersion=true
jetty.httpConfig.sendDateHeader=false

#gae.httpPort=80
#gae.httpsPort=443

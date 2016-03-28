#
# GAE Module for Jetty 9 MVM Image
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

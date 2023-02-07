#!/bin/bash

clj -J-Dlog4j2.configurationFile="/Users/tstout/src/conf-service/resources/log4j2.xml" \
-M:sys-loader \
server
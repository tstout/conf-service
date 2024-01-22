#!/bin/bash

clojure -J-Dlog4j2.configurationFile="$HOME/src/conf-service/resources/log4j2.xml" \
-M:conf-service \
server

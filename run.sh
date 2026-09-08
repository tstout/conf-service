#!/bin/bash

clojure -J-Dsys-loader.repl-port=8001 -M:conf-service server

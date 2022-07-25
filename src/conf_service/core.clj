(ns conf-service.core
  (:require [clojure.tools.cli :refer [parse-opts]]
            [sys-loader.core :as sys])
  (:gen-class))

;; TODO - see if uberjar can work without this
(defn -main [& args]
  (sys/-main args))

(ns conf-service.core
  (:require [clojure.tools.cli :refer [parse-opts]]
            [sys-loader.core :as sys])
  (:gen-class))

;; TODO - see if uberjar can work without this
(defn -main [& args]
  (sys/-main args))

(defn init-schema [run-ddl]
  (run-ddl "init-schema"))

(defn init [state]
  (prn "conf-init invoked-----------")
  (let [migrate (-> :sys/migrations state)]
    (migrate #'init-schema)))


(comment
  (System/getenv "FOO_BAR")


  ;;
  )
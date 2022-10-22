(ns conf-service.core
  (:require [clojure.tools.cli :refer [parse-opts]]
            [sys-loader.core :as sys]
            [clojure.pprint :refer [pprint]])
  (:gen-class))

(defn prn-modules
  "Print modules from class path. Intended for repl debugging of classpath issues."
  []
  (let [modules (.getResources (ClassLoader/getSystemClassLoader) "module.edn")]
    (while (.hasMoreElements modules)
      (prn (.. modules nextElement)))))


;; TODO - see if uberjar can work without this
(defn -main [& args]
  (prn-modules)
  (sys/-main args))

(defn init-schema [run-ddl]
  (run-ddl "init-schema")
  (run-ddl "name-table"))

(defn add-name-tbl [run-ddl]
  (run-ddl "name-table"))

(defn rename-col [run-ddl]
  (run-ddl "rename-name-col"))

(defn init [state] 
  (let [migrate (-> :sys/migrations state)]
    (migrate #'init-schema)
    (migrate #'add-name-tbl)
    (migrate #'rename-col)))


(comment 
  (pprint @sys/sys-state)
  (prn-modules)
  (init @sys/sys-state)


  (-> @sys/sys-state
      :ring-module
      bean)


  #'comment

  ;;
  )
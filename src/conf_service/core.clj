(ns conf-service.core
  (:require [clojure.tools.cli :refer [parse-opts]]
            [clojure.pprint :refer [print-table]]
            [sys-loader.core :as sys]
            [clojure.string :as string]
            [taoensso.timbre :as log]
            [conf-service.client :refer [mk-account fetch-account]])
  (:gen-class))

(defn prn-modules
  "Print modules from class path. Intended for repl debugging of classpath issues."
  []
  (let [modules (.getResources (ClassLoader/getSystemClassLoader) "module.edn")]
    (while (.hasMoreElements modules)
      (prn (.. modules nextElement)))))

(def cli-options
  [;; First three strings describe a short-option, long-option with optional
   ;; example argument description, and a description. All three are optional
   ;; and positional.
   ["-a" "--account account-edn" (format "'%s'"
                                         (str {:name "boa-chk" :path "bank.boa" :user "foo2" :pass "bar2"}))]
   ["-p" "--path PATH" "account path"]
   ["-e" "--encrypt"]
   ["-d" "--decrypt"]
   ["-s" "--server"]
   ["-u" "--url URL" "Root URL for conf service" :default "http://localhost:8080/v1/config/"]
   ["-h" "--help"]])

(defn usage [options-summary]
  (->> ["conf-service tool"
        ""
        "Usage: cmd [options]"
        ""
        "Commands:"
        "add         - add account"
        "fetch       - lookup account by path"
        "server      - run server"
        ""
        "Options:"
        options-summary
        ""]
       (string/join \newline)))

(defn error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (string/join \newline errors)))

(defn validate-args
  "Validate command line arguments. Either return a map indicating the program
  should exit (with an error message, and optional ok status), or a map
  indicating the action the program should take and the options provided."
  [args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
    (cond
      (:help options) ; help => exit OK with usage summary
      {:exit-message (usage summary) :ok? true}
      errors ; errors => exit with description of errors
      {:exit-message (error-msg errors)}
      ;; custom validation on arguments
      (#{"add" "server" "fetch"} (first arguments))
      {:action (first arguments) :options options}
      :else ; failed custom validation => exit with usage summary
      {:exit-message (usage summary)})))


(defn exit [status msg]
  (println msg)
  (System/exit status))


(defn add-account [options]
  (let [{:keys [encrypt url account]} options]
    (mk-account (str url "account") account)))

(defn load-account [options]
  (let [{:keys [decrypt url path]} options
        account (fetch-account (str url "account/" path))]
    (print-table account)))


;; TODO - see if uberjar can work without this
(defn -main [& args]
  ;;(prn-modules)
  (let [{:keys [action options exit-message ok?]} (validate-args args)]
    (if exit-message
      (exit (if ok? 0 1) exit-message)
      (case action
        "server" (sys/-main args)
        "add"    (add-account options)
        "fetch"  (load-account options)))))

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


  (-main (into-array ["fetch" "-p" "a.b.c"]))

  (type (into-array ["a" "b" "c"]))

  (-> @sys/sys-state
      :ring-module
      bean)


  (meta #'comment)

  ;;
  )
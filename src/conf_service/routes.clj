(ns conf-service.routes
  (:require [ring-module.router :refer [router register-uri-handler reset-registry!]]
            [clojure.string :refer [split starts-with?]]
            [sys-loader.core :refer [sys-state]]
            [clojure.java.io :as io]
            [clojure.edn :as edn]
            [ring.util.response :refer [bad-request
                                        resource-response
                                        response
                                        created]]
            [conf-service.db-io :refer [upsert-account
                                        upsert-tag
                                        next-seq-val
                                        select-account
                                        new-named-account]]))

;; TODO - referencing global state here is a little ugly.
(def data-source
  (delay
    (-> @sys-state :sys/db :data-source)))

(register-uri-handler (fn [uri]
                        (let [path "/v1/config/account"]
                          (when (starts-with? uri path)
                            path))))

(defn extract-path [uri]
  (last (split uri #"/")))

(defmethod router ["/v1/config/account" :get] [request]
  (let [{:keys [uri query-string]} request]
    (-> {:ds @data-source
         :path (extract-path uri)}
        select-account
        str
        response)))

(defn mk-account [body]
  (->> body
       slurp
       edn/read-string
       (merge {:ds @data-source})
       new-named-account))

(defmethod router ["/v1/config/account" :post] [request]
  (let [{:keys [body]} request]
    (->> body
         mk-account
         (str "account/")
         created)))

(defn mk-reader [x]
  (-> x
      char-array
      io/reader))


(comment
  *e
  (extract-path "/v1/config/account/a-b-c")
  (reset-registry!)
  @data-source
  data-source

  (-> "{:a 1 :b 2}"
      char-array
      io/reader
      slurp
      edn/read-string)


  (-> "{:}")


  ;;
  )
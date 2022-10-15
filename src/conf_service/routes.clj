(ns conf-service.routes
  (:require [ring-module.router :refer [router register-uri-handler reset-registry!]]
            [clojure.string :refer [split starts-with?]]
            [sys-loader.core :refer [sys-state]]
            [ring.util.response :refer [bad-request resource-response response]]
            [conf-service.db-io :refer [upsert-account
                                        upsert-tag
                                        next-seq-val
                                        select-account]]))

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
        
(comment
  *e
  (extract-path "/v1/config/account/a-b-c")
  (reset-registry!)
  @data-source
  data-source
  ;;
  )
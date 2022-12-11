(ns conf-service.routes
  (:require [ring-module.router :refer [router
                                        register-uri-handler
                                        reset-registry!]]
            [clojure.string :refer [split starts-with?]]
            [sys-loader.core :refer [sys-state]]
            [clojure.java.io :as io]
            [clojure.edn :as edn]
            [ring.util.response :refer [response
                                        created
                                        not-found]]
            [conf-service.db-io :refer [select-account
                                        new-named-account]]))

;; TODO - referencing global state here is a little ugly.
(def data-source
  (delay
    (-> @sys-state :sys/db :data-source)))

(defn config-routes []
  (register-uri-handler (fn [uri]
                          (let [path "/v1/config/account"]
                            (when (starts-with? uri path)
                              path)))))

;; TODO - the name for looking up a config entity is 
;; currently simply the last path param of the URI.
;; Consider other options here.
(defn extract-path [uri]
  (last (split uri #"/")))

(defmethod router ["/v1/config/account" :get] [request]
  (let [{:keys [uri]} request
        account  (-> {:ds @data-source
                      :path (extract-path uri)}
                     select-account)]
    (if (empty? account)
      (not-found nil)
      (-> account str response))))

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

(comment
  *e
  (extract-path "/v1/config/account/a-b-c")
  (reset-registry!)
  @data-source
  data-source

  (not-found nil)

  (-> "{:a 1 :b 2}"
      char-array
      io/reader
      slurp
      edn/read-string)

  (time (-> "{:a 1 :b 2}"
            char-array
            io/reader
            slurp
            edn/read-string))

  ;;
  )
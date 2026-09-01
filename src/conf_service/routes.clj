(ns conf-service.routes
  (:require [ring-module.router :refer [router
                                        register-uri-handler
                                        reset-registry!]]
            [clojure.string :refer [split starts-with?]]
            [clojure.tools.logging :as log]
            [sys-loader.bootstrap :refer [sys-state]]
            [clojure.java.io :as io]
            [clojure.edn :as edn]
            [http-jdk.server :refer [mk-http-server]]
            [ring.util.response :refer [response
                                        created
                                        not-found]]
            [conf-service.db-io :refer [select-account
                                        new-named-account]]))

;; TODO - referencing global state here is a little ugly.
(def data-source
  (delay
    (-> @sys-state :sys/db :data-source)))

(defn mk-account [body]
  (->> body 
       edn/read-string
       #_(tap-step :mk-account)
       (merge {:ds @data-source})
       new-named-account))

(defn account-get [req]
  (let [{:keys [path-params]} req
        account  (-> {:ds @data-source
                      :path (:acct-name path-params)}
                     select-account)]
    (if (empty? account)
      {:status 404
       :body   (format "account-not-found for req %s" (str req))
       :headers ""}
      {:status 200
       :body   (str account)
       :headers ""})))

(defn account-post [req]
  (let [{:keys [body]} req
        account        (->> body
                            mk-account
                            (str "account/")
                            created)] 
    (if (empty? account)
      {:status  404
       :body    nil
       :headers ""}
      {:status  200
       :body    (str account)
       :headers ""})))

(defn config-routes []
  #_(register-uri-handler (fn [uri]
                          (let [path "/v1/config/account"]
                            (when (starts-with? uri path)
                              path))))
  (let [server (mk-http-server 
                :port 8081 
                :host "0.0.0.0")]
    ;; add routes
    (server :add-route 
            "/v1/config/account"
            (fn [req]
              (let [{:keys [method body]} req]
                (case method
                  "GET" (account-get req)
                  "POST" (do #_(tap-step :account-post req)
                             (-> req account-post)))))
            "v1/config/account/{acct-name}")
    (server :start)
    {:http-server server}))

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

(defmethod router ["/v1/config/account" :post] [request]
  (let [{:keys [body]} request]
    (->> body
         mk-account
         (str "account/")
         created)))

(comment
  *e 
  @sys-state

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
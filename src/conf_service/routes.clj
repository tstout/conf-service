(ns conf-service.routes
  (:require [clojure.tools.logging :as log]
            [sys-loader.bootstrap :refer [sys-state]] 
            [clojure.edn :as edn]
            [http-jdk.server :refer [mk-http-server]]
            [http-jdk.response :refer [created]] 
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

;; TODO define created response and process headers
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
  (let [server (mk-http-server 
                :port 8080
                :host "0.0.0.0")]
    (log/info "route added GET/POST /v1/config/account/{acct-name}")
    (server :add-route 
            "/v1/config/account"
            (fn [req]
              (let [{:keys [method body]} req]
                (case method
                  :get  (account-get req)
                  :post (do #_(tap-step :account-post req)
                         (-> req account-post)))))
            "v1/config/account/{acct-name}")
    (server :start)
    {:http-server server}))

(comment
  *e
  @sys-state 
  (-> @sys-state :sys/http-server :http-server :port)
  (-> @sys-state :sys/http-server :http-server :host)
  (-> @sys-state :sys/http-server :http-server :server)
  ;;
  )
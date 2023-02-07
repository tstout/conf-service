(ns conf-service.client
  (:require [clojure.edn :as edn]
            [clojure.tools.logging :as log])
  (:import [java.net.http
            HttpClient
            HttpRequest
            HttpResponse$BodyHandlers
            HttpRequest$BodyPublishers]
           [java.net URI]))

(defn get-request [uri]
  (-> (HttpRequest/newBuilder)
      .GET
      (.uri (URI/create uri))
      (.setHeader "User-Agent" "Java 11+")
      .build))

(defn post-request [uri body]
  (-> (HttpRequest/newBuilder)
      (.uri (URI/create uri))
      (.POST (HttpRequest$BodyPublishers/ofString (str body)))
      (.setHeader "User-Agent" "Java 11+")
      .build))

(defn http-tx
  "Transmit an http request."
  [req]
  (-> (HttpClient/newHttpClient)
      (.send req (HttpResponse$BodyHandlers/ofString))))

(defn mk-account [body uri]
  (-> uri
      (post-request body)
      http-tx))

(defn fetch-account [uri]
  (log/infof "fetch-acccount %s" uri)
  (-> uri
      get-request
      http-tx
      .body
      edn/read-string))

(comment
  *e
  (fetch-account "http://localhost:8080/v1/config/account/a.b.c")

  (time (fetch-account "http://localhost:8080/v1/config/account/a.b.c"))

  (mk-account
   {:name "a.b.c.e"
    :path "a.b.c"
    :description "test account"
    :user "foo2"
    :pass "bar2"}
   "http://localhost:8080/v1/config/account")

  ;;
  )

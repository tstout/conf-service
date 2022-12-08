(ns conf-service.client
  (:require [clojure.edn :as edn]
            [taoensso.timbre :as log])
  (:import [java.net.http
            HttpClient
            HttpRequest
            HttpResponse$BodyHandlers
            HttpRequest$BodyPublishers]
           [java.net URI]))

;; Java 11 http client interop here is a bit unsightly, but I did not want to 
;; have a dependency on an external lib for such basic http functionality when 
;; JRE now provides it natively.

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

(defn mk-account [uri body]
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
   "http://localhost:8080/v1/config/account"
   {:name "a.b.c.e"
    :path "a.b.c"
    :description "test account"
    :user "foo2"
    :pass "bar2"})

  ;;
  )

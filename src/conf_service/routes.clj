(ns conf-service.routes
  (:require [ring-module.router :refer [router]]
            [clojure.string :refer [split]]
            [sys-loader.core :refer [sys-state]] 
            [ring.util.response :refer [bad-request resource-response response]]
            [conf-service.db-io :refer [upsert-account
                                        upsert-tag
                                        next-seq-val
                                        select-account]]))

;; TODO - referencing global state here is a little ugly.
(def data-source (-> @sys-state :sys/db :data-source))

(defn q-arg [q-str]
  (->> (split q-str #"=")
       seq
      (into {})))


(defmethod router ["/v1/config" :get] [request]
  (let [{:keys [uri query-string]} request]
    (select-account {:ds data-source})
    (response (str {:data (select-keys request [:uri :query-string])}))))


(comment
  *e

  (clojure.string/split "path=a.b.c" #"=")
  

  (def q-str (clojure.string/split "path=a.b.c" #"="))

  q-str
  (first q-str)
  (rest  q-str)

  (let [[k v] q-str]
    (format "%s=%s" k v))
  
  (q-str 1)

  (for [[k v] q-str] (prn k " " v))


  (split "x=y" #"=")

  zipmap


  (seq (vec [:a 1]))

  (into {} (vec [:a 1]))
  (q-arg "x=y")

  ;;
  )
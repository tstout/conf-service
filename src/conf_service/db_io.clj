(ns conf-service.db-io
  (:require [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]
            [clojure.java.io :as io]
            [taoensso.timbre :as log]))

(def sql-text
  "Load SQL text from resource. SQL resource file must exist at 
   sql/queries/<q-name>.sql"
  (memoize
   (fn [q-name]
     (-> (str "sql/queries/" (name q-name) ".sql")
         io/resource
         slurp))))

(defn next-seq-val
  "Get the next value from the named sequence"
  [seq-name ds]
  (log/infof "next-seq-val seq-name: %s" seq-name)
  (with-open [conn (jdbc/get-connection ds)]
    (-> conn
        (sql/query [(format "select next value for %s" seq-name)])
        first
        vals
        last)))

(defn upsert-account
  "Upsert an account row."
  [opts]
  {:pre [(map? opts)]}
  (let [{:keys [ds name description id user pass]} opts]
    (log/infof "updating account with id %d" id)
    (with-open [conn (jdbc/get-connection ds)]
      (jdbc/execute! conn
                     [(sql-text :account-merge)
                      id
                      name
                      description
                      user
                      pass]))))

(defn insert-name [opts]
  {:pre [(map? opts)]}
  (let [{:keys [ds name id]} opts]
    (log/infof "updating account with id %d" id)
    (with-open [conn (jdbc/get-connection ds)]
      (sql/insert! conn
                   :conf.name
                   {:name name
                    :account_id id}))))

(defn upsert-tag
  "Upsert a tag row."
  [opts]
  {:pre [(map? opts)]}
  (let [{:keys [ds name description id user pass]} opts]
    (log/infof "updating tag with id %d" id)
    (with-open [conn (jdbc/get-connection ds)]
      (jdbc/execute! conn
                     [(sql-text :tag-merge)
                      id
                      name
                      description]))))

(defn select-account
  "Fetch an account by name."
  [opts]
  {:pre [(map? opts)]}
  (let [{:keys [ds path]} opts]
    (with-open [conn (jdbc/get-connection ds)]
      (jdbc/execute! conn
                     [(sql-text :select-account)
                      path]))))


(comment
  *e
  (require '[sys-loader.core :refer [sys-state]])
  (def data-source (-> @sys-state :sys/db :data-source))

  data-source
  (time
   (next-seq-val "CONF.ACCOUNT_SEQ" data-source))

  (-> (select-account {:ds data-source :path "a.b.c"}) first :ACCOUNT/USER_ID)

  (insert-name {:ds data-source :name "a.b.c" :id 64})

  (upsert-account {:ds data-source
                   :name "Sample"
                   :description "Sample for testing"
                   :id (next-seq-val "CONF.ACCOUNT_SEQ" data-source)
                   :user "foo"
                   :pass "barw"})

  (upsert-tag {:ds data-source
               :id (next-seq-val "CONF.TAG_SEQ" data-source)
               :name "test-tag"
               :description "test descr"})

  (time (upsert-account {:ds data-source
                         :name "Sample"
                         :description "Sample for testing"
                         :id (next-seq-val "CONF.ACCOUNT_SEQ" data-source)
                         :user "foo"
                         :pass "barw"}))

  ;;
  )
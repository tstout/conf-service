(ns conf-service.db-io
  (:require [next.jdbc :as jdbc]
            [next.jdbc.sql :as sql]
            [next.jdbc.result-set :as rs]
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
  ([seq-name ds]
   (log/infof "next-seq-val seq-name: %s" seq-name)
   (with-open [conn (jdbc/get-connection ds)]
     (-> conn
         (sql/query [(format "select next value for %s" seq-name)])
         first
         vals
         last)))
  ([opts]
   {:pre [(map? opts)]}
   (let [{:keys [ds seq-name]} opts]
     (next-seq-val seq-name ds))))

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
  (let [{:keys [ds path id]} opts]
    (log/infof "creating name entry '%s' with id %d" path id)
    (with-open [conn (jdbc/get-connection ds)]
      (sql/insert! conn
                   :conf.name
                   {:path path
                    :account_id id}))))

(defn new-account
  "Create a new account. Returns the id of the new account"
  [opts]
  {:pre [(map? opts)]}
  (let [account-id (next-seq-val (merge opts {:seq-name "conf.account_seq"}))]
    (upsert-account (merge opts {:id account-id}))
    account-id))

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


;; Note - jdbc.next has many options regarding the shape of result sets.
;; Currently using as-unqualified-lower-map as this suits my taste at the
;; moment. 
(defn select-account
  "Fetch an account by path."
  [opts]
  {:pre [(map? opts)]}
  (let [{:keys [ds path]} opts]
    (with-open [conn (jdbc/get-connection ds)]
      (jdbc/execute! conn
                     [(sql-text :select-account)
                      path]
                     {:builder-fn rs/as-unqualified-lower-maps}))))

(defn new-named-account
  "Create a new account with an associated name entry defining a 
   path which can be used to lookup the account."
  [opts]
  (let [account-id (new-account opts)]
    (insert-name (assoc opts :id account-id))
    account-id))


(comment
  *e
  (require '[sys-loader.core :refer [sys-state]])
  (def data-source (-> @sys-state :sys/db :data-source))

  (clojure.pprint/pprint (bean data-source))
  @sys-state
  data-source
  (time
   (next-seq-val "CONF.ACCOUNT_SEQ" data-source))

  (new-account {:ds data-source
                :name "boa-online"
                :description "checking/savings"
                :user "foo"
                :pass "bar"})

  (new-named-account {:ds data-source
                      :name "boa-online"
                      :path "/banking/boa/online"
                      :description "general checking and savings"
                      :user "foo2"
                      :pass "bar2"})

  data-source

  (bean data-source)

  (select-account {:ds data-source :path "a.b.c"})


  (-> (select-account {:ds data-source :path "a.b.c"})
      str)

  (time (-> (select-account {:ds data-source :path "a.b.c"}) first :ACCOUNT/USER_ID))


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
  ;;
  )
(ns conf-service.crypto
  (:require [conceal.core :refer [reveal conceal mk-opts key-from-env]]
            [taoensso.timbre :as log]))

;; See https://github.com/tstout/conceal for more info.

(defn encrypt [txt]
  (log/infof "ecrypt: %s" txt)
  (->> (key-from-env)
       (mk-opts txt)
       conceal))

(defn decrypt [txt]
  (-> txt
      (mk-opts (key-from-env))
      reveal))

(defn encrypt-account [accnt encrypt?]
  {:pre [(map? accnt)]}
  (if encrypt?
    (merge accnt {:user (encrypt (:user accnt))
                  :pass (encrypt (:pass accnt))})
    accnt))

(defn decrypt-account [accnt decrypt?]
  (if decrypt?
    (merge accnt {:user_id (decrypt (:user_id accnt))
                  :pass (decrypt (:pass accnt))})
    accnt))

(comment
  *e 
  (encrypt-account {:user "hello" :pass "goodbye"} false)
  (encrypt-account {:user "hello" :pass "goodbye"} true)

  (def e-accnt (encrypt-account {:user "hello" :pass "goodbye"} true))
  e-accnt

  (decrypt-account e-accnt true)
  (decrypt-account e-accnt false)
  ;;
  )
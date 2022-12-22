(ns conf-service.spec
  (:require [clojure.spec.alpha :as s]))

(s/def ::name (s/and string? #(>= 255 (.length %))))
(s/def ::path (s/and string? #(>= 255 (.length %))))
(s/def ::user (s/and string? #(>= 255 (.length %))))
(s/def ::pass (s/and string? #(>= 255 (.length %))))

(s/def ::account (s/keys :req-un [::name
                                  ::path
                                  ::user
                                  ::pass]))

(comment

  (s/valid? ::account {})
  (s/valid? ::account {:name "hello"
                       :path "a.b.c"
                       :user "foo"
                       :pass "secret"})

  (s/conform ::account {:name "hello"
                        :path "a.b.c"
                        :user "foo"
                        :boo []
                        :pass "secret"})
  (s/& ::account {})
  (s/explain ::account {::name "hello"})
  (s/explain-str ::account {::name ""})
  (s/describe ::account)
  ;;
  )
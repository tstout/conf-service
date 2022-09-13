(ns conf-service.db-io-test
  (:require
   [clojure.test :refer [deftest is]]
   [conf-service.db-io :as subject]))

(deftest conf-service.db-io-test
  (is (= true
         (subject/foo))))
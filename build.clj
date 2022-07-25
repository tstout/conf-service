(ns build
  (:require [clojure.tools.build.api :refer [delete
                                             compile-clj
                                             git-count-revs
                                             create-basis
                                             copy-dir uber]]))

(def version (format "1.0.%s" (git-count-revs nil)))

(def class-dir "target/classes")

(def basis (create-basis {:project "deps.edn"}))

(def uber-file (format "target/%s-%s.jar" "conf-service" version))

(defn clean [_] (delete {:path "target"}))

(defn uberjar [_]
  (clean nil)
  (copy-dir {:target-dir class-dir, :src-dirs ["src" "resources"]})
  (compile-clj {:basis basis, :class-dir class-dir, :src-dirs ["src"]})
  (uber
   {:basis basis,
    :uber-file uber-file,
    :class-dir class-dir,
    :main (symbol "conf-service.core")}))

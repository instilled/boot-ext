(ns instilled.boot.task
  (:require
   [instilled.boot.keybase :as keybase]
   [clojure.java.io        :as io]
   ;;[boot.task.built-in     :refer [push]]
   [boot.core              :as core]
   [boot.gpg               :as gpg])
  (:import
   [java.io File]))

(core/deftask sign-with-keybase
  "Rebind `boot.gpg/sign-jar` to `instilled.boot.keybase/sign-jar`."
  []
  (alter-var-root #'gpg/sign-jar (fn [v r] r) keybase/sign-jar)
  identity)

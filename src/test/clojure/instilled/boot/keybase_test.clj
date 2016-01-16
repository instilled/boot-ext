(ns instilled.boot.keybase-test
  (:require
   [instilled.boot.keybase :refer :all]
   [clojure.java.io        :as    io]
   [clojure.test           :refer :all])
  (:import
   [java.io
    File]))

(defn tmp-file
  [^String n]
  (let [f (File/createTempFile "keybase-test-" n)]
    (spit f "hello")
    (.getAbsolutePath f)))

(deftest ^:integration test-keybase-ops
  (testing "signing works"
    (let [f (tmp-file "file1")
          encf (str f ".asc")]
      (apply sign* (signing-args f {}))
      (is (.exists (io/file encf)))
      (is true))))

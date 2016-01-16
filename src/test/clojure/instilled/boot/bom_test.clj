(ns instilled.boot.bom-test
  (:require
   [instilled.boot.bom :refer :all]
   [clojure.test       :refer :all]))

(deftest test-bom
  (testing "resolves correctly"
    (System/setProperty "boot-ext.bom" "src/test/resources/test-bom.edn")
    (is (= [['org.clojure/clojure "1.7"
             :exclusions nil
             :scope "compile"]
            ['adzerk/boot-test "1.0.5"
             :exclusions ['org.clojure/clojure]
             :scope "test"]]
           (bom
             [['org.clojure/clojure]
              ['adzerk/boot-test]])))))

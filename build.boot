(def artifact
  {:project
   'boot-ext/boot-ext

   :version
   "0.0.1-SNAPSHOT"

   :description
   "Boot extensions."

   :url
   "https://github.com/instilled/boot-ext"})

(set-env!
  :source-paths
  #{"src/main/clojure"}

  :resource-paths
  #{"src/main/clojure"}

  :dependencies
  '[[org.clojure/clojure                  "1.7.0"
     :scope "provided"]
    [clj-jgit/clj-jgit                    "0.8.8"]

    ;; test dependencies
    [adzerk/boot-test                     "1.0.4"
     :scope "test"]])

(task-options!
  pom artifact)

(require
  '[adzerk.boot-test :refer :all])

(deftask remove-ignored
  []
  (sift
    :invert true
    :include #{#".*\.swp"
               #".gitkeep"}))

(deftask dev
  "Profile setup for running tests."
  []
  (merge-env!
    :source-paths
    #{"src/test/clojure"}

    :resource-paths
    #{"src/test/resources"})
  identity)

(deftask testc
  []
  (comp
    (dev)
    (watch)
    (speak)
    (test)))

(deftask tests
  []
  (comp
    (dev)
    (speak)
    (test)))

(deftask build
  []
  ;; Only :resource-paths will be added to the final
  ;; aritfact. Thus we need to merge :source-paths
  ;; into :resources-paths.
  (merge-env!
    :resource-paths
    #{"src/main/cloujure"})
  (comp
    (remove-ignored)
    (pom)
    (jar)
    (install)))

(def artifact
  {:project
   'instilled/boot-ext

   :version
   "0.0.1-SNAPSHOT"

   :description
   "Boot extensions."

   :url
   "https://github.com/instilled/boot-ext"})

(def boot-version "2.5.5")

(set-env!
  :source-paths
  #{"src/main/clojure"}

  :resource-paths
  #{"src/main/clojure"}

  :dependencies
  `[[org.clojure/clojure                  "1.7.0"
     :scope "provided"]

    [clj-jgit/clj-jgit                    "0.8.8"
     :scope "provided"]

    [boot/core                            ~boot-version
     :scope "provided"]
    [boot/base                            ~boot-version
     :scope "provided"]
    [boot/pod                             ~boot-version
     :scope "provided"]

    ;; test dependencies
    [adzerk/boot-test                     "1.1.0"
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

(replace-task!
 [t test] (fn [& xs] (comp (dev) (apply t xs))))

(deftask test-repeatedly
  []
  (comp
   (watch)
   (speak)
   (test)))

(deftask build
  []
  ;; Only :resource-paths will be added to the final
  ;; aritfact. Thus we need to merge :source-paths
  ;; into :resources-paths.
  (merge-env!
    :resource-paths
    #{"src/main/clojure"})
  (comp
    (remove-ignored)
    (pom)
    (jar)
    (target)
    (install)))

(deftask deploy
  []
  (push
   ;;:gpg-sign true
   :repo "clojars"
   ;;:ensure-branch "master"
   ;;:ensure-clean true
   ;;:ensure-release true
   ;;:ensure-snapshot true
   ;;:ensure-tag ""
   ;;:ensure-version ""
   ))


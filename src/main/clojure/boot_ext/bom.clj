(ns boot-ext.bom
  (:require
    [clj-jgit.porcelain :as git]
    [clojure.edn        :as edn]
    [clojure.java.io    :as io]))

(defn bom
  ([deps]
   (bom
     (or (System/getProperty "boot-ext.bom")
         (System/getenv "BOOT_EXT_BOM"))
     deps))
  ([bom-path artifacts]
   (letfn [(build-artifact-map
             [bom-artifacts acc dep]
             (let [[a av] dep]
               (if-let [{:keys [version scope exclusions allow-override]}
                        (get bom-artifacts a)]
                 (conj
                   acc
                   [a version
                    :exclusions exclusions
                    :scope (or scope "compile")])
                 (do
                   (when-not (and av)
                     (throw
                       (IllegalStateException.
                         (str "Dependency not in bom and no version declared: " a))))
                   (println "Warn: dependency not declared in bom: " a)
                   (conj acc dep)))))]
     (let [bom-path (io/file bom-path)]
       (when-not (and bom-path (.isFile bom-path))
         (throw (IllegalStateException.
                  (str "Invalid path for boot-ext.bom or BOOT_EXT_BOM: " bom-path))))
       (let [bom-artifacts (edn/read-string (slurp bom-path))]
         (->> (reduce
                (partial build-artifact-map bom-artifacts)
                []
                artifacts)
              (distinct)))))))

;; This is how source deps could look like
;;(deps
;;  [['org.clojure/clojure]
;;   ['instilled/confucius#{3x54fu,master,tag-0.0.1}
;;    :src #{"src/main/clojure" "src/main/java"}
;;    :test #{"src/test/clojure" "src/test/java"}]])


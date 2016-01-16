(ns instilled.boot.keybase
  (:require
   [clojure.java.io    :as io]
   [clojure.java.shell :as shell]
   [boot.pod           :as pod]
   [boot.util          :as util])
  (:import [java.io StringReader File]))


;; Copied from `boot.pod.gpg` and slightly modified.
;; We should somehow extend boot to provide a pluggable
;; artifact signing mechanism. ...I'll rise an issue.

(defn signing-program
  "Lookup the gpg program to use, defaulting to 'keybase'"
  []
  (or (boot.App/config "BOOT_SIGNING_COMMAND") "keybase"))

(defn- ^{:boot/from :technomancy/leiningen} get-english-env []
  "Returns environment variables as a map with clojure keywords and LANGUAGE set to 'en'"
  (let [env (System/getenv)
        keywords (map #(keyword %) (keys env))]
    (merge (zipmap keywords (vals env))
           {:LANGUAGE "en"})))

(defn signing-args
  "Produce Keybase arguments for signing a file."
  [file {:keys [gpg-key gpg-passphrase] :as opts}]
  (let [file-enc (str file ".asc")]
    `["pgp" "sign" "-i" ~file "-o" ~file-enc]))

(defn keybase
  "Shells out to (gpg-program) with the given arguments"
  [& args]
  (let [env (get-english-env)]
    (try
      (shell/with-sh-env env
        (apply shell/sh (signing-program) args))
      (catch java.io.IOException e
        {:exit 1 :err (.getMessage e)}))))

(defn ^{:boot/from :technomancy/leiningen} sign
  "Create a detached signature and return the signature file name."
  [file opts]
  (let [{:keys [err exit]} (apply keybase (signing-args file opts))]
    (when-not (zero? exit)
      (util/fail (str "Could not sign " file "\n" err
                      "\n\nIf you don't expect people to need to verify the "
                      "authorship of your jar, don't set :gpg-sign option of push task to true.\n")))
    (str file ".asc")))

(defn sign-jar
  [outdir jarfile pompath opts]
  (shell/with-sh-dir
    outdir
    (let [jarname (.getName jarfile)
          jarout  (io/file outdir (str jarname ".asc"))
          pomfile (doto (File/createTempFile "pom" ".xml")
                    (.deleteOnExit)
                    (spit (pod/pom-xml jarfile pompath)))
          pomout  (io/file outdir (.replaceAll jarname "\\.jar$" ".pom.asc"))
          sign-it #(slurp (sign (.getPath %) opts))]
      (spit pomout (sign-it pomfile))
      (spit jarout (sign-it jarfile))
      {[:extension "jar.asc"] (.getPath jarout)
       [:extension "pom.asc"] (.getPath pomout)})))

#_(defn decrypt
  "Use gpg to decrypt a file -- returns string contents of file."
  [file]
  (let [path (.getPath (io/file file))
        {:keys [out err exit]} (gpg "--quiet" "--batch" "--decrypt" "--" path)]
    (assert (zero? exit) err)
    out))

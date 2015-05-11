(ns cljrpn.options
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.string :as s]))

(defn expand-user
  "Expand paths which contain ~ to indicate the home directory
  of the user."
  [fpath]
  (s/replace fpath #"^~" (System/getProperty "user.home")))

(def config-paths
  "Default locations to search for cljrpnrc file"
  ["./.cljrpnrc"
   "./cljrpn.rc"
   "~/.cljrpnrc"
   ".config/cljrpn/rc"])

(defn load-config-file
  "Load a configuration file from the given path.
  Files are in edm format."
  [^String fpath]
  (let [f (io/file (expand-user fpath))]
    (when (.exists f)
      (edn/read-string (slurp (.getCanonicalPath f))))))

(defn get-config
  "Load the first configuration file that exists from a
  list of paths."
  [& custom-paths]
  (let [paths (concat
                (remove nil? custom-paths)
                config-paths)]
    (or (some load-config-file paths) {})))

(def defaults
  (-> "cljrpnrc" io/resource slurp edn/read-string))

(ns cljrpn.options
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.string :as s]))

(defn expand-user [fpath]
  (s/replace fpath #"^~" (System/getProperty "user.home")))

(def config-paths
  ["./.cljrpnrc"
   "./cljrpn.rc"
   "~/.cljrpnrc"
   ".config/cljrpn/rc"])

(defn load-config
  ([]
   (some load-config config-paths))
  ([fpath]
   (let [f (io/file (expand-user fpath))]
     (when (.exists f)
       (edn/read-string (slurp (.getCanonicalPath f)))))))

(defn load-config-safe [& fpaths]
  (or (apply load-config fpaths)
      {}))

(ns cljrpn.test.fixtures)

(defn disconnect [f]
  (binding [cljrpn.stack/pushf identity]
    (f)))


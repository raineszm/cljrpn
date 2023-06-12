(ns cljrpn.test-helpers)

(defn stack= [state coll]
  {:pre (coll? coll)}
  (= (:stack state) coll))

(defn register= [state reg value]
  (= ((:registers state) reg)
     value))

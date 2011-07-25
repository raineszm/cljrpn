(ns rpn.registers)

(def *registers* (atom {}))

(defn register? [r]
  (re-matches #"\p{Alpha}" r))

(defn set-register [k v]
  (swap! *registers* assoc (keyword k) v))

(defn get-register [k]
  ((keyword k) @*registers*))

(defn used-registers []
  (sort (filter second @*registers*)))


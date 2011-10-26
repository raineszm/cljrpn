(ns cljrpn.registers
  "Contains the code for handling single value registers")

(def ^:dynamic *registers* (atom {}))

(defn register? [r]
  "Determine whether the provided string is a valid register name"
  (re-matches #"\p{Alpha}" r))

(defn set-register [k v]
  "Set the register named k to the value v"
  (swap! *registers* assoc (keyword k) v))

(defn get-register [k]
  "Get the contents of the register named k"
  ((keyword k) @*registers*))

(defn used-registers []
  "Return a list of of register name (as keywords) with non nil values"
  (sort (filter val @*registers*)))


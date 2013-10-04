(ns cljrpn.registers
  "Contains the code for handling single value registers")

(defn register? [r]
  "Determine whether the provided string is a valid register name"
  (re-matches #"\p{Alpha}" r))

(defn set-register [state k v]
  "Set the register named _k_ to the value _v_"
  (assoc-in state [:registers (keyword k)] v))

(defn get-register [state k]
  "Get the contents of the register named _k_"
  (get-in state [:registers (keyword k)]))

(defn used-registers [state]
  "Return a list of of register name (as keywords) with non nil values"
  (sort (filter val (:registers state))))

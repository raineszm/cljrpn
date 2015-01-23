(ns cljrpn.registers
  "Contains the code for handling single value registers")

(defn register?
  "Determine whether the provided string is a valid register name"
  [r]
  (re-matches #"\p{Alpha}" r))

(defn set-register
  "Set the register named _k_ to the value _v_"
  [state k v]
  (assoc-in state [:registers (keyword k)] v))

(defn get-register
  "Get the contents of the register named _k_"
  [state k]
  (get-in state [:registers (keyword k)]))

(defn used-registers
  "Return a list of of register name (as keywords) with non nil values"
  [state]
  (sort (filter val (:registers state))))

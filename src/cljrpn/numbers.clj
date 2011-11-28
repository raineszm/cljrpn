(ns cljrpn.numbers
  "Tools for parsing numerical input.")

(defn num? [tok]
  "Determines if the string tok represents a valid decimal number"
  (re-matches #"-?((\d+\.?\d*)|(\d*\.?\d+))(e-?\d+)?" tok))

(defn process-num [stack tok]
  "Parses a number and pushes it to the stack"
  (conj stack (Float/parseFloat tok)))

(defn binary? [tok]
  "Determines if the string tok represents a binary number"
  (re-matches #"[01]+" tok))

(defn hex? [tok]
  "Determines if the string tok represents a hex number"
  (re-matches #"\p{XDigit}+" tok))

(defn with-base [b tok]
  "Parses the string tok as a base b integer."
  (float (Long/parseLong tok b)))

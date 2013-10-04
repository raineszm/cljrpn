(ns cljrpn.numbers
  "Tools for parsing numerical input."
  (:use cljrpn.state))

(defn num? [tok]
  "Determines if the string _tok_ represents a valid decimal number"
  (re-matches #"-?((\d+\.?\d*)|(\d*\.?\d+))(e-?\d+)?" tok))

(defn process-num [state tok]
  "Parses a number and pushes it to the stack"
  (pushf state (Float/parseFloat tok)))

(defn binary? [tok]
  "Determines if the string _tok_ represents a binary number"
  (re-matches #"[01]+" tok))

(defn hex? [tok]
  "Determines if the string _tok_ represents a hex number"
  (re-matches #"\p{XDigit}+" tok))

(defn with-base [b tok]
  "Parses the string _tok_ as a base b integer."
  (float (Long/parseLong tok b)))

(ns cljrpn.numbers
  "Tools for parsing numerical input."
  (:require [cljrpn.state :refer [pushf]]))

(defn num?
  "Determines if the string _tok_ represents a valid decimal number"
  [tok]
  (re-matches #"-?((\d+\.?\d*)|(\d*\.?\d+))(e-?\d+)?" tok))

(defn process-num
  "Parses a number and pushes it to the stack"
  [state tok]
  (pushf state (Float/parseFloat tok)))

(defn binary?
  "Determines if the string _tok_ represents a binary number"
  [tok]
  (re-matches #"[01]+" tok))

(defn hex?
  "Determines if the string _tok_ represents a hex number"
  [tok]
  (re-matches #"\p{XDigit}+" tok))

(defn with-base
  "Parses the string _tok_ as a base b integer."
  [b tok]
  (float (Long/parseLong tok b)))

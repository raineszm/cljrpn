(ns rpn.numbers
  "Tools for parsing numerical input."
  (:use [rpn.stack :only [pushf]]))

(defn num? [tok]
  "Determines if the string tok represents a valid decimal number"
  (re-matches #"(\d+\.?\d*)|(\d*\.?\d+)" tok))

(defn process-num [tok]
  "Parses a number and pushes it to the stack"
  (pushf (Float/parseFloat tok)))

(defn binary? [tok]
  "Determines if the string tok represents a binary number"
  (re-matches #"[01]+" tok))

(defn hex? [tok]
  "Determines if the string tok represents a hex number"
  (re-matches #"\p{XDigit}+" tok))

(defn with-base [b tok]
  "Parses the string tok as a base b integer."
  (float (Long/parseLong tok b)))

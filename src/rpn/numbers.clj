(ns rpn.numbers
  (:use [rpn.stack :only [pushf]]))

(defn num? [tok]
  (re-matches #"(\d+\.?\d*)|(\d*\.?\d+)" tok))

(defn process-num [tok]
  (pushf (Float/parseFloat tok)))

(defn hex? [tok]
  (re-matches #"\p{XDigit}+" tok))

(defn as-hex [tok]
  (float (Long/parseLong tok 16)))
